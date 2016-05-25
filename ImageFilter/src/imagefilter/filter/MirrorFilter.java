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
public class MirrorFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {
        final int rate = 2;
        final int offset = (int) (image.getWidth() / (rate * 2));
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Settings.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            for (int rowY = 0; rowY < proceedImage.getHeight(); rowY += rate) {
            for (int colX = 0; colX  < proceedImage.getWidth(); colX += rate) {
                int index = (colX + (rowY * proceedImage.getWidth()))*3;
                int b = pixels[index] & 0xFF;
                int g = pixels[index+1] & 0xFF;
                int r = pixels[index+2] & 0xFF;

                //Set Subsampled Pixel
                proceedImage.setRGB((colX / rate)+offset, rowY / rate, ((r) << 16 | (g) << 8 | (b)));
                
                //Set Subsampled Mirror Pixel
                r *= 0.35;
                g *= 0.35;
                b *= 0.35;
                proceedImage.setRGB((colX / rate)+offset, proceedImage.getHeight()-1-(rowY/rate), ((r) << 16 | (g) << 8 | (b)));
            }
        }
            return proceedImage;
        } else {
            return image;
        }
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString() {
        return "Mirror";
    }

}
