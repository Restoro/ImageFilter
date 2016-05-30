/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.model.Model;
import imagefilter.model.Model.FilterPair;
import imagefilter.model.Setting;
import imagefilter.model.SettingWithXOptions;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Fritsch
 */
public class MainFrame extends JFrame {

    private File file;
    private int filterCount;

    private JScrollPane scrpSettings;
    private ImagePanel imagePanel;
    private JMenuItem openFile;
    private JMenuItem reset;
    private JMenuItem saveDisplay;
    private JMenuItem saveLastFilter;
    private JMenuItem saveDisplayAs;
    private JMenuItem saveLastFilterAs;
    private JMenuItem plugins;
    private MenuHandler handler;
    private JList filterList;
    private Model model;

    private JPanel settingsPanel;

    public MainFrame() {
        createWindow();
    }

    public MainFrame(int width, int heigth, Model model) {
        this.setSize(width, heigth);
        this.model = model;
        createWindow();
        initModel();
    }

    private void createWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        handler = new MenuHandler();
        JFrame newFrame = new JFrame();
        newFrame.setLayout(new BorderLayout());

        setupMenu();

        imagePanel = new ImagePanel(model);
        this.add(imagePanel, BorderLayout.CENTER);

        this.add(new SelectFilterPanel(model), BorderLayout.SOUTH);

        setupFilterList();
        setupSettingsList();
    }

    private void setupSettingsList() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        scrpSettings = new JScrollPane(settingsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = settingsPanel.getPreferredSize();
        d.width = 200;
        scrpSettings.setPreferredSize(d);
        //d.width = 150;
        this.add(scrpSettings, BorderLayout.EAST);
    }

    private void updateSettings(FilterInterface filter) {
        
        if (filter.getSettings() != null) {
            for (int j = 0; j < filter.getSettings().length; j++) {
                Setting setting = filter.getSettings()[j];
                settingsPanel.add(Box.createRigidArea(new Dimension(200, 10)));
                JLabel name = new JLabel(setting.getName());
                settingsPanel.add(name);

                JSlider slider = Tools.getJSlider(setting.getMinValue(), setting.getMaxValue(), setting.getCurValue());
                slider.setName(j + "");
                slider.addChangeListener(new SettingsChangedHandler());
                Hashtable labelTable = new Hashtable();
                if (setting instanceof SettingWithXOptions) {
                    SettingWithXOptions settingOptions = (SettingWithXOptions) setting;
                    String[] optionNames = settingOptions.getOptionNames();
                    for (int i = 0; i < optionNames.length; i++) {
                        labelTable.put(i, new JLabel(optionNames[i]));
                    }
                    Tools.setTickSpacingOfJSlider(slider, 1);
                } else {

                    labelTable.put(setting.getMinValue(), new JLabel(String.valueOf(setting.getMinValue())));
                    labelTable.put(setting.getMaxValue(), new JLabel(String.valueOf(setting.getMaxValue())));
                    Tools.setTickSpacingOfJSlider(slider, (setting.getMaxValue() - setting.getMinValue()) / 4);

                }
                slider.setLabelTable(labelTable);
                settingsPanel.add(slider);
            }
        }
        settingsPanel.revalidate();
    }

    private void setupFilterList() {
        FilterListRenderer filterListRenderer = new FilterListRenderer();
        FilterListModel filterListModel = new FilterListModel(model, filterListRenderer);
        filterList = new FilterList(filterListModel);
        filterList.setCellRenderer(filterListRenderer);
        JScrollPane scrpList = new JScrollPane(filterList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = filterList.getPreferredSize();
        scrpList.setPreferredSize(d);
        this.add(scrpList, BorderLayout.WEST);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        openFile = new JMenuItem("Open File");
        openFile.addActionListener(handler);
        menuFile.add(openFile);

        reset = new JMenuItem("Reset Image");
        reset.addActionListener(handler);
        menuFile.add(reset);

        saveDisplay = new JMenuItem("Save Displayed Image");
        saveDisplay.setEnabled(false);
        saveDisplay.addActionListener(handler);
        menuFile.add(saveDisplay);

        saveLastFilter = new JMenuItem("Save Last Filtered Image");
        saveLastFilter.setEnabled(false);
        saveLastFilter.addActionListener(handler);
        menuFile.add(saveLastFilter);

        saveDisplayAs = new JMenuItem("Save Displayed Image As");
        saveDisplayAs.setEnabled(false);
        saveDisplayAs.addActionListener(handler);
        menuFile.add(saveDisplayAs);

        saveLastFilterAs = new JMenuItem("Save Last Filtered Image As");
        saveLastFilterAs.setEnabled(false);
        saveLastFilterAs.addActionListener(handler);
        menuFile.add(saveLastFilterAs);
        
        JMenu menuExtra = new JMenu("Extras");
        plugins = new JMenuItem("Plugins");
        plugins.addActionListener(handler);
        menuExtra.add(plugins);

        menuBar.add(menuFile);
        menuBar.add(menuExtra);

        this.setJMenuBar(menuBar);
    }

    private void initModel() {
        model.addApplyingFiltersChangedListener(new ApplyingFiltersChangedListener() {
            @Override
            public void addApplyingFilter(FilterInterface filter) {

                filterCount++;
                checkEnabled();
            }

            @Override
            public void removeApplyingFilter(int index) {
                filterCount--;
                checkEnabled();
            }

            @Override
            public void applyingFiltersChanged(Collection<FilterInterface> filters) {
                filterCount = filters.size();
                checkEnabled();
            }

            @Override
            public void startApplyingFilter(int index) {
            }

            @Override
            public void finished(FilterPair filterPair) {
                filterList.setSelectedIndex(model.getIndexOfFilterPair(filterPair));
            }

            private void checkEnabled() {
                boolean b = filterCount > 0;
                saveLastFilter.setEnabled(b);
                saveLastFilterAs.setEnabled(b);
            }
        });
        model.addDisplayImageChangedListener(filterPair
                -> {
                    boolean b = filterPair != null;
                    saveDisplay.setEnabled(b);
                    saveDisplayAs.setEnabled(b);
                    settingsPanel.removeAll();
                    settingsPanel.revalidate();
                    if (filterPair!=null&&filterPair.filter != null) {
                        updateSettings(filterPair.filter);
                    }
                });
    }

    public void addMenuOpenFileListener(ActionListener listener) {
        openFile.addActionListener(listener);
    }

    public void setImageOfImagePanel(BufferedImage image) {
        imagePanel.setImage(image);
    }

//    public void addFilterBtnListener(ActionListener listener) {
//        btnSearch.addActionListener(listener);
//    }
    public class SettingsChangedHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof JSlider) {
                JSlider sourceSlider = (JSlider) e.getSource();
                if (!sourceSlider.getValueIsAdjusting()) {
                    //Name of Slider = index
                    int index = Integer.valueOf(sourceSlider.getName());
                    Setting changeSetting = (model.getDisplayImage().filter.getSettings())[index];
                    changeSetting.setCurValue(sourceSlider.getValue());
                    model.setSetting();
                }
            }
        }

    }

    public class MenuHandler implements ActionListener {

        private final JFileChooser fc;

        public MenuHandler() {
            fc = new JFileChooser();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == openFile) {
                fc.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                fc.setMultiSelectionEnabled(false);
                int returnVal = fc.showOpenDialog(MainFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                    try {
                        model.setReferenceImage(ImageIO.read(file));
                    } catch (IOException ex) {
                        System.out.println("Exception - Could not load image");
                        System.out.println(ex.getMessage());
                    }
                }
            } else if (e.getSource() == reset) {
                model.setReferenceImage(model.getReferenceImage());
            } else if (e.getSource() == saveDisplay) {
                overrride(model.getDisplayImage().image);
            } else if (e.getSource() == saveLastFilter) {
                overrride(model.getCurrentImage());
            } else if (e.getSource() == saveDisplayAs) {
                saveAs(model.getDisplayImage().image);
            } else if (e.getSource() == saveLastFilterAs) {
                saveAs(model.getCurrentImage());
            } else if(e.getSource() == plugins)
            {
                new PluginsDialog(Paths.get("C:\\")).setVisible(true);
            }
        }

        private void overrride(BufferedImage image) {
            if (JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure to override the old Image", "Save", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                saveImage(image);
            }
        }

        private void saveAs(BufferedImage image) {
            if (fc.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                saveImage(image);
            }
        }

        private void saveImage(BufferedImage image) {
            try {
                if (!file.exists()) {
                    Files.createFile(Paths.get(file.getAbsolutePath()));
                }
                ImageIO.write(image, getFileExtension(file), file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Error while saving...operation canceled");
            }
        }

        private String getFileExtension(File file) {
            String fileName = file.getName();
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                return fileName.substring(i + 1);
            }
            return "";
        }

    }
}
