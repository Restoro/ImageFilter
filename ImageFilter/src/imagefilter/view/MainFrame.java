/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.model.Model;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Fritsch
 */
public class MainFrame extends JFrame
{
    private File file;
    private int filterCount;

    private JMenuBar menuBar;
    private JMenu menu;
    private JFrame mainFrame;
    private ImagePanel imagePanel;
    private JMenuItem openFile;
    private JMenuItem reset;
    private JMenuItem saveDisplay;
    private JMenuItem saveLastFilter;
    private JMenuItem saveDisplayAs;
    private JMenuItem saveLastFilterAs;
    private MenuHandler handler;
    private JList filterList;
    private Model model;

    public MainFrame()
    {
        createWindow();
    }

    public MainFrame(int width, int heigth, Model model)
    {
        this.setSize(width, heigth);
        this.model = model;
        createWindow();
        initModel();
    }

    private void createWindow()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        handler = new MenuHandler();
        JFrame newFrame = new JFrame();
        newFrame.setLayout(new BorderLayout());

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        openFile = new JMenuItem("Open File");
        openFile.addActionListener(handler);
        menu.add(openFile);

        reset = new JMenuItem("Reset Image");
        reset.addActionListener(handler);
        menu.add(reset);

        saveDisplay = new JMenuItem("Save Displayed Image");
        saveDisplay.setEnabled(false);
        saveDisplay.addActionListener(handler);
        menu.add(saveDisplay);

        saveLastFilter = new JMenuItem("Save Last Filtered Image");
        saveLastFilter.setEnabled(false);
        saveLastFilter.addActionListener(handler);
        menu.add(saveLastFilter);

        saveDisplayAs = new JMenuItem("Save Displayed Image As");
        saveDisplayAs.setEnabled(false);
        saveDisplayAs.addActionListener(handler);
        menu.add(saveDisplayAs);

        saveLastFilterAs = new JMenuItem("Save Last Filtered Image As");
        saveLastFilterAs.setEnabled(false);
        saveLastFilterAs.addActionListener(handler);
        menu.add(saveLastFilterAs);

        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        imagePanel = new ImagePanel(model);
        this.add(imagePanel, BorderLayout.CENTER);

        this.add(new SelectFilterPanel(model), BorderLayout.SOUTH);

        FilterListRenderer filterListRenderer = new FilterListRenderer();
        FilterListModel filterListModel = new FilterListModel(model, filterListRenderer);
        filterList = new FilterList(filterListModel);
        filterList.setCellRenderer(filterListRenderer);
        JScrollPane scrpList = new JScrollPane(filterList);
        this.add(scrpList, BorderLayout.WEST);

        mainFrame = this;
    }

    private void initModel()
    {
        model.addApplyingFiltersChangedListener(new ApplyingFiltersChangedListener()
        {
            @Override
            public void addApplyingFilter(FilterInterface filter)
            {
                filterCount++;
                checkEnabled();
            }

            @Override
            public void removeApplyingFilter(int index)
            {
                filterCount--;
                checkEnabled();
            }

            @Override
            public void applyingFiltersChanged(Collection<FilterInterface> filters)
            {
                filterCount = filters.size();
                checkEnabled();
            }

            @Override
            public void startApplyingFilter(int index)
            {
            }

            @Override
            public void finished()
            {
            }

            private void checkEnabled()
            {
                boolean b = filterCount > 0;
                saveLastFilter.setEnabled(b);
                saveLastFilterAs.setEnabled(b);
            }
        });
        model.addDisplayImageChangedListener(image
                -> 
                {
                    boolean b = image != null;
                    saveDisplay.setEnabled(b);
                    saveDisplayAs.setEnabled(b);
        });
    }

    public void addMenuOpenFileListener(ActionListener listener)
    {
        openFile.addActionListener(listener);
    }

    public void setImageOfImagePanel(BufferedImage image)
    {
        imagePanel.setImage(image);
    }

//    public void addFilterBtnListener(ActionListener listener) {
//        btnSearch.addActionListener(listener);
//    }
    public class MenuHandler implements ActionListener
    {

        private final JFileChooser fc;

        public MenuHandler()
        {
            fc = new JFileChooser();
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == openFile)
            {
                fc.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                fc.setMultiSelectionEnabled(false);
                int returnVal = fc.showOpenDialog(MainFrame.this);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    file = fc.getSelectedFile();
                    try
                    {
                        model.setReferenceImage(ImageIO.read(file));
                    } catch(IOException ex)
                    {
                        System.out.println("Exception - Could not load image");
                        System.out.println(ex.getMessage());
                    }
                }
            } else if(e.getSource() == reset)
            {
                model.setReferenceImage(model.getReferenceImage());
            } else if(e.getSource() == saveDisplay)
            {
                overrride(model.getDisplayImage());
            } else if(e.getSource() == saveLastFilter)
            {
                overrride(model.getCurrentImage());
            } else if(e.getSource() == saveDisplayAs)
            {
                saveAs(model.getDisplayImage());
            } else if(e.getSource() == saveLastFilterAs)
            {
                saveAs(model.getCurrentImage());
            }
        }

        private void overrride(BufferedImage image)
        {
            if(JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure to override the old Image", "Save", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                saveImage(image);
            }
        }

        private void saveAs(BufferedImage image)
        {
            if(fc.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
            {
                file = fc.getSelectedFile();
                saveImage(image);
            }
        }

        private void saveImage(BufferedImage image)
        {
            try
            {
                if(!file.exists())
                {
                    Files.createFile(Paths.get(file.getAbsolutePath()));
                }
                ImageIO.write(image, getFileExtension(file), file);
            } catch(IOException ex)
            {
                JOptionPane.showMessageDialog(MainFrame.this, "Error while saving...operation canceled");
                ex.printStackTrace();
            }
        }

        private String getFileExtension(File file)
        {
            String fileName = file.getName();
            int i = fileName.lastIndexOf('.');
            if(i > 0)
            {
                return fileName.substring(i + 1);
            }
            return "";
        }

    }
}
