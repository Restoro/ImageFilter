/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.FilterOperations;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.listener.FiltersChangedListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import imagefilter.listener.DisplayImageChangedListener;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the main model of the programm. Important data are:
 * <ul>
 * <li>Reference Image</li>
 * <li>Displayed Image -> filter and result</li>
 * <li>Current Image -> index of last applied filter</li>
 * <li>List of all possible filters</li>
 * <li>List of applied filters</li>
 * </ul>
 * This model supports three different types of listeners:
 * <ul>
 * <li>Filters Changed: when list of applyable filters changed</li>
 * <li>Applying Filters Changed: All changes of applying filters</li>
 * <li>Display Image Changed: To see result of filter or to change settings</li>
 * </ul>
 * The model has its own thread to apply filters.
 *
 * @author hoellinger
 */
public class Model
{

    private BufferedImage referenceImage;
    private FilterPair displayImage;
    private volatile int currentImage;

    private final SortedSet<FilterInterface> allFilters;
    private final List<FilterPair> applyingFilters;

    private final List<DisplayImageChangedListener> displayImageChangedListeners;
    private final List<FiltersChangedListener> filtersChangedListeners;
    private final List<ApplyingFiltersChangedListener> applyingFiltersChangedListeners;

    private final ScheduledExecutorService filterProcessor;
    private final FilterOperations filterOpRunnable;
    private ScheduledFuture filterResult;

    private boolean removeFilterIfAppliedInList = true;

    public Model()
    {
        this.displayImageChangedListeners = new LinkedList<>();
        this.filtersChangedListeners = new LinkedList<>();
        this.applyingFiltersChangedListeners = new LinkedList<>();
        this.allFilters = new TreeSet<>((FilterInterface a, FilterInterface b)
                -> 
                {
                    return a.toString().compareTo(b.toString());
        });
        this.applyingFilters = new LinkedList<>();
        this.filterProcessor = Executors.newSingleThreadScheduledExecutor();
        this.filterOpRunnable = new FilterOperations(this, applyingFilters);

    }

    /**
     * Returns the current displayed filter and its result
     *
     * @return the current display image and filter
     */
    public FilterPair getDisplayImage()
    {
        return displayImage;
    }

    /**
     * Sets the display image
     * @param image filter and result of it
     */
    public void setDisplayImage(FilterPair image)
    {
        this.displayImage = image;
        fireDisplayImageChanged();
    }

    /**
     * Sets the display image, by applying the current image on it.
     * @param filter filter to set
     */
    public void setDisplayImage(FilterInterface filter)
    {
        if(referenceImage == null)
        {
            return;
        }
        setDisplayImage(new FilterPair(filter, filter.processImage(getCurrentImage())));
    }

    /**
     * Sets the display image to the filter pair in applied filters
     * @param index index of applied filters
     */
    public void setDisplayImage(int index)
    {
        if(currentImage < index || index < 0)
        {
            return;
        }
        setDisplayImage(applyingFilters.get(index));
    }

    /**
     * Returns the reference image
     * @return reference image
     */
    public BufferedImage getReferenceImage()
    {
        return referenceImage;
    }

    /**
     * Sets the reference image. By doing this, all applying filters become deleted.
     * @param referenceImage 
     */
    public void setReferenceImage(BufferedImage referenceImage)
    {
        if(referenceImage != null)
        {
            this.referenceImage = referenceImage;
            this.currentImage = -1;
            deleteAllApplyingFilters();
            setDisplayImage(new FilterPair(null, referenceImage));
        }
    }

    /**
     * Returns either the reference image if no filter is applied yet, or the result 
     * of the last applied filter. Note that this needs not to be the result of the last
     * filter in the applying list, because maybe the applying thread is not finished yet.
     * @return current image
     */
    public BufferedImage getCurrentImage()
    {
        return currentImage == -1 ? referenceImage : applyingFilters.get(currentImage).image;
    }

    /**
     * There are two different modes when adding a new filter.
     * <br>
     * 1: remove all following filters.
     * <br>
     * 2: apply all following filters again.
     * @param removeFilterIfAppliedInList if true mode 1, else mode 2
     */
    public void setRemoveFilterIfAppliedInList(boolean removeFilterIfAppliedInList)
    {
        this.removeFilterIfAppliedInList = removeFilterIfAppliedInList;
    }
    
/**
 * Sets the list of applyable filters.
 * @param filters the new list of appliable filters
 */
    public void setFilters(Collection<FilterInterface> filters)
    {
        this.allFilters.clear();
        this.allFilters.addAll(filters);
        fireFiltersChanged();
    }

    /**
     * Add a new appliable filter.
     * @param filter new appliable filter
     */
    public void addFilter(FilterInterface filter)
    {
        if(this.allFilters.add(filter))
        {
            fireFiltersChanged();
        }
    }

    /**
     * Removes an appliable filter.
     * @param filter appliable filter to remove
     */
    public void removeFilter(FilterInterface filter)
    {
        if(this.allFilters.remove(filter))
        {
            fireFiltersChanged();
        }
    }

    /**
     * Changes the appliable filters by removing all of the first list and adding all 
     * of the second list
     * @param removed filters to remove
     * @param added filters to add
     */
    public void changeFilters(List<FilterInterface> removed, List<FilterInterface> added)
    {
        this.allFilters.removeAll(removed);
        this.allFilters.addAll(added);
        fireFiltersChanged();
    }

    /**
     * Adds an applying filter to the list. See setRemoveFilterIfAppliedInList(boolean)
     * to read different types of modes. Stops applying filters when index to add < currentImage+1.
     * @param filter adds an applying filter
     */
    public void addApplyingFilter(FilterInterface filter)
    {
        if(filter == null || referenceImage == null)
        {
            return;
        }
        if(getIndexOfFilterPair(displayImage) != applyingFilters.size() - 1)
        {
            try
            {
                continueWithApplyingFilter(getIndexOfFilterPair(displayImage), new FilterPair(filter.getClass().newInstance(), null));
            } catch(InstantiationException | IllegalAccessException ex)
            {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else
        {
            fireApplyingFiltersChanged(listener -> listener.addApplyingFilter(filter));
            synchronized(applyingFilters)
            {
                try
                {
                    applyingFilters.add(new FilterPair(filter.getClass().newInstance(), null));
                } catch(InstantiationException | IllegalAccessException ex)
                {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            applyFilters();
        }
    }

    /**
     * Removes the filter with the specified index. Stops applying filter if 
     * index < currentImage+1.
     * @param index the index to remove
     */
    public void removeApplyingFilter(int index)
    {
        if(index >= applyingFilters.size() || index < 0)
        {
            return;
        }
        fireApplyingFiltersChanged(listener -> listener.removeApplyingFilter(index));
        synchronized(applyingFilters)
        {
            applyingFilters.remove(index);
        }
        applyFilters(index);
    }

    /**
     * The next applying filter is finished.
     * @param img result of filter
     */
    public void appliedFilter(BufferedImage img)
    {
        synchronized(applyingFilters)
        {
            currentImage++;
            applyingFilters.get(currentImage).image = img;
        }
        setDisplayImage(applyingFilters.get(currentImage));
    }

    /**
     * All filters are applied now.
     */
    public void finishedApplyingFilters()
    {
        synchronized(applyingFilters)
        {
            currentImage = applyingFilters.size() - 1;
        }
        fireApplyingFiltersChanged(listener -> listener.finished(displayImage));
    }

    /**
     * Returns the filter to apply next and the last result of the filter in relation to param current.
     * @param current
     * @return 
     */
    public FilterPair getApplyingFilterAndSourceImg(int current)
    {
        return new FilterPair(applyingFilters.get(current + 1).filter, getCurrentImage());
    }

    /**
     * The next filter is applied now.
     */
    public void startApplyingNextFilter()
    {
        fireApplyingFiltersChanged(listener -> listener.startApplyingFilter(currentImage + 1));
    }

    /**
     * The settings of the display image has changed now. If index of displayImage < currentImage +1
     * applying filters is stopped.
     */
    public void settingsChanged()
    {
        int index = applyingFilters.indexOf(displayImage);
        if(index >= applyingFilters.size() || index < 0)
        {
            return;
        }
        applyFilters(index);
    }

    /**
     * Returns the index of the filter pair in applied list.
     * @param filterPair
     * @return 
     */
    public int getIndexOfFilterPair(FilterPair filterPair)
    {
        return this.applyingFilters.indexOf(filterPair);
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

    public void addDisplayImageChangedListener(DisplayImageChangedListener listener)
    {
        displayImageChangedListeners.add(listener);
    }

    public void removeDisplayImageChangedListener(DisplayImageChangedListener listener)
    {
        displayImageChangedListeners.remove(listener);
    }

    public void addApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener)
    {
        applyingFiltersChangedListeners.add(listener);
    }

    public void removeApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener)
    {
        applyingFiltersChangedListeners.remove(listener);
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

    private void deleteAllApplyingFilters()
    {
        applyingFilters.clear();
        fireApplyingFiltersChanged(listener -> listener.applyingFiltersChanged(new LinkedList<>()));
    }

    private void applyFilters()
    {
        if(filterResult == null || filterResult.isCancelled() || filterResult.isDone())
        {
            filterOpRunnable.setCurrent(currentImage);
            filterResult = filterProcessor.schedule(filterOpRunnable, 0, TimeUnit.MILLISECONDS);
        }
    }

    private void applyFilters(int index)
    {
        if((currentImage + 1) >= index)
        {
            filterResult.cancel(true);
            filterResult = null;
            currentImage = index - 1;
            applyFilters();
        }
    }

    private void continueWithApplyingFilter(int index, FilterPair newFilterPair)
    {
        if(index >= applyingFilters.size())
        {
            return;
        }
        if(removeFilterIfAppliedInList)
        {
            for(int toRemove = applyingFilters.size() - 1; toRemove > index; toRemove--)
            {
                //Lambda needs final variables as reference
                final int indexToRemove = toRemove;
                synchronized(applyingFilters)
                {
                    applyingFilters.remove(indexToRemove);
                }
                fireApplyingFiltersChanged(listener -> listener.removeApplyingFilter(indexToRemove));
            }
            synchronized(applyingFilters)
            {
                applyingFilters.add(newFilterPair);
            }
            fireApplyingFiltersChanged(listener -> listener.addApplyingFilter(newFilterPair.filter));
        } else
        {
            synchronized(applyingFilters)
            {
                applyingFilters.add((index + 1), newFilterPair);
            }
            fireApplyingFiltersChanged(listener -> listener.applyingFiltersChangedPair(applyingFilters));
        }
        applyFilters(index + 1);
    }

    public class FilterPair
    {

        public FilterInterface filter;
        public BufferedImage image;

        public FilterPair(FilterInterface filter, BufferedImage image)
        {
            this.filter = filter;
            this.image = image;
        }

    }
}
