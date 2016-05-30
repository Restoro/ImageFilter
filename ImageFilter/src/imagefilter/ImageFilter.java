/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import imagefilter.helper.FilterClassLoader;
import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.model.Model;
import imagefilter.view.MainFrame;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;

/**
 *
 * @author Fritsch
 */
public class ImageFilter
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        Model model = new Model();
        setFilters(model, Paths.get(Tools.getProperty("pluginDirectory")));
        drawWindow(model);
    }

    private static void drawWindow(Model model)
    {
        MainFrame frame = new MainFrame(400, 400, model);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private static void setFilters(Model model, Path pluginDirectory)
    {
        FilterClassLoader cl = FilterClassLoader.getFilterClassLoader();
        for(FilterInterface filter : cl.getAllFilters(pluginDirectory))
        {
            model.addFilter(filter);
        }
    }
}
