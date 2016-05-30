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
import javax.swing.ImageIcon;

/**
 *
 * @author Gerstberger
 */
// The water filter effect outgoing from the centre of the image
// Take a look at:
// https://en.wikipedia.org/wiki/Sine_wave
// https://github.com/axet/jhlabs/blob/master/src/main/java/com/jhlabs/image/WaterFilter.java
public class WaterFilter implements FilterInterface {

    private final Setting[] settings;
    private BufferedImage preview;
    
    public WaterFilter() {
        settings = new Setting[2];
        settings[0] = new Setting("Amplitude", 1, 50, 15);
        settings[1] = new Setting("Wellenlänge", 10, 200, 100);
    }
    
    @Override
    public BufferedImage processImage(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        image = Tools.convertToStandardType(image);
        image.getRGB(0, 0, width, height, inPixels, 0, width);

        waterFilter(inPixels, outPixels, width, height);

        BufferedImage dest = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
        dest.setRGB(0, 0, width, height, outPixels, 0, width);

        return dest;
    }

    public void waterFilter(int[] inPixels, int[] outPixels, int width, int height) {

        float amplitude = settings[0].getCurValue();
        float waveLength = settings[1].getCurValue();
        
        int i = 0;
        int centreX = width / 2;
        int centreY = height / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int xOffset = x - centreX;
                int yOffset = y - centreY;

                // distance is needed to calculate how many waves should be created
                float distance = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);

                // formula of the sine wave: A*sin(2*pi*frequency*time) 
                float sineWavePixels = amplitude * (float) Math.sin(distance / waveLength * Math.PI * 2.0f);

                // add the amount of sineWavePixels to the current Position outgoing from the center of the image
                float newX = centreX + xOffset + sineWavePixels;
                float newY = centreY + yOffset + sineWavePixels;

                // last check if the new position is out of bound and cast to integer
                int nx = (int) Tools.checkBoundaries((int) (newX + 0.5f), 0, width - 1);
                int ny = (int) Tools.checkBoundaries((int) (newY + 0.5f), 0, height - 1);

                // get the pixel of the calculated position and set the rgb value to the current position
                int rgb = inPixels[ny * width + nx];
                outPixels[i] = rgb;

                i++;
            }
        }
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }

    @Override
    public void setPreview(BufferedImage preview)
    {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "WaterFilter"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
