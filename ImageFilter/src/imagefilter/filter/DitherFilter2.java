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
import java.awt.BorderLayout;
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
public class DitherFilter2 implements FilterInterface {

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

    public DitherFilter2() {
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

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            BufferedImage imgCopy = new BufferedImage(image.getWidth(), image.getHeight(), Constants.IMAGE_STANDARD_TYPE);
            Graphics g = imgCopy.createGraphics();
            g.drawImage(image, 0, 0, null);

            int width = image.getWidth();
            int height = image.getHeight();

            byte[] inPixels = ((DataBufferByte) imgCopy.getRaster().getDataBuffer()).getData();
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            int[] inPixelsInt = new int[width * height];
            int[] outPixelsInt = new int[width * height];
            image.getRGB(0, 0, width, height, inPixelsInt, 0, width);

            for (int y = 0; y < height; y++) {
                int yOffset = y * width;
                for (int x = 0; x < width; x++) {
                    // gets the right pixel with the yOffset, because of the one-dimensional array
                    int rgb = inPixelsInt[yOffset + x];
                    byte[] currentBrg = new byte[3];
                    for (int i = 0; i < currentBrg.length; i++) {
                        currentBrg[i] = inPixels[(yOffset + x) * 3 + i];
                    }
                    checkEquality(currentBrg, rgb);

                //int rgb = (inPixels[(yOffset + x)*3+2]&0xff)<<16 | (inPixels[(yOffset + x)*3+1]&0xff)<<8 | (inPixels[(yOffset + x)*3]&0xff);
                    // finds the closest color in the palette that suits best
                    int rgbNew = findClosestColor(rgb);
                    byte[] brgNew = findClosestColor(currentBrg);

                    // sets the new color to the outPixels array for destination image
                    //outPixels[yOffset + x] = rgbNew;
                    System.arraycopy(brgNew, 0, outPixels, (yOffset + x) * 3, brgNew.length);
                    outPixelsInt[yOffset + x] = rgbNew;
                    // calculates an error rgb value
                    byte[] brgErr = sub(currentBrg, brgNew);
                    int[] rgbErr = sub(rgb, rgbNew);
                    // the error rgb value is then multiplied by a factor and then added to pixels, which come after the current pixel
                    // . . . 
                    // . x 7
                    // 3 5 1
                    // these values were developed by floyd and steinberg
                    // Have a look at: http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
                    if (x + 1 < width) {
                        
//                        for (int i = 0; i < oldBrg.length; i++) {
//                            oldBrg[i] = inPixels[(yOffset + x + 1) * 3 + i];
//                        }
//                        byte[] newBrg = add(oldBrg, mul(brgErr, 7.0f / 16));
//                        for (int i = 0; i < oldBrg.length; i++) {
//                            inPixels[(yOffset + x + 1) * 3 + i] = newBrg[i];
//                        }
                        int[] mul = mul(rgbErr, 7.0f / 16);
                        int newRgb = add(inPixelsInt[yOffset + x + 1], mul);
                        inPixelsInt[yOffset + x + 1] = newRgb;
                        byte[] returnValue = mulAndAddToPixel(inPixels, brgErr, 7.0f / 16, yOffset + x + 1);
                        checkEquality(returnValue, newRgb);
                    }
                    if (x - 1 >= 0 && y + 1 < height) {
                        int newRgb = add(inPixelsInt[yOffset + width + x - 1], mul(rgbErr, 3.0f / 16));
                        inPixelsInt[yOffset + width + x - 1] = newRgb;
                        byte[] returnValue = mulAndAddToPixel(inPixels, brgErr, 3.0f / 16, yOffset + width + x - 1);
                        checkEquality(returnValue, newRgb);
                    }
                    if (y + 1 < height) {
                        int newRgb = add(inPixelsInt[yOffset + width + x], mul(rgbErr, 5.0f / 16));
                        inPixelsInt[yOffset + width + x] = newRgb;
                        byte[] returnValue = mulAndAddToPixel(inPixels, brgErr, 5.0f / 16, yOffset + width + x);
                        checkEquality(returnValue, newRgb);
                    }
                    if (x + 1 < width && y + 1 < height) {
                        int newRgb = add(inPixelsInt[yOffset + width + x + 1], mul(rgbErr, 1.0f / 16));
                        inPixelsInt[yOffset + width + x + 1] = newRgb;
                        byte[] returnValue = mulAndAddToPixel(inPixels, brgErr, 1.0f / 16, yOffset + width + x + 1);
                        checkEquality(returnValue, newRgb);
                    }
//                if (x+1 < width)                    inPixels[yOffset +         x+1] = add(inPixels[yOffset +         x+1], mul(rgbErr, 7.0f / 16));
//                if (x-1 >= 0 && y+1 < height)       inPixels[yOffset + width + x-1] = add(inPixels[yOffset + width + x-1], mul(rgbErr, 3.0f / 16));
//                if (y+1 < height)                   inPixels[yOffset + width + x  ] = add(inPixels[yOffset + width + x  ], mul(rgbErr, 5.0f / 16));
//                if (x+1 < width && y+1 < height)    inPixels[yOffset + width + x+1] = add(inPixels[yOffset + width + x+1], mul(rgbErr, 1.0f / 16));
                }
            }
            //BufferedImage dest = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
            //dest.setRGB(0, 0, width, height, outPixels, 0, width);

            return proceedImage;
        } else {
            return image;
        }
    }

    private void checkEquality(byte[] brg, int rgb) {
        for (int i = brg.length - 1; i >= 0; i--) {
            byte equalValue = (byte) ((rgb >> (8 * i)) & 0xff);
            if ((brg[i]) != equalValue) {
                System.out.println("NOT EQUAL");
                System.out.println("BRG:" + (brg[i] & 0xff) + " RGB:" + ((rgb >> (8 * i)) & 0xff) + " i value:" + i);
            }
        }

    }

    private byte[] mulAndAddToPixel(byte[] inPixels, byte[] brgErr, float factor, int index) {
        byte[] oldBrg = new byte[3];
        for (int i = 0; i < oldBrg.length; i++) {
            oldBrg[i] = inPixels[(index) * 3 + i];
        }
        byte[] newBrg = add(oldBrg, mul(brgErr, factor));
        System.arraycopy(newBrg, 0, inPixels, (index) * 3, oldBrg.length);
        return newBrg;
    }

    private byte[] findClosestColor(byte[] brg) {
        int min = Integer.MAX_VALUE;
        byte[] newBrg = new byte[3];
        for (int[] vals : palette) {
            int r = vals[0];
            int g = vals[1];
            int b = vals[2];

            // the color with the smallest difference suits best
            int diff = diff(brg[2], brg[1], brg[0], r, g, b);
            if (diff < min) {
                min = diff;
                newBrg[0] = (byte) (b & 0xff);
                newBrg[1] = (byte) (g & 0xff);
                newBrg[2] = (byte) (r & 0xff);
            }
        }
        return newBrg;
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

    private byte[] sub(byte[] brgOne, byte[] brgTwo) {
        byte[] newBrg = new byte[3];
        for (int i = 0; i < newBrg.length; i++) {
            newBrg[i] = (byte) (brgOne[i] - brgTwo[i]);
        }
        return newBrg;
    }

    // adds one rgb value to another --> BoundaryCheck needed
    private int add(int rgbOne, int[] rgbTwo) {
        int newR = Tools.boundaryCheck((rgbOne >> 16 & 0xff) + rgbTwo[0]);
        int newG = Tools.boundaryCheck((rgbOne >> 8 & 0xff) + rgbTwo[1]);
        int newB = Tools.boundaryCheck((rgbOne & 0xff) + rgbTwo[2]);
        return newR << 16 | newG << 8 | newB;
    }

    private byte[] add(byte[] brgOne, byte[] brgTwo) {
        byte[] newBrg = new byte[3];
        for (int i = 0; i < newBrg.length; i++) {
            newBrg[i] = (byte) (brgOne[i] + brgTwo[i]);
        }
        return newBrg;
    }

    // multiplies the errorRGB value with an given factor
    private int[] mul(int[] rgb, float f) {
        int newR = (int) (rgb[0] * f);
        int newG = (int) (rgb[1] * f);
        int newB = (int) (rgb[2] * f);
        return new int[]{newR, newG, newB};
    }

    private byte[] mul(byte[] brg, float f) {
        byte[] newBrg = new byte[3];
        for (int i = 0; i < brg.length; i++) {
            newBrg[i] = (byte) ((brg[i] * f));
        }
        return newBrg;
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
        return "Dither 2"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
