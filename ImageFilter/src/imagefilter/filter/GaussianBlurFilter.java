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
 * @author Gerstberger
 */
public class GaussianBlurFilter implements FilterInterface {
    // we use an modified version of the gaussian blur filter, because of smaller process time
    // the one-dimensional blur filter is applied line by line
    // but to get the two-dimensional gaussian blur effect, the one-dimensional logic is applied two times
    // once horizontally and once vertically

    private final Setting[] settings;
    private ImageIcon preview;

    public GaussianBlurFilter() {
        settings = new Setting[1];
        settings[0] = new Setting("Radius", 5, 20, 10);
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {

        float radius = settings[0].getCurValue();
        int width = image.getWidth();
        int height = image.getHeight();

        float[] matrix = createMatrix(radius);

        BufferedImage proceedImage = new BufferedImage(width, height, Constants.IMAGE_STANDARD_TYPE);
        image = Tools.convertToStandardType(image);

        if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
            byte[] inPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] bufferPixels = new byte[inPixels.length];
            byte[] outPixels = ((DataBufferByte) proceedImage.getRaster().getDataBuffer()).getData();

            // first apply to convolve Pixels horizontally
            convolve(matrix, inPixels, bufferPixels, width, height);
            // second apply to convole Pixels vertically
            convolve(matrix, bufferPixels, outPixels, height, width);
            
            return proceedImage;
        } else {
            return image;
        }
    }

    // the matrix values are applied to every pixel
    private void convolve(float[] matrix, byte[] srcPixels, byte[] destPixels, int width, int height) {
        int halfCol = matrix.length / 2;

        for (int y = 0; y < height; y++) {
            int i = y;
            // we just have a one-dimensional array and therefore the yOffset variable is used to find the right Pixel
            int yOffset = y * width;
            for (int x = 0; x < width; x++) {
                // rgb values, which are summed up later
                float sumR = 0;
                float sumG = 0;
                float sumB = 0;
                // matrixOffset is needed to prevent an IndexOutOfBoundException in the for loop when accessing the matrix
                int matrixOffset = halfCol;
                // then we iterate a column from -radius (-halfCol) -> currentPoint -> +radius (+halfCol)
                for (int col = -halfCol; col <= halfCol; col++) {
                    // retrieving the right convolution value from the matrix
                    float f = matrix[matrixOffset + col];

                    // when the value is 0, nothing should happen, because it would falsify the result
                    if (f != 0) {
                        // ix is the right pixel of the column (concerning the matrix) in the image width
                        int ix = x + col;
                        if (ix < 0) {
                            ix = 0;
                        } else if (ix >= width) {
                            ix = width - 1;
                        }
                        // the yOffset + ix results in the right position of the pixel in the one-dimensional image array
                        // then the rgb value is retrieved from this pixel
                        int b = srcPixels[(yOffset + ix) * 3] & 0xFF;
                        int g = srcPixels[(yOffset + ix) * 3 + 1] & 0xFF;
                        int r = srcPixels[(yOffset + ix) * 3 + 2] & 0xFF;
                        // now each r,g,b value is multiplied with the matrix value and added to the sum of sumR,sumB,sumG
                        sumR += f * r;
                        sumG += f * g;
                        sumB += f * b;
                    }
                }
                // some of the r,g,b values may now be greater or less than the boundaries, so we need to check them
                int r = (int) Tools.boundaryCheck((int) (sumR + 0.5));
                int g = (int) Tools.boundaryCheck((int) (sumG + 0.5));
                int b = (int) Tools.boundaryCheck((int) (sumB + 0.5));

                // then the rgb values are merged and set to the destination value
                destPixels[i * 3    ] = (byte) b;
                destPixels[i * 3 + 1] = (byte) g;
                destPixels[i * 3 + 2] = (byte) r;

                i += height;
            }
        }
    }

    // here the matrix is created for a given radius
    // the values of the matrix look like the gaussian bell curve, when shown in a diagram
    // but they are just one-dimensional and therefore saved in a one-dimensional array
    // to apply these values on the two-dimensional image, we apple the one-dimensional matrix two times
    // once horizontally and once vertically, so we also get the two-dimensional gaussian blur effect
    // Have a look at: http://www.pixelstech.net/article/1353768112-Gaussian-Blur-Algorithm
    private float[] createMatrix(float radius) {
        // first we convert the float radius to an integer
        int r = (int) Math.ceil(radius);
        // then the sigma value is choosen. This value is application dependent...
        float sigma = radius / 3;

        // the matrix is one-dimensional from -radius -> centerPoint -> +radius
        float[] matrix = new float[r * 2 + 1];

        // at the end every weight value in the matrix has to be divided by weightSum, 
        // because otherwise the the matrix values added together don't add up to 1
        float weightSum = 0;
        int i = 0;
        for (int row = -r; row <= r; row++) {
            float dist = row * row;
            // here is the actual gaussian blur logic
            matrix[i] = (float) Math.exp(-(dist) / (2 * sigma * sigma)) / (2 * (float) Math.PI * sigma * sigma);

            weightSum += matrix[i];
            i++;
        }

        // every matrix value is then divided by the weightSum to guarantee a sum of 1
        for (int j = 0; j < matrix.length; j++) {
            matrix[j] /= weightSum;
        }

        // now we have the one-dimensional matrix weights, which are later multiplied with the pixels of the image
        return matrix;
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
        return "Gaussian Blur";
    }

    @Override
    public Setting[] getSettings() {
        return settings;
    }
}
