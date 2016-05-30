/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.FilterClassLoader;
import imagefilter.helper.Tools;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author hoellinger
 */
public class SearchFilterDialog extends JDialog
{
    private static SearchFilterDialog dialog;
    private static final ImageIcon TEST_ICON = new ImageIcon(Tools.getResource("preview.png"));
    private static final BufferedImage TEST_IMAGE = new BufferedImage(TEST_ICON.getIconWidth(), TEST_ICON.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);

    private JFileChooser fc;
    private FilterClassLoader cl;
    private BufferedImage img;
    
    static{
        setBufferedImage(TEST_IMAGE);
    }

    private SearchFilterDialog(JFileChooser fc)
    {
        this.fc = fc;
        this.cl = new FilterClassLoader();
        img = new BufferedImage(TEST_ICON.getIconWidth(), TEST_ICON.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
        init();
    }

    private void init()
    {
        setModal(true);
        setSize(400, 200);
        
        txtPath = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        panImg = new ImagePanel(null, TEST_IMAGE);
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        btnBrowse.setText("Browse");

        /*javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(panImg);
        panImg.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
        );*/

        btnCancel.setText("Cancel");

        btnOk.setText("OK");

        JPanel pan = new JPanel();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(pan);
        pan.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panImg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtPath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowse))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 164, Short.MAX_VALUE)
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panImg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap())
        );
        add(pan);
    }

    public static SearchFilterDialog show(JFileChooser fc)
    {
        if(dialog == null)
        {
            dialog = new SearchFilterDialog(fc);
        }
        dialog.setVisible(true);
        return dialog;
    }

    private void showFileChooser()
    {
        fc.setFileFilter(new FileNameExtensionFilter("Java Class File", ".class"));
        fc.setMultiSelectionEnabled(false);
        int returnVal = fc.showOpenDialog(SearchFilterDialog.this);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            txtPath.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void showFilter()
    {
        FilterInterface filterInterface = cl.getSingleFilterInterface(txtPath.getText());
        if(filterInterface == null)
        {
            failFilter("This was no filter!!");
        } else
        {
            try
            {
                img = filterInterface.processImage(TEST_IMAGE);
                filterInterface.setPreview(img);
                panImg.setImage(img);
            } catch(Throwable t)
            {
                failFilter("Filter was not executeable");
            }
        }
    }
    
    private void failFilter(String msg){
        
            JOptionPane.showMessageDialog(this, msg);
            txtPath.setText("");
    }
    
    private static void setBufferedImage(BufferedImage imgToSet)
    {
        imgToSet.getGraphics().drawImage(TEST_ICON.getImage(), 0, 0, null);
    }

    private JButton btnOk;
    private JButton btnCancel;
    private JButton btnBrowse;
    private JTextField txtPath;
    private ImagePanel panImg;
}
