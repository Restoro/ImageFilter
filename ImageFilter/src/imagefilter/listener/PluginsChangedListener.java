package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.List;

/**
 *
 * @author hoellinger
 */
public interface PluginsChangedListener
{
    public void pluginsChanged(List<FilterInterface> removedPlugins, List<FilterInterface> newPlugins, List<FilterInterface> allPlugins);
}
