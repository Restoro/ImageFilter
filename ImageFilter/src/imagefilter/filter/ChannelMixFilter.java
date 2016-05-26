/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
public class ChannelMixFilter implements FilterInterface
{

    @Override
    public BufferedImage processImage(BufferedImage image) {
        
        // needs to be adjustable
        // looks nice for pink images now ;)
        
        int width = image.getWidth();
        int height = image.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        
        image.getRGB(0, 0, width, height, inPixels, 0, width);
        
        for(int y = 0; y < height; y ++)
        {
            int yOffset = y * width;
            for(int x = 0; x < width; x ++)
            {
                int rgb = inPixels[yOffset + x];
                
                int r = rgb >> 16 & 0xff;
                int g = rgb >> 8 & 0xff;
                int b = rgb & 0xff;
                
                outPixels[yOffset + x] = (g/2 + b / 2) << 16 | g << 8 | b;
            }
        }
        
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        dest.setRGB(0, 0, width, height, outPixels, 0, width);
        
        return dest;
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString() {
        return "Channel Mix"; //To change body of generated methods, choose Tools | Templates.
    }
}
