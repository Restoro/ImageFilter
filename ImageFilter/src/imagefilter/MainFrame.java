/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Fritsch
 */


public class MainFrame extends JFrame{
    ArrayList<JButton> filterButtons;
    JMenuBar menuBar;
    JMenu menu;
    JFrame mainFrame;
    JPanel buttonPanel;
    ImagePanel imagePanel;
    JMenuItem openFile;
    MenuHandler handler;

    public MainFrame() {
        createWindow();
    }
    
    public MainFrame(int width, int heigth)
    {
        this.setSize(width, heigth);
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
        handler.addListenerToMenuItem(openFile);
        menu.add(openFile);
        
        menuBar.add(menu);
        
        this.setJMenuBar(menuBar);
        
        imagePanel = new ImagePanel();
        this.add(imagePanel, BorderLayout.CENTER);
        
        buttonPanel = new JPanel(new FlowLayout());
        this.add(buttonPanel,BorderLayout.SOUTH);
        mainFrame = this;
    }
    
    public void addButton(String buttonText)
    {
        if(filterButtons == null)
            filterButtons = new ArrayList<>();
        
        JButton newButton = new JButton(buttonText);
        filterButtons.add(newButton);
        buttonPanel.add(newButton);
    }
    
    
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
                        imagePanel.setImage(readImage);
                    } catch (IOException ex) {
                        System.out.println("Exception - Could not load image");
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
        
    }
}
