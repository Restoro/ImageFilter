/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.ConvolveOperation;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Fritsch
 */
public class EmbossFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {
        final double[][] filter = {{-1, -1, 0}, {-1, 0, 1}, {0, 1, 1}};
        return ConvolveOperation.processImage(image, filter, 1, 128);
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString() {
        return "Emboss";
    }

    @Override
    public Setting[] getSettings() {
        return null;
    }
}
