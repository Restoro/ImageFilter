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
public class EdgeDetectionFilter implements FilterInterface {
    private ImageIcon preview;

    @Override
    public BufferedImage processImage(BufferedImage image) {
        final double[][] filter = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
        return ConvolveOperation.processImage(image, filter, 1, 0);
    }

    @Override
    public ImageIcon getPreview() {
        return preview;
    }

    @Override
    public void setPreview(ImageIcon preview)
    {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "Edge detection";
    }

    @Override
    public Setting[] getSettings() {
       return null;
    }
}
