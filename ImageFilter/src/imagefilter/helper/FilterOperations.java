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
 * This Class provides the code to apply the filters in an own thread.
 * @author hoellinger
 */
public class FilterOperations implements Runnable
{
    private final Model model;
    private final List<Model.FilterPair> filters;
    private final ExecutorService executorService;
    private final FilterOperation filterOp;
    private int current;

    /**
     * @param model the model to say, when a filter is finished and other things
     * @param filters the list of FilterPairs to apply the filters
     */
    public FilterOperations(Model model, List<Model.FilterPair> filters)
    {
        this(model, filters, 0);
    }

    /**
     * @param model the model to say, when a filter is finished and other things
     * @param filters the list of FilterPairs to apply the filters
     * @param current where the thread has to start to apply filters
     */
    public FilterOperations(Model model, List<Model.FilterPair> filters, int current)
    {
        this.model = model;
        this.filters = filters;
        this.filterOp = new FilterOperation(null, null);
        executorService = Executors.newSingleThreadExecutor();
        this.current = current;
    }

    /**
     * With this method you do not have to create a new instance of this class.
     * It sets the index, where the thread has to start applying filters
     * @param current starting index to apply filters
     */
    public void setCurrent(int current)
    {
        this.current = current;
    }

    @Override
    public void run()
    {
        System.out.println("start filteroperations");
        //boolean to check either filters were successfully applied or not (for example when interrupt this thread)
        boolean failure = false;
        while(!Thread.currentThread().isInterrupted())
        {
            Model.FilterPair filterPair;
            synchronized(filters)
            {
                if(current+1 >= filters.size())
                {
                    break;
                }
                //Tell model that next filter is starting
                model.startApplyingNextFilter();
                //the new filter to apply and the image of the last applied filter or the reference image if this is the first filter
                filterPair = model.getApplyingFilterAndSourceImg(current++);                        
            }
            BufferedImage img;
            //sets filter and image of FilterOperation. So you don't have to ceate a new instance.
            filterOp.set(filterPair.filter, filterPair.image);
            try
            {
                //applies filter and waits until it is finished
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
            //tell model that current filter is applied
            model.appliedFilter(img);
        }
        if(!failure||!Thread.currentThread().isInterrupted())
        {
            //tell model that all filters are successfully applied
            model.finishedApplyingFilters();
        }
    }

}
