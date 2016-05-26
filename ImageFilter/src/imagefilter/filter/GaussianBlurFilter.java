/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Tools;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author chger_000
 */
public class GaussianBlurFilter implements FilterInterface
{
    // we use an modified version of the gaussian blur filter, because of smaller process time
    // the one-dimensional blur filter is applied line by line
    // but to get the two-dimensional gaussian blur effect, the one-dimensional logic is applied two times
    // once horizontally and once vertically
    
    @Override
    public BufferedImage processImage(BufferedImage image) {

        // ATTENTION: radius still needs to be set by the user
        float radius = 10f;
        int width = image.getWidth();
        int height = image.getHeight();
        int[] srcPixels = new int[width * height];
        int[] destPixels = new int[width * height];

        float[] matrix = createMatrix(radius);

        // saves the pixels of the image in a one-dimensional array
        image.getRGB(0, 0, width, height, srcPixels, 0, width);

        // first apply to convolve Pixels horizontally
        convolve(matrix, srcPixels, destPixels, width, height);
        // second apply to convole Pixels vertically
        convolve(matrix, destPixels, srcPixels, height, width);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        
        // sets the convolved Pixels (now again in the srcPixels array, becuase of twice used) to the image
        img.setRGB(0, 0, width, height, srcPixels, 0, width);

        return img;
    }

    // the matrix values are applied to every pixel
    private void convolve(float[] matrix, int[] srcPixels, int[] destPixels, int width, int height) {
        int halfCol = matrix.length / 2;

        for (int y = 0; y < height; y++)
        {
            int i = y;
            // we just have a one-dimensional array and therefore the yOffset variable is used to find the right Pixel
            int yOffset = y * width;
            for (int x = 0; x < width; x++)
            {
                // rgb values, which are summed up later
                float sumR = 0;
                float sumG = 0;
                float sumB = 0;
                // matrixOffset is needed to prevent an IndexOutOfBoundException in the for loop when accessing the matrix
                int matrixOffset = halfCol;
                // then we iterate a column from -radius (-halfCol) -> currentPoint -> +radius (+halfCol)
                for (int col = -halfCol; col <= halfCol; col++)
                {
                    // retrieving the right convolution value from the matrix
                   float f = matrix[matrixOffset + col];

                   // when the value is 0, nothing should happen, because it would falsify the result
                    if (f != 0)
                    {
                        // ix is the right pixel of the column (concerning the matrix) in the image width
                        int ix = x + col;
                        if (ix < 0)
                        {
                                ix = 0;
                        } else if (ix >= width)
                        {
                                ix = width - 1;
                        }
                        // the yOffset + ix results in the right position of the pixel in the one-dimensional image array
                        // then the rgb value is retrieved from this pixel
                        int rgb = srcPixels[yOffset + ix];
                        // now each r,g,b value is multiplied with the matrix value and added to the sum of sumR,sumB,sumG
                        sumR += f * ((rgb >> 16) & 0xff);
                        sumG += f * ((rgb >> 8) & 0xff);
                        sumB += f * (rgb & 0xff);
                    }
                }
                // we do not use the alpha value
                int a = 0xff;
                // some of the r,g,b values may now be greater or less than the boundaries, so we need to check them
                int r = (int) Tools.checkBoundaries((int) (sumR + 0.5), 0, 255);
                int g = (int) Tools.checkBoundaries((int) (sumG + 0.5), 0, 255);
                int b = (int) Tools.checkBoundaries((int) (sumB + 0.5), 0, 255);
                
                // then the rgb values are merged and set to the destination value
                destPixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
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
        for (int row = -r; row <= r; row++)
        {
            float dist = row * row;
            // here is the actual gaussian blur logic
            matrix[i] = (float) Math.exp(-(dist) / (2 * sigma * sigma)) / (2 * (float) Math.PI * sigma * sigma);
            
            weightSum += matrix[i];
            i++;
        }
        
        // every matrix value is then divided by the weightSum to guarantee a sum of 1
        for (int j = 0; j < matrix.length; j++)
        {
            matrix[j] /= weightSum;
        }
        
        // now we have the one-dimensional matrix weights, which are later multiplied with the pixels of the image
        
        return matrix;
    }

    @Override
    public ImageIcon getPreview() {
        return new ImageIcon(Tools.getResource("scrollright.png"));
    }
}
