/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.List;

/**
 * This is a listener for changes of the plugins in a broader sense. When a method of 
 * this class is called, then the plugins don't changes really. This methods only shows 
 * possible changes. The narrow sense is, if there existis for examples more than one
 * dialog to add new plugins. So they get informed when something changes. Only when 
 * applying this changes, the plugins changes really. Threrfore look at PluginsChangedListener.
 * @author hoellinger
 */
public interface PluginChangesListener
{
    /**
     * 
     * @param fi a new possible plugin
     */
    public void newPlugin(FilterInterface fi);
    
    /**
     * A removed plugin. Either a filter wich was added before or a filter of the current plugins
     * @param fi removed filter
     */
    public void removePlugin(FilterInterface fi);
    
    /**
     * When changes canceld, this method gets called.
     * @param plugins the list of plugins with no changes
     */
    public void changesCanceled(List<FilterInterface> plugins);
}
