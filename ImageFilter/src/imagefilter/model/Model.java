/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import imagefilter.filter.FilterInterface;
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
import java.util.function.Consumer;

/**
 *
 * @author Fritsch
 */
public class Model
{
    private BufferedImage referenceImage;
    private BufferedImage currentImage;
    private BufferedImage displayImage;

    private final Set<FilterInterface> allFilters;
    private final List<FilterInterface> applyingFilters;

    private final List<DisplayImageChangedListener> displayImageChangedListeners;
    private final List<FiltersChangedListener> filtersChangedListeners;
    private final List<ApplyingFiltersChangedListener> applyingFiltersChangedListeners;

    public Model()
    {
        this.displayImageChangedListeners = new LinkedList<>();
        this.filtersChangedListeners = new LinkedList<>();
        this.applyingFiltersChangedListeners = new LinkedList<>();
        this.allFilters = new HashSet<>();
        this.applyingFilters = new LinkedList<>();
    }

    public BufferedImage getReferenceImage()
    {
        return referenceImage;
    }

    public void setReferenceImage(BufferedImage referenceImage)
    {
        this.referenceImage = referenceImage;
        this.currentImage = referenceImage;
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
        setDisplayImage(filter.processImage(currentImage));
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

    }

    public void addApplyingFilter(FilterInterface filter)
    {
        applyingFilters.add(filter);
        fireApplyingFiltersChanged(listener->listener.addApplyingFilter(filter));
    }

    public void removeApplyingFilter(int index)
    {
        applyingFilters.remove(index);
        fireApplyingFiltersChanged(listener->listener.removeApplyingFilter(index));
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
}
