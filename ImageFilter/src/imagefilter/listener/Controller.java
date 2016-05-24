/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.model.Model;
import imagefilter.view.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Fritsch
 */
public class Controller {

    private final Model model;
    private final MainFrame view;

    public Controller(Model model, MainFrame view) {
        this.model = model;
        this.view = view;

        view.addMenuOpenFileListener(new MenuAddFileListener());
    }

    public void generateButtons() {
        try {
            Class[] filters = Tools.getClasses("imagefilter.filter");
            for (Class filter : filters) {
                if (FilterInterface.class.isAssignableFrom(filter) && !FilterInterface.class.equals(filter)) {
                    JButton button = new JButton(filter.getSimpleName().replace("Filter", ""));
                    button.addActionListener(new AddFilterButtonListener((FilterInterface) filter.newInstance()));
                    view.addButton(button);
                    System.out.println(filter.getName());
                }
            }

        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class MenuAddFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    BufferedImage readImage = ImageIO.read(file);
                    model.setCurrentImage(readImage);
                    model.setReferenceImage(readImage);
                    view.setImageOfImagePanel(readImage);
                } catch (IOException ex) {
                    System.out.println("Exception - Could not load image");
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public class AddFilterButtonListener implements ActionListener {

        private final FilterInterface filter;

        public AddFilterButtonListener(FilterInterface filter) {
            this.filter = filter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedImage newImage = filter.processImage(model.getCurrentImage());
            model.setCurrentImage(newImage);
            view.setImageOfImagePanel(newImage);
        }

    }
}
