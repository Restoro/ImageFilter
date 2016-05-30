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
public class SharpenFilter implements FilterInterface {

    private ImageIcon preview;
    @Override
    public BufferedImage processImage(BufferedImage image) {
        final double[][] filter = {{1, 4, 6, 4, 1}, {4, 16, 24, 16, 4}, {6, 24, -476, 24, 6}, {4, 16, 24, 16, 4}, {1, 4, 6, 4, 1}};
        return ConvolveOperation.processImage(image, filter, -256, 0);
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public void setPreview(ImageIcon preview)
    {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "Sharpen";
    }

    @Override
    public Setting[] getSettings() {
        return null;
    }
}
