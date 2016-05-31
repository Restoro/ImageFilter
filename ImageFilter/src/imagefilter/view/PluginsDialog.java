package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.listener.PluginChangesListener;
import imagefilter.model.PluginModel;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    private final PluginModel pluginModel;
    private PluginChangesListener listener;

    private PluginsDialog(PluginModel model)
    {
        this.pluginDirectory = Paths.get(Tools.getProperty("pluginDirectory"));
        this.listModel = new DefaultListModel<>();
        this.pluginModel = model;
        init();
        initPlugins();
    }

    private void init()
    {
        this.setModal(true);
        this.setSize(400, 500);
        setTitle("Plugins");

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
        listener = new PluginChangesListener()
        {
            @Override
            public void newPlugin(FilterInterface fi)
            {
                listModel.addElement(fi);
            }

            @Override
            public void removePlugin(FilterInterface fi)
            {
                listModel.removeElement(fi);
            }

            @Override
            public void changesCanceled(List<FilterInterface> plugins)
            {
            }
        };
    }

    public static void showPluginDialog(PluginModel model)
    {
        if(dialog == null)
        {
            dialog = new PluginsDialog(model);
        }
        dialog.prepareShowing();
    }

    private void handleClick(ActionEvent a)
    {
        if(a.getSource() == btnAdd)
        {
            SearchFilterDialog.show(fc, filter -> filterSelected(filter));
        } else if(a.getSource() == btnRemove)
        {
            pluginModel.removePlugin(lstFilter.getSelectedValue());
        } else if(a.getSource() == btnBrowse)
        {
            fc.setFileFilter(null);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setSelectedFile(Paths.get(txtDirectory.getText()).toFile());
            int returnVal = fc.showOpenDialog(PluginsDialog.this);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                if(pluginModel.setPluginDirectory(Paths.get(fc.getSelectedFile().getAbsolutePath())))
                {
                    JOptionPane.showMessageDialog(this, "You have to restart the programm");
                    System.exit(0);
                }
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

    private void filterSelected(FilterInterface filter)
    {
        pluginModel.newPlugin(filter);
    }

    private void prepareShowing()
    {
        listModel.removeAllElements();
        for(FilterInterface fi : pluginModel.addPluginChangesListener(listener))
        {
            listModel.addElement(fi);
        }
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
        pluginModel.applayChanges();
    }

    private void closeCancled()
    {
        pluginModel.cancleChanges();
        close();
    }

    private void close()
    {
        pluginModel.removePluginChangesListener(listener);
        dispose();
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
}
