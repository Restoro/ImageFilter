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
 *
 * @author hoellinger
 */
public interface ApplyingFiltersChangedListener
{
    public void addApplyingFilter(FilterInterface filter);
    public void removeApplyingFilter(int index);
    public void applyingFiltersChanged(Collection<FilterInterface> filters);
    public void startApplyingFilter(int index);
    public void finished(FilterPair filter);
}
