/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import imagefilter.model.Model;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hoellinger
 */
public class FilterOperations implements Runnable
{
    private final Model model;
    private final List<Model.FilterPair> filters;
    private final ExecutorService executorService;
    private final FilterOperation filterOp;
    private int current;

    public FilterOperations(Model model, List<Model.FilterPair> filters)
    {
        this(model, filters, 0);
    }

    public FilterOperations(Model model, List<Model.FilterPair> filters, int current)
    {
        this.model = model;
        this.filters = filters;
        this.filterOp = new FilterOperation(null, null);
        executorService = Executors.newSingleThreadExecutor();
        this.current = current;
    }

    public void setCurrent(int current)
    {
        this.current = current;
    }

    @Override
    public void run()
    {
        System.out.println("start filteroperations");
        boolean failure = false;
        while(!Thread.currentThread().isInterrupted())
        {
            Model.FilterPair filterPair = null;
            synchronized(filters)
            {
                if(current+1 >= filters.size())
                {
                    break;
                }
                model.startApplyingNextFilter();
                filterPair = model.getApplyingFilterAndSourceImg(current++);                        
            }
            BufferedImage img;
            filterOp.set(filterPair.filter, filterPair.image);
            try
            {
                img = executorService.submit(filterOp).get();
            } catch(InterruptedException ex)
            {
                failure = true;
                break;
            } catch(ExecutionException ex)
            {
                failure = true;
                Logger.getLogger(FilterOperations.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            model.appliedFilter(img);
        }
        if(!failure||!Thread.currentThread().isInterrupted())
        {
            model.finishedApplyingFilters();
        }
    }

}
