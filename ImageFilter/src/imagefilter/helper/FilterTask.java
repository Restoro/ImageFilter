/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hoellinger
 */
public class FilterTask extends FutureTask<BufferedImage>
{
    private final Consumer<BufferedImage> c;

    public FilterTask(Callable<BufferedImage> callable, Consumer<BufferedImage> c)
    {
        super(callable);
        this.c = c;
    }

    @Override
    protected void done()
    {
        try
        {
            c.accept(get());
        } catch(InterruptedException | ExecutionException ex)
        {
            Logger.getLogger(FilterTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
