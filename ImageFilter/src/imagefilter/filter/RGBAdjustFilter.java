/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Constants;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;

/**
 *
 * @author Hochrathner
 */
public class RGBAdjustFilter implements FilterInterface {
    
    private final Setting[] settings;
    
    private BufferedImage preview;
    
    public RGBAdjustFilter() {
        settings = new Setting[3];
        settings[0] = (new Setting("Red",0,200,50));
        settings[1] = (new Setting("Green",0,200,50));
        settings[2] = (new Setting("Blue",0,200,50));
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {
        //parameter  
        float rFactor = settings[0].getCurValue()/100f;
        float gFactor = settings[1].getCurValue()/100f;
        float bFactor = settings[2].getCurValue()/100f;

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            for (int pixel = 0; pixel < pixels.length; pixel += 3) {
                int r = pixels[pixel + 2] & 0xFF;
                int g = pixels[pixel + 1] & 0xFF;
                int b = pixels[pixel] & 0xFF;

                r = Tools.boundaryCheck(r * rFactor);
                g = Tools.boundaryCheck(g * gFactor);
                b = Tools.boundaryCheck(b * bFactor);

                outPixels[pixel+2] = (byte) (r & 0xFF); 
                outPixels[pixel+1] = (byte) (g & 0xFF);
                outPixels[pixel] = (byte) (b & 0xFF);
            }
            return proceedImage;
        }
        return image;
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public void setPreview(BufferedImage preview)
    {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "RGB Adjust";
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }

}
