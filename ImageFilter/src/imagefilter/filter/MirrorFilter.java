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
 * @author Fritsch
 */
public class MirrorFilter implements FilterInterface {

    private final Setting[] settings;
    public MirrorFilter() {
        settings = new Setting[1];
        settings[0] = new Setting("Mirror Darkness", 0, 100, 35);
    }

    
    @Override
    public BufferedImage processImage(BufferedImage image) {
        final int rate = 2;
        final int offset = (int) (image.getWidth() / (rate * 2));
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            //Subsample Image
            
            for (int rowY = 0; rowY < image.getHeight(); rowY += rate) {
                for (int colX = 0; colX < image.getWidth(); colX += rate) {
                    int index = (colX + (rowY * image.getWidth())) * 3;
                    int b = pixels[index] & 0xFF;
                    int g = pixels[index + 1] & 0xFF;
                    int r = pixels[index + 2] & 0xFF;

                    //Set Subsampled Pixel
                    proceedImage.setRGB((colX / rate) + offset, rowY / rate, ((r) << 16 | (g) << 8 | (b)));

                    //Set Subsampled Mirror Pixel
                    float mirrorDarkness = settings[0].getCurValue()/100f;
                    r *= mirrorDarkness;
                    g *= mirrorDarkness;
                    b *= mirrorDarkness;
                    proceedImage.setRGB((colX / rate) + offset, image.getHeight() - 1 - (rowY / rate), ((r) << 16 | (g) << 8 | (b)));
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

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
