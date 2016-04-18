/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import static java.lang.Void.TYPE;
import javax.swing.JPanel;

/**
 *
 * @author Fritsch
 */
public class ImagePanel extends JPanel{
    private BufferedImage toDrawImage;

    public ImagePanel() {
        this.toDrawImage = null;
    }
    
    public ImagePanel(BufferedImage toDrawImage) {
        this.toDrawImage = toDrawImage;
    }
    
    public void setImage(BufferedImage newImage)
    {
        this.toDrawImage = newImage;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(toDrawImage != null)
        {
            Image image = toDrawImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
            g.drawImage(image, 0, 0 , null);
        }
    }
}
