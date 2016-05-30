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
import javax.swing.JSlider;

/**
 *
 * @author Hochrathner
 */
public class GrayFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] outPixels =((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            for (int pixel = 0; pixel < pixels.length; pixel += 3) {
                
                int r = pixels[pixel + 2] & 0xFF;
                int g = pixels[pixel + 1] & 0xFF;
                int b = pixels[pixel] & 0xFF;

                //http://www.jhlabs.com/ip/filters/GrayFilter.html
                r = Tools.boundaryCheck(Math.round((255 + r) / 2));
                g = Tools.boundaryCheck(Math.round((255 + g) / 2));
                b = Tools.boundaryCheck(Math.round((255 + b) / 2));
                
                outPixels[pixel + 2] = (byte) (r & 0xFF);
                outPixels[pixel + 1] = (byte) (g & 0xFF);
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
    public String toString() {
        return "Gray";
    }

    @Override
    public Setting[] getSettings() {
        return null;
    }
}
