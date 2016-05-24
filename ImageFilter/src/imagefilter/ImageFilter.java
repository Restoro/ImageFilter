/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import imagefilter.listener.Controller;
import imagefilter.model.Model;
import imagefilter.view.MainFrame;
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
        drawWindow();
    }
    
    private static void drawWindow() {
        Model model = new Model();
        MainFrame frame = new MainFrame(400,400);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Controller ctrl = new Controller(model, frame);
        ctrl.generateButtons();
    }
    
}
