/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Settings;
import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;

/**
 *
 * @author Fritsch
 */
public class GrayscaleFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Settings.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += 3) {
                int b = pixels[pixel] & 0xFF;
                int g = pixels[pixel + 1] & 0xFF;
                int r = pixels[pixel + 2] & 0xFF;
                
                //Formula: https://en.wikipedia.org/wiki/Grayscale
                int grayValue = (int) (r * 0.2126 + g * 0.7152 + b * 0.0722);
                proceedImage.setRGB(col, row, (grayValue ) << 16 | (grayValue ) << 8 | (grayValue ));
                
                col++;
                if (col == image.getWidth()) {
                    col = 0;
                    row++;
                }
            }
            return proceedImage;
        } else {
            return image;
        }
    }

    @Override
    public ImageIcon getPreview()
    {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString()
    {
        return "Grayscale";
    }
}
