package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.List;

/**
 * The lsitener to inform when the plugins have really changed. For none applied changes
 * look at PluginChangesListener
 * @author hoellinger
 */
public interface PluginsChangedListener
{
    /**
     * Gets called when plugins have changed.
     * @param removedPlugins a list of removed plugins
     * @param newPlugins a list of new plugins
     * @param allPlugins the list of all current plugins
     */
    public void pluginsChanged(List<FilterInterface> removedPlugins, List<FilterInterface> newPlugins, List<FilterInterface> allPlugins);
}
