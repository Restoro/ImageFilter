/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter;

import imagefilter.helper.FilterClassLoader;
import imagefilter.filter.FilterInterface;
import imagefilter.model.Model;
import imagefilter.view.MainFrame;
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
        setFilters(model, args == null || args.length == 0 ? "" : args[0]);
        drawWindow(model);
    }

    private static void drawWindow(Model model)
    {
        MainFrame frame = new MainFrame(400, 400, model);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private static void setFilters(Model model, String pluginDirectory)
    {
        FilterClassLoader cl = new FilterClassLoader(pluginDirectory);
        for(FilterInterface filter : cl.getAllFilters())
        {
            model.addFilter(filter);
        }
    }
}
