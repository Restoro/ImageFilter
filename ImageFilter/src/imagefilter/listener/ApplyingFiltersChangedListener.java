/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import imagefilter.model.Model.FilterPair;
import java.util.Collection;

/**
 * A listener for all changes of the applying filters
 * @author hoellinger
 */
public interface ApplyingFiltersChangedListener
{
    /**
     * A new filter is applied.
     * @param filter the new filter
     */
    public void addApplyingFilter(FilterInterface filter);
    
    /**
     * A filter with the specified index has been removed.
     * @param index the index of the removed filter in the list
     */
    public void removeApplyingFilter(int index);
    
    /**
     * This method is called when several filters are removed or/and added
     * @param filters the new collection of applied filters
     */
    public void applyingFiltersChanged(Collection<FilterInterface> filters);
    
    /**
     * Like the method applyingFiltersChanged(...) only with FilterPairs
     * @param filters 
     */
    public void applyingFiltersChangedPair(Collection<FilterPair> filters);
    
    /**
     * When a new filter is applied, this method gets called.
     * @param index the index of the currently applied filter in the list
     */
    public void startApplyingFilter(int index);
    
    /**
     * This method is called, when all filters were applied successfully.
     * @param filter the last filter pair in the list
     */
    public void finished(FilterPair filter);
}
