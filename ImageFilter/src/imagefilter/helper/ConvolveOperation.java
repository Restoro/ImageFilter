/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 *
 * @author Fritsch
 */
//Quellen:
//https://en.wikipedia.org/wiki/Kernel_(image_processing)
//https://en.wikipedia.org/wiki/Convolution
public class ConvolveOperation {

    public static BufferedImage processImage(BufferedImage image, double[][] filter, double factor, double bias) {
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            factor = 1/factor;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int r = 0, b = 0, g = 0;
                    for (int filterY = 0; filterY < filter.length; filterY++) {
                        for (int filterX = 0; filterX < filter[0].length; filterX++) {

                            int imageX = (x - filter.length / 2 + filterX + image.getWidth()) % image.getWidth();
                            int imageY = (y - filter.length / 2 + filterY + image.getHeight()) % image.getHeight();

                            r += (pixels[(imageX + imageY * image.getWidth()) * 3 + 2] & 0xFF) * filter[filterY][filterX];
                            g += (pixels[(imageX + imageY * image.getWidth()) * 3 + 1] & 0xFF) * filter[filterY][filterX];
                            b += (pixels[(imageX + imageY * image.getWidth()) * 3] & 0xFF) * filter[filterY][filterX];
                        }
                    }
                    r = Tools.boundaryCheck(r * factor + bias);
                    g = Tools.boundaryCheck(g * factor + bias);
                    b = Tools.boundaryCheck(b * factor + bias);
                    outPixels[(x + y * proceedImage.getWidth())*3]=(byte) (b&0xFF);
                    outPixels[(x + y * proceedImage.getWidth())*3+1]=(byte) (g&0xFF);
                    outPixels[(x + y * proceedImage.getWidth())*3+2]=(byte) (r&0xFF);
                }
            }
            return proceedImage;
        } else {
            return image;
        }
    }
}
