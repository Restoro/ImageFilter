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
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
// In our DitherFilter we use the Floyd-Steinberg algorithm
//
// https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
// http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
public class DitherFilter1 implements FilterInterface {

    private int[][] palette = null;

    private final int[][] paletteColor = {
        {0, 0, 0},
        {0, 0, 255},
        {0, 255, 0},
        {0, 255, 255},
        {255, 0, 0},
        {255, 0, 255},
        {255, 255, 0},
        {255, 255, 255}
    };

    private final int[][] paletteBlackWhite = {
        {0, 0, 0},
        {255, 255, 255}
    };

    private final int[][] paletteGray = {
        {0, 0, 0},
        {128, 128, 128},
        {255, 255, 255}
    };

    private final Setting[] settings;
    private ImageIcon preview;

    public DitherFilter1() {
        settings = new Setting[1];
        settings[0] = new SettingWithXOptions("Palette", 0, 2, 0) {
            @Override
            public String[] getOptionNames() {
                return new String[]{"B/W", "Gray", "Color"};
            }
        };
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {

        // sets the adjusted color palette
        switch (settings[0].getCurValue()) {
            case 0:
                palette = paletteBlackWhite;
                break;
            case 1:
                palette = paletteGray;
                break;
            case 2:
                palette = paletteColor;
        }

        BufferedImage proceedImage = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        int width = image.getWidth();
        int height = image.getHeight();

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        image.getRGB(0, 0, width, height, inPixels, 0, width);

        for (int y = 0; y < height; y++) {
            int yOffset = y * width;
            for (int x = 0; x < width; x++) {
                // gets the right pixel with the yOffset, because of the one-dimensional array
                int rgb = inPixels[yOffset + x];
                // finds the closest color in the palette that suits best
                int rgbNew = findClosestColor(rgb);
                // sets the new color to the outPixels array for destination image
                outPixels[yOffset + x] = rgbNew;

                // calculates an error rgb value
                int[] rgbErr = sub(rgb, rgbNew);

                // the error rgb value is then multiplied by a factor and then added to pixels, which come after the current pixel
                // . . . 
                // . x 7
                // 3 5 1
                // these values were developed by floyd and steinberg
                // Have a look at: http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
                if (x + 1 < width) {
                    inPixels[yOffset + x + 1] = add(inPixels[yOffset + x + 1], mul(rgbErr, 7.0f / 16));
                }
                if (x - 1 >= 0 && y + 1 < height) {
                    inPixels[yOffset + width + x - 1] = add(inPixels[yOffset + width + x - 1], mul(rgbErr, 3.0f / 16));
                }
                if (y + 1 < height) {
                    inPixels[yOffset + width + x] = add(inPixels[yOffset + width + x], mul(rgbErr, 5.0f / 16));
                }
                if (x + 1 < width && y + 1 < height) {
                    inPixels[yOffset + width + x + 1] = add(inPixels[yOffset + width + x + 1], mul(rgbErr, 1.0f / 16));
                }
            }
        }
        BufferedImage dest = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
        dest.setRGB(0, 0, width, height, outPixels, 0, width);

        return dest;
    }

    // searches for the closest color in the palette that suits best
    private int findClosestColor(int rgb) {
        int rOrig = rgb >> 16 & 0xff;
        int gOrig = rgb >> 8 & 0xff;
        int bOrig = rgb & 0xff;

        int newRGB = 0;

        int min = Integer.MAX_VALUE;
        for (int[] vals : palette) {
            int r = vals[0];
            int g = vals[1];
            int b = vals[2];

            // the color with the smallest difference suits best
            int diff = diff(rOrig, gOrig, bOrig, r, g, b);
            if (diff < min) {
                min = diff;
                newRGB = r << 16 | g << 8 | b;
            }
        }
        return newRGB;
    }

    private int diff(int r, int g, int b, int r2, int g2, int b2) {
        return Math.abs(r - r2) + Math.abs(g - g2) + Math.abs(b - b2);
    }

    private int[] sub(int rgbOne, int rgbTwo) {
        int newR = (rgbOne >> 16 & 0xff) - (rgbTwo >> 16 & 0xff);
        int newG = (rgbOne >> 8 & 0xff) - (rgbTwo >> 8 & 0xff);
        int newB = (rgbOne & 0xff) - (rgbTwo & 0xff);
        return new int[]{newR, newG, newB};
    }

    // adds one rgb value to another --> BoundaryCheck needed
    private int add(int rgbOne, int[] rgbTwo) {
        int newR = Tools.boundaryCheck((rgbOne >> 16 & 0xff) + rgbTwo[0]);
        int newG = Tools.boundaryCheck((rgbOne >> 8 & 0xff) + rgbTwo[1]);
        int newB = Tools.boundaryCheck((rgbOne & 0xff) + rgbTwo[2]);
        return newR << 16 | newG << 8 | newB;
    }

    // multiplies the errorRGB value with an given factor
    private int[] mul(int[] rgb, float f) {
        int newR = (int) (rgb[0] * f);
        int newG = (int) (rgb[1] * f);
        int newB = (int) (rgb[2] * f);
        return new int[]{newR, newG, newB};
    }

    @Override
    public ImageIcon getPreview() {
        return preview;
    }

    @Override
    public void setPreview(ImageIcon preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "Dither 1"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }

}
