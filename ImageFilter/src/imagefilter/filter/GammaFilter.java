/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Constants;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import imagefilter.model.SettingWithXOptions;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JSlider;

/**
 *
 * @author Verena
 */
public class GammaFilter implements FilterInterface{

    private final Setting[] settings;
    
    public GammaFilter() {
        settings = new Setting[1];
        settings[0] = (new Setting("Gamma",0,100,35));
    }
    
    
     @Override
    public BufferedImage processImage(BufferedImage image) {
        
        //parameter gamma 
        float gamma = settings[0].getCurValue()/100f;
        
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
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

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
