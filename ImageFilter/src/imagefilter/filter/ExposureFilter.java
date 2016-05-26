/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
public class ExposureFilter implements FilterInterface
{

    @Override
    public BufferedImage processImage(BufferedImage image) {
        
        float val = 0.67f;
        float maxVal = 1;
        float scaleFactor = 2* val / maxVal;
        
        RescaleOp op = new RescaleOp(scaleFactor, 0, null);
        
        return op.filter(image, null);
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public String toString() {
        return "Exposure"; //To change body of generated methods, choose Tools | Templates.
    }
}
