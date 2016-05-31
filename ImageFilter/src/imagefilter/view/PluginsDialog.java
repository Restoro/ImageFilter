package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.FilterClassLoader;
import imagefilter.helper.Tools;
import imagefilter.model.Model;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author hoellinger
 */
public class PluginsDialog extends JDialog
{
    private static PluginsDialog dialog;

    private Path pluginDirectory;
    private final DefaultListModel<FilterInterface> listModel;
    private final List<FilterInterface> newFilter;
    private final List<FilterInterface> removedFilter;
    private final List<FilterInterface> installedFilters;
    private final Model model;

    private PluginsDialog(Model model)
    {
        this.pluginDirectory = Paths.get(Tools.getProperty("pluginDirectory"));
        this.listModel = new DefaultListModel<>();
        this.newFilter = new ArrayList<>();
        this.removedFilter = new ArrayList<>();
        this.installedFilters = new ArrayList<>();
        this.model = model;
        init();
        initPlugins();
    }

    private void init()
    {
        this.setModal(true);
        this.setSize(400, 500);

        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnCancel = new JButton("Cancel");
        btnApply = new JButton("Apply");
        btnOk = new JButton("OK");
        btnBrowse = new JButton("Browse");
        txtDirectory = new JTextField(pluginDirectory.toString());
        lstFilter = new JList<>(listModel);
        fc = new JFileChooser();

        btnAdd.addActionListener(a -> handleClick(a));
        btnRemove.addActionListener(a -> handleClick(a));
        btnCancel.addActionListener(a -> handleClick(a));
        btnApply.addActionListener(a -> handleClick(a));
        btnOk.addActionListener(a -> handleClick(a));
        btnBrowse.addActionListener(a -> handleClick(a));

        txtDirectory.setEditable(false);

        JScrollPane scrp = new JScrollPane();
        scrp.setViewportView(lstFilter);
        lstFilter.setCellRenderer(new PluginsListRenderer());

        JPanel pan = new JPanel();
        GroupLayout layout = new GroupLayout(pan);
        pan.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(txtDirectory)
                                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnApply, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                .addComponent(scrp, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnAdd, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRemove, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                .addComponent(btnBrowse, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnAdd)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnRemove)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(scrp, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnBrowse)
                                .addComponent(txtDirectory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnCancel)
                                .addComponent(btnApply)
                                .addComponent(btnOk))
                        .addContainerGap())
        );
        this.add(pan);
    }

    private void initPlugins()
    {
        for(FilterInterface filter : FilterClassLoader.getFilterClassLoader().getPluginFilters(pluginDirectory))
        {
            listModel.addElement(filter);
        }
    }

    public static void showPluginDialog(Model model)
    {
        if(dialog == null)
        {
            dialog = new PluginsDialog(model);
        }
        dialog.prepareShowing();
    }

    public void setPluginDirectory(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
    }

    private void handleClick(ActionEvent a)
    {
        if(a.getSource() == btnAdd)
        {
            SearchFilterDialog.show(fc, filter -> filterSelected(filter));
        } else if(a.getSource() == btnRemove)
        {
            FilterInterface fi = lstFilter.getSelectedValue();
            if(fi != null)
            {
                if(!newFilter.remove(fi))
                {
                    installedFilters.remove(fi);
                    removedFilter.add(fi);
                }
                listModel.remove(lstFilter.getSelectedIndex());
            }
        } else if(a.getSource() == btnBrowse)
        {
            fc.setFileFilter(null);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setSelectedFile(Paths.get(txtDirectory.getText()).toFile());
            int returnVal = fc.showOpenDialog(PluginsDialog.this);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                Path p = Paths.get(fc.getSelectedFile().getAbsolutePath());
                Path classes = p.resolve("class");
                Path imgs = p.resolve("img");
                try
                {
                    createNecessaryPluginFolder(imgs);
                    createNecessaryPluginFolder(classes);
                } catch(IOException ex)
                {
                    JOptionPane.showMessageDialog(this, "Directory cannot be selected");
                    return;
                }
                txtDirectory.setText(p.toString());
            }
        } else if(a.getSource() == btnOk)
        {
            closeOk();
        } else if(a.getSource() == btnApply)
        {
            apply();
        } else if(a.getSource() == btnCancel)
        {
            closeCancled();
        }
    }

    private void createNecessaryPluginFolder(Path p) throws IOException
    {
        if(!Files.exists(p))
        {
            Files.createDirectories(p);
        }
    }

    private void filterSelected(FilterInterface filter)
    {
        if(filter != null)
        {
            newFilter.add(filter);
            listModel.addElement(filter);
        }
    }

    private JList<FilterInterface> lstFilter;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnOk;
    private JButton btnCancel;
    private JButton btnApply;
    private JButton btnBrowse;
    private JTextField txtDirectory;
    private JFileChooser fc;

    private void prepareShowing()
    {
        txtDirectory.setText(pluginDirectory.toString());
        this.setVisible(true);
    }

    private void closeOk()
    {
        apply();
        close();
    }

    private void apply()
    {
        Path newDirectory = Paths.get(txtDirectory.getText());
        if(!newDirectory.equals(pluginDirectory))
        {
            pluginDirectory = newDirectory;
            Tools.writeProperty("pluginDirectory", txtDirectory.getText());
        }
        for(FilterInterface fi : removedFilter)
        {
            model.removeFilter(fi);
            try
            {
                removeFilter(fi, FilterClassLoader.getPathFromFilter(fi));
            } catch(IOException ex)
            {
                Logger.getLogger(PluginsDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(FilterInterface fi : newFilter)
        {
            model.addFilter(fi);
            try
            {
                addFilter(fi, FilterClassLoader.getPathFromFilter(fi));
            } catch(IOException ex)
            {
                Logger.getLogger(PluginsDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void closeCancled()
    {
        for(FilterInterface fi : newFilter)
        {
            listModel.removeElement(fi);
        }
        newFilter.clear();
        for(FilterInterface fi : removedFilter)
        {
            listModel.addElement(fi);
        }
        newFilter.clear();
        close();
    }

    private void close()
    {
        dispose();
    }

    private void addFilter(FilterInterface fi, Path pathFromFilter) throws IOException
    {
        String imgFileName = Tools.nameWithoutExtension(pathFromFilter) + ".jpg";
        Path img = pluginDirectory.resolve("img").resolve(imgFileName);
        ImageIO.write(Tools.fromImageIconToBufferedImage(fi.getPreview()), "jpg", Files.newOutputStream(img, StandardOpenOption.CREATE, StandardOpenOption.WRITE));

        Path _class = pluginDirectory.resolve("class").resolve(Tools.nameWithExtension(pathFromFilter));
        if(!Files.exists(_class))
        {
            Files.createFile(_class);
        }
        Files.copy(pathFromFilter, Files.newOutputStream(_class));
    }

    private void removeFilter(FilterInterface fi, Path path) throws IOException
    {
        Path _class = pluginDirectory.resolve("class").resolve(path.getFileName());
        Path img = pluginDirectory.resolve("img").resolve(Tools.nameWithoutExtension(path) + ".jpg");

        Files.deleteIfExists(_class);
        Files.deleteIfExists(img);
    }
}
