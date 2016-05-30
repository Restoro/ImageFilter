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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
// In our DitherFilter we use the Floyd-Steinberg algorithm
//
// https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
// http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
public class DitherFilter implements FilterInterface {

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
    private BufferedImage preview;

    public DitherFilter() {
        settings = new Setting[1];
        settings[0] = new SettingWithXOptions("Palette", 0, 2, 2) {
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

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage proceedImage = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);
        
        BufferedImage imgCopy = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
        Graphics g = imgCopy.createGraphics();
        g.drawImage(image, 0, 0, null);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] inPixels = ((DataBufferByte) imgCopy.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            dither(inPixels, outPixels, width, height);

            return proceedImage;
        } else {
            return image;
        }
    }

    private void dither(byte[] inPixels, byte[] outPixels, int width, int height) {

        for (int y = 0; y < height; y++) {
            int yOffset = y * width;
            for (int x = 0; x < width; x++) {
                // gets the right pixel with the yOffset, because of the one-dimensional array
                int[] bgr = new int[3];
                bgr[0] = inPixels[(yOffset + x) * 3] & 0xff;
                bgr[1] = inPixels[(yOffset + x) * 3 + 1] & 0xff;
                bgr[2] = inPixels[(yOffset + x) * 3 + 2] & 0xff;

                // finds the closest color in the palette that suits best
                int[] bgrNew = findClosestColor(bgr);
                // sets the new color to the outPixels array for destination image
                outPixels[(yOffset + x) * 3] = (byte) bgrNew[0];
                outPixels[(yOffset + x) * 3 + 1] = (byte) bgrNew[1];
                outPixels[(yOffset + x) * 3 + 2] = (byte) bgrNew[2];

                // calculates an error bgr value
                int[] bgrErr = sub(bgr, bgrNew);

                // the error rgb value is then multiplied by a factor and then added to pixels, which come after the current pixel
                // . . . 
                // . x 7
                // 3 5 1
                // these values were developed by floyd and steinberg
                // Have a look at: http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
                if (x + 1 < width) {
                    float f = 7.0f / 16;

                    for (int i = 0; i <= 2; i++) {
                        int mulVal = (int) (bgrErr[i] * f);
                        int addVal = inPixels[(yOffset + x + 1) * 3 + i] + mulVal;
                        inPixels[(yOffset + x + 1) * 3 + i] = (byte) (Tools.boundaryCheck(addVal));
                    }
                }
                if (x - 1 >= 0 && y + 1 < height) {
                    float f = 3.0f / 16;

                    for (int i = 0; i <= 2; i++) {
                        int mulVal = (int) (bgrErr[i] * f);
                        int addVal = inPixels[(yOffset + width + x - 1) * 3 + i] + mulVal;
                        inPixels[(yOffset + width + x - 1) * 3 + i] = (byte) (Tools.boundaryCheck(addVal));
                    }
                }
                if (y + 1 < height) {
                    float f = 5.0f / 16;

                    for (int i = 0; i <= 2; i++) {
                        int mulVal = (int) (bgrErr[i] * f);
                        int addVal = inPixels[(yOffset + width + x) * 3 + i] + mulVal;
                        inPixels[(yOffset + width + x) * 3 + i] = (byte) (Tools.boundaryCheck(addVal));
                    }
                }
                if (x + 1 < width && y + 1 < height) {
                    float f = 1.0f / 16;

                    for (int i = 0; i <= 2; i++) {
                        int mulVal = (int) (bgrErr[i] * f);
                        int addVal = inPixels[(yOffset + width + x + 1) * 3 + i] + mulVal;
                        inPixels[(yOffset + width + x + 1) * 3 + i] = (byte) (Tools.boundaryCheck(addVal));
                    }
                }
            }
        }
    }

    // searches for the closest color in the palette that suits best
    // returns an array with {b,g,r}
    private int[] findClosestColor(int[] bgrOrig) {

        int[] newBGR = new int[3];

        int min = Integer.MAX_VALUE;
        for (int[] vals : palette) {
            // the color with the smallest difference suits best
            int diff = diff(bgrOrig, vals);
            if (diff < min) {
                min = diff;
                newBGR[0] = vals[0];
                newBGR[1] = vals[1];
                newBGR[2] = vals[2];
            }
        }
        return newBGR;
    }

    private int diff(int[] bgr, int[] bgr2) {
        return Math.abs(bgr[0] - bgr2[0]) + Math.abs(bgr[1] - bgr2[1]) + Math.abs(bgr[2] - bgr2[2]);
    }

    private int[] sub(int[] bgrOne, int[] bgrTwo) {
        int newB = (bgrOne[0] - bgrTwo[0]);
        int newG = (bgrOne[1] - bgrTwo[1]);
        int newR = (bgrOne[2] - bgrTwo[2]);
        return new int[]{newR, newG, newB};
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
        return "Dither"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
