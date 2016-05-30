package imagefilter.view;

import imagefilter.filter.FilterInterface;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;

/**
 *
 * @author hoellinger
 */
public class PluginsDialog extends JDialog
{
    private Path pluginDirectory;
    private ListModel<FilterInterface> model;

    public PluginsDialog(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
        this.model = new DefaultListModel<>();
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
        lstFilter = new JList<>(model);

        btnAdd.addActionListener(a -> handleClick(a));
        btnRemove.addActionListener(a -> handleClick(a));
        btnCancel.addActionListener(a -> handleClick(a));
        btnApply.addActionListener(a -> handleClick(a));
        btnOk.addActionListener(a -> handleClick(a));
        btnBrowse.addActionListener(a -> handleClick(a));

        JScrollPane scrp = new JScrollPane();
        scrp.setViewportView(lstFilter);

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

    }

    public void setPluginDirectory(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
    }

    private void handleClick(ActionEvent a)
    {
        if(a.getSource() == btnAdd)
        {

        } else if(a.getSource() == btnRemove)
        {

        } else if(a.getSource() == btnBrowse)
        {

        } else if(a.getSource() == btnOk)
        {

        } else if(a.getSource() == btnApply)
        {

        } else if(a.getSource() == btnCancel)
        {

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
}
