package imagefilter.model;

import java.util.ArrayList;

/**
 * A adapter to combine Model and PluginModel. So it is a Controller of Model and 
 * a Viewer of PluginModel. And so this components are independent.
 * @author hoellinger
 */
public class Adapter
{
    /**
     * Adapts the plugin model to the model.
     * @param model
     * @param pluginModel 
     */
    public static void adapt(Model model, PluginModel pluginModel)
    {
        model.changeFilters(new ArrayList<>(), pluginModel.addPluginsChangedListener((r, n, p)
                -> 
                {
                    model.changeFilters(r, n);
        }));
    }
}
