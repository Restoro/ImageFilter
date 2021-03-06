/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Constants;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import imagefilter.model.SettingWith2Options;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;

/**
 *
 * @author Fritsch
 */
public class MirrorFilter implements FilterInterface {

    private final Setting[] settings;
    private ImageIcon preview;
    public MirrorFilter() {
        settings = new Setting[2];
        settings[0] = new SettingWith2Options("Mirror Horizontal", 1);
        settings[1] = new Setting("Mirror Darkness", 0, 100, 35);
    }

    
    @Override
    public BufferedImage processImage(BufferedImage image) {
        final int rate = 2;
        final int offsetX = settings[0].getCurValue()==1?((int) (image.getWidth() / (rate * 2))):0;
        final int offsetY = settings[0].getCurValue()==1?0:(int) (image.getHeight()/ (rate * 2));
        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            //Subsample Image
            
            for (int rowY = 0; rowY < image.getHeight(); rowY += rate) {
                for (int colX = 0; colX < image.getWidth(); colX += rate) {
                    int index = (colX + (rowY * image.getWidth())) * 3;
                    int b = pixels[index] & 0xFF;
                    int g = pixels[index + 1] & 0xFF;
                    int r = pixels[index + 2] & 0xFF;

                    //Set Subsampled Pixel
                    index = (((colX / rate)+offsetX)+(rowY/rate+offsetY)*proceedImage.getWidth())*3;
                    outPixels[index] = (byte) (b&0xff);
                    outPixels[index+1] = (byte) (g&0xff);
                    outPixels[index+2] = (byte) (r&0xff);

                    //Set Subsampled Mirror Pixel
                    float mirrorDarkness = settings[1].getCurValue()/100f;
                    r *= mirrorDarkness;
                    g *= mirrorDarkness;
                    b *= mirrorDarkness;
                    
                    if(settings[0].getCurValue() == 1)
                        index = (((colX / rate)+offsetX)+(image.getHeight() - 1 - (rowY / rate))*proceedImage.getWidth())*3;
                    else
                        index = ((image.getWidth()-1-(colX / rate))+(rowY/rate+offsetY)*proceedImage.getWidth())*3;
                    outPixels[index] = (byte) (b&0xff);
                    outPixels[index+1] = (byte) (g&0xff);
                    outPixels[index+2] = (byte) (r&0xff);
                }
            }
            return proceedImage;
        } else {
            return image;
        }
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
        return "Mirror";
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
