/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.model.Model;
import imagefilter.view.MainFrame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Fritsch
 */
public class ImageFilter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Model model = new Model();
        setFilters(model);
        drawWindow(model);
    }
    
    private static void drawWindow(Model model) {
        MainFrame frame = new MainFrame(400,400, model);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private static void setFilters(Model model)
    {
        try {
            Class[] filters = Tools.getClasses("imagefilter.filter");
            for (Class filter : filters) {
                if (FilterInterface.class.isAssignableFrom(filter) && !FilterInterface.class.equals(filter)) {
                    model.addFilter((FilterInterface) filter.newInstance());
                }
            }
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ImageFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
