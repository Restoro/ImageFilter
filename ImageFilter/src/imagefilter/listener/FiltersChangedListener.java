package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.Collection;

public interface FiltersChangedListener
{
    public void filtersChanged(Collection<FilterInterface> newCollection);
}
