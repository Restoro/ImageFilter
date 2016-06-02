package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.Collection;

/**
 * A listener for changes of the filters, which are possible to apply.
 * @author hoellinger
 */
public interface FiltersChangedListener
{
    /**
     * When the Collection of the possible filters to apply has changed, this method gets called.
     * @param newCollection the new collection of all applyable filters
     */
    public void filtersChanged(Collection<FilterInterface> newCollection);
}
