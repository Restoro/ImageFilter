/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

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
        MainFrame frame = new MainFrame(1920,1080);
        frame.setVisible(true);
        frame.addButton("Much Filter");
    }
    
}
