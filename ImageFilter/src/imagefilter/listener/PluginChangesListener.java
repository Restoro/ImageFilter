/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.filter.FilterInterface;
import java.util.List;

/**
 *
 * @author hoellinger
 */
public interface PluginChangesListener
{
    public void newPlugin(FilterInterface fi);
    public void removePlugin(FilterInterface fi);
    public void changesCanceled(List<FilterInterface> plugins);
}
