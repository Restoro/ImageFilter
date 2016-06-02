/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import imagefilter.helper.FilterClassLoader;
import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.model.Adapter;
import imagefilter.model.Model;
import imagefilter.model.PluginModel;
import imagefilter.view.MainFrame;
import java.nio.file.Paths;
import javax.swing.JFrame;

/**
 *
 * @author Fritsch
 */
public class ImageFilter
{

    /**
     * This method starts the program. 
     * First it creates the model and sets the filters int the programm. 
     * Then it creates the plugin model with the specified plugin directory
     * in the settings.properties file.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Model model = new Model();
        setFilters(model);
        PluginModel pluginModel = new PluginModel(Paths.get(Tools.getProperty("pluginDirectory")));
        Adapter.adapt(model, pluginModel);
        drawWindow(model, pluginModel);
    }

    private static void drawWindow(Model model, PluginModel pluginModel)
    {
        MainFrame frame = new MainFrame(400, 400, model, pluginModel);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private static void setFilters(Model model)
    {
        FilterClassLoader cl = FilterClassLoader.getFilterClassLoader();
        for(FilterInterface filter : cl.getProjectFilters())
        {
            model.addFilter(filter);
        }
    }
}
