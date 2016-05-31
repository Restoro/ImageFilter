package imagefilter.model;

import imagefilter.filter.FilterInterface;
import java.util.ArrayList;

/**
 *
 * @author hoellinger
 */
public class Adapter
{
    public static void adapt(Model model, PluginModel pluginModel)
    {
        model.changeFilters(new ArrayList<>(), pluginModel.addPluginsChangedListener((r, n, p)
                -> 
                {
                    model.changeFilters(r, n);
        }));
    }
}
