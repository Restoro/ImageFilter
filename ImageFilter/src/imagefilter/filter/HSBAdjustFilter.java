/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Constants;
import imagefilter.helper.Tools;
import imagefilter.model.Setting;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JSlider;

/**
 *
 * @author Verena
 */
public class HSBAdjustFilter implements FilterInterface {

    @Override
    public BufferedImage processImage(BufferedImage image) {

        //parameter  
        float hFactor = 0.7f;
        float sFactor = 0.7f;
        float bFactor = 1.3f;

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16 & 0xFF);
                int g = (rgb >> 8 & 0xFF);
                int b = (rgb & 0xFF);

                float[] hsb = getHSB(r / 255f, g / 255f, b / 255f);
                float[] rgbNeu = getRGB(Math.max(0, Math.min(360, hsb[0] * hFactor)),
                        Math.max(0, Math.min(1, hsb[1] * sFactor)),
                        Math.max(0, Math.min(1, hsb[2] * bFactor)));

                r = Math.round(rgbNeu[0] * 255);
                g = Math.round(rgbNeu[1] * 255);
                b = Math.round(rgbNeu[2] * 255);

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
        return "HSB Adjust";
    }

    private float[] getHSB(float r, float g, float b) {
        float hsb[] = new float[3];
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.min(g, b));

        if (min == max) {
            hsb[0] = 0;
        } else if (r == max) {
            hsb[0] = 60 * (0 + (g - b) / (max - min));
        } else if (g == max) {
            hsb[0] = 60 * (2 + (b - r) / (max - min));
        } else if (b == max) {
            hsb[0] = 60 * (4 + (r - g) / (max - min));
        }

        while (hsb[0] < 0) {
            hsb[0] += 360;
        }

        if (max == 0) {
            hsb[1] = 0;
        } else {
            hsb[1] = (max - min) / max;
        }

        hsb[2] = max;

        hsb[0] = Math.max(0f, Math.min(360f, hsb[0]));
        hsb[1] = Math.max(0f, Math.min(1f, hsb[1]));
        hsb[2] = Math.max(0f, Math.min(1f, hsb[2]));

        return hsb;
    }

    private float[] getRGB(float h, float s, float b) {
        int hi = (int) Math.floor(h / 60f);
        float f = (h / 60f - (float) hi);

        float rgb[] = new float[3];
        float t, p, q;

        p = b * (1f - s);
        q = b * (1f - s * f);
        t = b * (1f - s * (1f - f));

        switch (hi) {
            case 0:
            case 6:
                rgb[0] = b;
                rgb[1] = t;
                rgb[2] = p;
                break;
            case 1:
                rgb[0] = q;
                rgb[1] = b;
                rgb[2] = p;
                break;
            case 2:
                rgb[0] = p;
                rgb[1] = b;
                rgb[2] = t;
                break;
            case 3:
                rgb[0] = p;
                rgb[1] = q;
                rgb[2] = b;
                break;
            case 4:
                rgb[0] = t;
                rgb[1] = p;
                rgb[2] = b;
                break;
            case 5:
                rgb[0] = b;
                rgb[1] = p;
                rgb[2] = q;
                break;
            default:
                break;
        }

        rgb[0] = Math.max(0f, Math.min(1f, rgb[0]));
        rgb[1] = Math.max(0f, Math.min(1f, rgb[1]));
        rgb[2] = Math.max(0f, Math.min(1f, rgb[2]));

        return rgb;
    }

    @Override
    public Setting[] getSettings() {
        return null;
    }
}
