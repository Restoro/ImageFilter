/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import imagefilter.filter.FilterCallable;
import imagefilter.filter.FilterInterface;
import imagefilter.filter.FilterTask;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.listener.FiltersChangedListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import imagefilter.listener.DisplayImageChangedListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fritsch
 */
public class Model
{
    private BufferedImage referenceImage;
    private BufferedImage displayImage;
    private volatile int currentImage;

    private final Set<FilterInterface> allFilters;
    private final List<FilterPair> applyingFilters;

    private final List<DisplayImageChangedListener> displayImageChangedListeners;
    private final List<FiltersChangedListener> filtersChangedListeners;
    private final List<ApplyingFiltersChangedListener> applyingFiltersChangedListeners;

    private FilterTask filterTask;
    private FilterCallable filterCallable;
    private Consumer<BufferedImage> filterFinished;
    private ExecutorService filterProcessor;
    private volatile boolean isProcessorWorking;
    private volatile boolean stop;

    public Model()
    {
        this.displayImageChangedListeners = new LinkedList<>();
        this.filtersChangedListeners = new LinkedList<>();
        this.applyingFiltersChangedListeners = new LinkedList<>();
        this.allFilters = new HashSet<>();
        this.applyingFilters = new LinkedList<>();
        filterCallable = new FilterCallable(null, null);
        filterFinished = image
                -> 
                {
                    System.out.println("Complete");
                    if(stop)
                    {
                        return;
                    }
                    synchronized(applyingFilters)
                    {
                        currentImage++;
                        applyingFilters.get(currentImage).image = image;
                        if(currentImage != applyingFilters.size() - 1)
                        {
                            startProcessing();
                        } else
                        {
                            setDisplayImage(getCurrentImage());
                            fireApplyingFiltersChanged(listener -> listener.finished());
                            isProcessorWorking = false;
                        }
                    }
        };
        filterProcessor = Executors.newSingleThreadExecutor();
    }

    public BufferedImage getReferenceImage()
    {
        return referenceImage;
    }

    public void setReferenceImage(BufferedImage referenceImage)
    {
        this.referenceImage = referenceImage;
        this.currentImage = -1;
        deleteAllApplyingFilters();
        setDisplayImage(referenceImage);
    }

    public void setFilters(Collection<FilterInterface> filters)
    {
        this.allFilters.clear();
        this.allFilters.addAll(filters);
        fireFiltersChanged();
    }

    public void addFilter(FilterInterface filter)
    {
        if(this.allFilters.add(filter))
        {
            fireFiltersChanged();
        }
    }

    public void removeFilter(FilterInterface filter)
    {
        if(this.allFilters.remove(filter))
        {
            fireFiltersChanged();
        }
    }

    public Collection<FilterInterface> addFiltersChangedListener(FiltersChangedListener listener)
    {
        filtersChangedListeners.add(listener);
        return allFilters;
    }

    public void removeFiltersChangedListener(FiltersChangedListener listener)
    {
        filtersChangedListeners.remove(listener);
    }

    private void fireFiltersChanged()
    {
        Iterator<FiltersChangedListener> it = filtersChangedListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                it.next().filtersChanged(allFilters);
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }

    public void setDisplayImage(BufferedImage image)
    {
        this.displayImage = image;
        fireDisplayImageChanged();
    }

    public void setDisplayImage(FilterInterface filter)
    {
        if(referenceImage == null)
        {
            return;
        }
        setDisplayImage(filter.processImage(getCurrentImage()));
    }

    public void setDisplayImage(int index)
    {
        if(currentImage < index || index < 0)
        {
            return;
        }
        setDisplayImage(applyingFilters.get(index).image);
    }

    public void addDisplayImageChangedListener(DisplayImageChangedListener listener)
    {
        displayImageChangedListeners.add(listener);
    }

    public void removeDisplayImageChangedListener(DisplayImageChangedListener listener)
    {
        displayImageChangedListeners.remove(listener);
    }

    private void fireDisplayImageChanged()
    {
        Iterator<DisplayImageChangedListener> it = displayImageChangedListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                it.next().displayImageChanged(displayImage);
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }

    private void deleteAllApplyingFilters()
    {
        applyingFilters.clear();
        fireApplyingFiltersChanged(listener -> listener.applyingFiltersChanged(new LinkedList<>()));
    }

    public void addApplyingFilter(FilterInterface filter)
    {
        fireApplyingFiltersChanged(listener -> listener.addApplyingFilter(filter));
        applyingFilters.add(new FilterPair(filter, null));
        applyFilters();
    }

    public void removeApplyingFilter(int index)
    {
        if(index >= applyingFilters.size())
        {
            return;
        }
        fireApplyingFiltersChanged(listener -> listener.removeApplyingFilter(index));
        applyingFilters.remove(index);
        applyFilters(index);
    }

    private void applyFilters()
    {
        if(!isProcessorWorking)
        {
            startProcessing();
        }
    }

    private void applyFilters(int index)
    {
        if(currentImage > index)
        {
            stop = true;
            try
            {
                filterTask.get();
            } catch(InterruptedException | ExecutionException ex)
            {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
            stop = false;
            currentImage = index - 1;
            startProcessing();
        }
    }

    private void startProcessing()
    {
        if(applyingFilters.isEmpty())
        {
            currentImage = -1;
        }
        if(currentImage == applyingFilters.size() - 1)
        {
            return;
        }
        setDisplayImage(getCurrentImage());
        fireApplyingFiltersChanged(listener -> listener.startApplyingFilter(currentImage + 1));
        isProcessorWorking = true;
        filterCallable.set(applyingFilters.get(currentImage + 1).filter, getCurrentImage());
        filterTask = new FilterTask(filterCallable, filterFinished);
        filterProcessor.execute(filterTask);
    }

    private BufferedImage getCurrentImage()
    {
        return currentImage == -1 ? referenceImage : applyingFilters.get(currentImage).image;
    }

    public void addApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener)
    {
        applyingFiltersChangedListeners.add(listener);
    }

    public void removeApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener)
    {
        applyingFiltersChangedListeners.remove(listener);
    }

    private void fireApplyingFiltersChanged(Consumer<ApplyingFiltersChangedListener> consumer)
    {
        Iterator<ApplyingFiltersChangedListener> it = applyingFiltersChangedListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                consumer.accept(it.next());
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }

    private class FilterPair
    {
        FilterInterface filter;
        BufferedImage image;

        public FilterPair(FilterInterface filter, BufferedImage image)
        {
            this.filter = filter;
            this.image = image;
        }

    }
}
