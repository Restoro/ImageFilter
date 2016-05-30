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
import javax.swing.ImageIcon;
import javax.swing.JSlider;

/**
 *
 * @author Verena
 */
public class GrayFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);

                int r = Math.round((255 + (rgb >> 16 & 0xFF)) / 2);
                int g = Math.round((255 + (rgb >> 8 & 0xFF)) / 2);
                int b = Math.round((255 + (rgb & 0xFF)) / 2);

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

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
        return "Gray";
    }

    @Override
    public Setting[] getSettings() {
        return null;
    }
}
