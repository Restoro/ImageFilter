/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Settings;
import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;

/**
 *
 * @author Verena
 */
public class InvertFilter implements FilterInterface{

    @Override
    public BufferedImage processImage(BufferedImage image) {
        
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Settings.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);
        
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                int rgb = image.getRGB(x, y);
                int r = 255 - rgb >>16 & 0xFF;
                int g = 255 - rgb >> 8 & 0xFF;
                int b = 255 - rgb & 0xFF;
                proceedImage.setRGB(x, y, r << 16 | g << 8 | b); 
            }
        }
        return proceedImage;
             
    }
                
                
}
