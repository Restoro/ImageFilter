/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.model.Model;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Fritsch
 */


public class MainFrame extends JFrame{
    private ArrayList<JButton> filterButtons;
    private JMenuBar menuBar;
    private JMenu menu;
    private JFrame mainFrame;
    private ImagePanel imagePanel;
    private JMenuItem openFile;
    private MenuHandler handler;
    private DefaultListModel filterListModel;
    private JList filterList;
    private Model model;

    public MainFrame() {
        createWindow();
    }
    
    public MainFrame(int width, int heigth, Model model)
    {
        this.setSize(width, heigth);
        this.model = model;
        createWindow();
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
        
        menuBar.add(menu);
        
        this.setJMenuBar(menuBar);
        
        imagePanel = new ImagePanel(model);
        this.add(imagePanel, BorderLayout.CENTER);
        
        this.add(new SelectFilterPanel(model),BorderLayout.SOUTH);
        
        filterListModel = new DefaultListModel();
        filterListModel.addElement("Test1");
        filterListModel.addElement("Test2");
        filterList = new JList(filterListModel);
        
        filterList.setPrototypeCellValue("Dies ist ein String");
        this.add(filterList, BorderLayout.WEST);
        
        mainFrame = this;
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
    
    public class MenuHandler implements ActionListener{

        private final JFileChooser fc;
        private BufferedImage readImage;
        
        public MenuHandler() {
            fc = new JFileChooser();
        }

        public void addListenerToMenuItem(JMenuItem item)
        {
            item.addActionListener(this);
        }
        
        public BufferedImage getImage()
        {
            return readImage;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == openFile)
            {
                fc.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
                fc.setMultiSelectionEnabled(false);
                int returnVal = fc.showOpenDialog(mainFrame);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();
                    try {
                        readImage = ImageIO.read(file);
                        model.setReferenceImage(readImage);
                    } catch (IOException ex) {
                        System.out.println("Exception - Could not load image");
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
        
        
    }
}
