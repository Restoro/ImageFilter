/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import imagefilter.filter.FilterInterface;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The main class to load Plugins. It overrids ClassLoader, because the main
 * method to create a new class, which is not in this project, is protected.
 *
 * @author hoellinger
 */
public class FilterClassLoader extends ClassLoader
{
    private static FilterClassLoader cl;
    private static final HashMap<Path, FilterInterface> ALLREADY_LOADED_FILTERS = new HashMap<>();
    private static final HashMap<FilterInterface, Path> ALLREADY_LOADED_FILTERS_REVERSED = new HashMap<>();

    private FilterClassLoader()
    {

    }

    /**
     * Returns the FilterClassLoader in a singelton way.
     *
     * @return the FilterClassLoader
     */
    public static FilterClassLoader getFilterClassLoader()
    {
        if(cl == null)
        {
            cl = new FilterClassLoader();
        }
        return cl;
    }

    /**
     * Returns all subclasses of FilterInterface in the package
     * imagefilter.filter. It also sets a preview if available.
     *
     * @return a list of FilterInterface in the package imagefilter.filter
     */
    public List<FilterInterface> getProjectFilters()
    {
        List<FilterInterface> filters = new ArrayList<>();
        try
        {
            Class[] classes = Tools.getClasses("imagefilter.filter");
            for(Class filter : classes)
            {
                if(FilterInterface.class.isAssignableFrom(filter) && !FilterInterface.class.equals(filter))
                {
                    try
                    {
                        FilterInterface fi = (FilterInterface) filter.newInstance();
                        fi.setPreview(new ImageIcon(Tools.getResource("SampleImages/" + filter.getSimpleName() + ".jpg")));
                        filters.add(fi);
                    } catch(InstantiationException | IllegalAccessException ex)
                    {
                        System.out.println("Error loading Filter:" + ex.getMessage());
                    }
                }
            }
        } catch(ClassNotFoundException | IOException ex)
        {
            Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filters;
    }

    /**
     * Returns a list of all possible FilterInterface in the plugin directory.
     * To handle the pluginDirectory param, the plugin directory must have
     * subfolders of /class and /img. In addition each .class file must have an
     * equivalent with the same name with a .jpg extension in /img to be
     * recognized.
     *
     * @param pluginDirectory the plugin directory with subfolder /class and
     * /img
     * @return
     */
    public List<FilterInterface> getPluginFilters(Path pluginDirectory)
    {
        List<FilterInterface> filters = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];

        //Example of input: key: InverFilter; value: .../InverFilter.class
        Map<String, Path> classPathes = new HashMap<>();
        //Example of input: key: InverFilter; value: .../InverFilter.jpg
        Map<String, Path> imgPathes = new HashMap<>();
        Path classes = pluginDirectory.resolve("class");
        Path imgs = pluginDirectory.resolve("img");
        try
        {
            Files.walk(classes, 1)
                    .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".class"))
                    .forEach(p
                            -> 
                            {
                                classPathes.put(Tools.nameWithoutExtension(classes.relativize(p)), p);
                    });
            Files.walk(imgs, 1)
                    .filter(p -> !Files.isDirectory(p) && isJPGorPNG(p.toString()))
                    .forEach(p
                            -> 
                            {
                                imgPathes.put(Tools.nameWithoutExtension(imgs.relativize(p)), p);
                    });
        } catch(IOException ex)
        {
        }

        //after this code there are only plugins, when there is a .class and a .jpg file
        classPathes.forEach((s, p)
                -> 
                {
                    Path img = imgPathes.get(s);
                    if(img != null)
                    {
                        try
                        {
                            //reads file of path p -> stores bytes in out, buffer = bytes per read operation
                            readFile(p, out, buffer);
                            //defines class of bytes, if already defined, a linkage error is thrown
                            Class c = define(out.toByteArray(), 0, out.size());
                            if(FilterInterface.class.isAssignableFrom(c) && !FilterInterface.class.equals(c))
                            {
                                FilterInterface filter = (FilterInterface) c.newInstance();
                                ImageIcon ic = getPreview(img);
                                filter.setPreview(ic);
                                filters.add(filter);
                                ALLREADY_LOADED_FILTERS.put(p, filter);
                                ALLREADY_LOADED_FILTERS_REVERSED.put(filter, p);
                            }
                        } catch(LinkageError er)
                        {
                            //when tried to load a class which is already loaded
                            filters.add(ALLREADY_LOADED_FILTERS.get(p));
                        } catch(IOException | InstantiationException | IllegalAccessException ex)
                        {
                            Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
        });
        return filters;
    }

    /**
     * Like the mehod getPluginFilters just for one. This method primarly exists
     * to load add new plugins. Therefore there is no image for preview needed.
     * And therefore the param is the location of the class file.
     *
     * @param path the path of the .class file
     * @return returns a FilterInterface depending of the input path
     */
    public FilterInterface getSingleFilterInterface(String path)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        Path p = Paths.get(path);
        try
        {
            readFile(p, out, buffer);
            Class c = define(out.toByteArray(), 0, out.size());
            if(FilterInterface.class.isAssignableFrom(c) && !FilterInterface.class.equals(c))
            {
                FilterInterface filter = (FilterInterface) c.newInstance();
                ALLREADY_LOADED_FILTERS.put(p, filter);
                ALLREADY_LOADED_FILTERS_REVERSED.put(filter, p);
                return filter;
            }
        } catch(LinkageError er)
        {
            return ALLREADY_LOADED_FILTERS.get(p);
        } catch(Throwable t)
        {
        }
        return null;
    }

    /**
     * Returns programm filters as well as plugin filters. The plugin directory input
     * must have /class and /img subfolders like in getPluginFilters(Path pluginDirectory).
     * @param pluginDirectory the plugin directory
     * @return a list of FilterInterface
     */
    public List<FilterInterface> getAllFilters(Path pluginDirectory)
    {
        List<FilterInterface> filters = getProjectFilters();
        filters.addAll(getPluginFilters(pluginDirectory));
        return filters;
    }

    /**
     * When you have a plugin filter and you want the path it is located in
     * the plugin directory. If the input filter is no plug in, for example it 
     * is a programm filter, this method returns null.
     * @param fi the plugin interace
     * @return path of filter in plugin directory or null if it is no plugin
     */
    public static Path getPathFromFilter(FilterInterface fi)
    {
        return ALLREADY_LOADED_FILTERS_REVERSED.get(fi);
    }

    //reads file and stores data in ByteArrayOutputStream out.
    private void readFile(Path file, ByteArrayOutputStream out, byte[] buffer) throws IOException
    {
        if(buffer == null)
        {
            buffer = new byte[1024];
        }
        BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file));
        out.reset();

        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

    //Calls definClass(...) of ClassLoader. Throws LinkageError if file has already been loaded
    private Class define(byte[] b, int offset, int length)
    {
        return this.defineClass(null, b, offset, length);
    }

    private boolean isJPGorPNG(String fileName)
    {
        int i = fileName.lastIndexOf('.');
        if(i > 0)
        {
            String s = fileName.substring(i + 1);
            if(s.equals("png") || s.equals("jpg"))
            {
                return true;
            }
        }
        return false;
    }

    //Reads image from disc and transforms it into an ImageIcon
    private ImageIcon getPreview(Path img) throws IOException
    {
        ImageIcon ic = new ImageIcon(ImageIO.read(img.toFile()));
        return ic;
    }
}
