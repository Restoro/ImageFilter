/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Settings;
import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Verena
 */
public class GammaFilter implements FilterInterface{
    
     @Override
    public BufferedImage processImage(BufferedImage image) {
        
        //parameter gamma 
        float gamma = Settings.GAMMA;
        
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Settings.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);
        
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16 & 0xFF);
                int g = (rgb >> 8 & 0xFF);
                int b = (rgb & 0xFF);
                
                r = (int) Math.round(255 * Math.pow(((float)r/255f), 1f/gamma));
                g = (int) Math.round(255 * Math.pow(((float)g/255f), 1f/gamma));
                b = (int) Math.round(255 * Math.pow(((float)b/255f), 1f/gamma));
         
                proceedImage.setRGB(x, y, r << 16 | g << 8 | b); 
            }
        }
        return proceedImage;
             
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString() {
        return "Gamma";
    }
    
    
}
