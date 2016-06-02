/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import imagefilter.filter.FilterInterface;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

/**
 * This Class is instance of Callable<BufferedImage> and applies a single filter.
 * @author hoellinger
 */
public class FilterOperation implements Callable<BufferedImage>
{
    private FilterInterface filter;
    private BufferedImage image;

    public FilterOperation(FilterInterface filter, BufferedImage image)
    {
        this.filter = filter;
        this.image = image;
    }

    @Override
    public BufferedImage call() throws Exception
    {
        System.out.println("start filter: " + filter.toString());
        this.image = filter.processImage(image);
        return image;
    }

    /**
     * Sets applying filter and image where it should be applied. Because of this
     * method you don't have to create a new instance of this class each time.
     * @param filter the filter, which should be applied
     * @param image the image to apply filter on
     */
    public void set(FilterInterface filter, BufferedImage image)
    {
        this.filter = filter;
        this.image = image;
    }
}
