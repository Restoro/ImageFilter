/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import imagefilter.filter.FilterInterface;
import imagefilter.listener.FiltersChangedListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import imagefilter.listener.DisplayImageChangedListener;

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

    public Model()
    {
        this.displayImageChangedListeners = new LinkedList<>();
        this.filtersChangedListeners = new LinkedList<>();
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
        setDisplayImage(filter.processImage(currentImage));
    }
    
    public Collection<FilterInterface> addDisplayImageChangedListener(DisplayImageChangedListener listener)
    {
        displayImageChangedListeners.add(listener);
        return allFilters;
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
        fireApplyingFiltersChanged();
    }
    
    private void fireApplyingFiltersChanged()
    {
        
    }
}
