/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Constants;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import imagefilter.model.SettingWithXOptions;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
public class ChannelMixFilter implements FilterInterface {

    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    private final Setting[] settings;
    private BufferedImage preview;

    public ChannelMixFilter() {
        settings = new Setting[1];
        settings[0] = new SettingWithXOptions("RGB", 0, 2, 0) {

            @Override
            public String[] getOptionNames() {
                return new String[]{"R", "G", "B"};
            }
        };
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] inPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            for (int i = 0; i < inPixels.length; i += 3) {
                int b = inPixels[i] & 0xff;
                int g = inPixels[i + 1] & 0xff;
                int r = inPixels[i + 2] & 0xff;

                switch (settings[0].getCurValue()) {
                    case RED:
                        outPixels[i] = (byte)b;
                        outPixels[i + 1] = (byte) g;
                        outPixels[i + 2] = (byte) (g / 2 + b / 2);
                        break;
                    case GREEN:
                        outPixels[i] = (byte)b;
                        outPixels[i + 1] = (byte) (r / 2 + b / 2);
                        outPixels[i + 2] = (byte) r;
                        break;
                    case BLUE:
                        outPixels[i] = (byte)(r / 2 + g / 2);
                        outPixels[i + 1] = (byte) g;
                        outPixels[i + 2] = (byte) r;
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
    public void setPreview(BufferedImage preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "Channel Mix"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
