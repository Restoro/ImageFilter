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

public class FilterClassLoader extends ClassLoader
{
    private static FilterClassLoader cl;
    private static final HashMap<Path, FilterInterface> ALLREADY_LOADED_FILTERS = new HashMap<>();
    private static final HashMap<FilterInterface, Path> ALLREADY_LOADED_FILTERS_REVERSED = new HashMap<>();

    private FilterClassLoader()
    {

    }

    public static FilterClassLoader getFilterClassLoader()
    {
        if(cl == null)
        {
            cl = new FilterClassLoader();
        }
        return cl;
    }

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
                    try{
                    filters.add((FilterInterface) filter.newInstance());
                    } catch(InstantiationException | IllegalAccessException ex)
                    {
                        System.out.println("Error loading Filter:"+ex.getMessage());
                    }
                }
            }
        } catch(ClassNotFoundException | IOException ex)
        {
            Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filters;
    }

    public List<FilterInterface> getPluginFilters(Path pluginDirectory)
    {
        List<FilterInterface> filters = new ArrayList<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];

        Map<String, Path> classPathes = new HashMap<>();
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
                                String path = classes.relativize(p).toString();
                                String nas = nameWithoutExtension(path);
                                classPathes.put(nameWithoutExtension(classes.relativize(p).toString()), p);
                    });
            Files.walk(imgs, 1)
                    .filter(p -> !Files.isDirectory(p) && isJPGorPNG(p.toString()))
                    .forEach(p
                            -> 
                            {

                                String path = imgs.relativize(p).toString();
                                String nas = nameWithoutExtension(path);
                                imgPathes.put(nameWithoutExtension(imgs.relativize(p).toString()), p);
                    });
        } catch(IOException ex)
        {
            //Logger.getLogger(PluginsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        classPathes.forEach((s, p)
                -> 
                {
                    Path img = imgPathes.get(s);
                    if(img != null)
                    {
                        try
                        {
                            readFile(p, out, buffer);
                            Class c = define(out.toByteArray(), 0, out.size());
                            if(FilterInterface.class.isAssignableFrom(c) && !FilterInterface.class.equals(c))
                            {
                                FilterInterface filter = (FilterInterface) c.newInstance();
                                ImageIcon ic = getPreview(img);
                                Object o = c.getDeclaredMethods();
                                filter.getPreview();
                                filter.setPreview(ic);
                                filters.add(filter);
                                ALLREADY_LOADED_FILTERS.put(p, filter);
                                ALLREADY_LOADED_FILTERS_REVERSED.put(filter, p);
                            }
                        } catch(LinkageError er)
                        {
                            filters.add(ALLREADY_LOADED_FILTERS.get(p));
                        } catch(IOException | InstantiationException | IllegalAccessException ex)
                        {
                            Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
        });
        return filters;
    }

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
            int a = 0;
        }
        return null;
    }

    public List<FilterInterface> getAllFilters(Path pluginDirectory)
    {
        List<FilterInterface> filters = getProjectFilters();
        filters.addAll(getPluginFilters(pluginDirectory));
        return filters;
    }

    public static Path getPathFromFilter(FilterInterface fi)
    {
        return ALLREADY_LOADED_FILTERS_REVERSED.get(fi);
    }

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

    private Class define(byte[] b, int offset, int length)
    {
        return this.defineClass(null, b, offset, length);
    }

    private String nameWithoutExtension(String fileName)
    {
        int i = fileName.lastIndexOf('.');
        if(i > 0)
        {
            return fileName.substring(0, i);
        }
        return "";
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

    private ImageIcon getPreview(Path img) throws IOException
    {
        ImageIcon ic = new ImageIcon(ImageIO.read(img.toFile()));
        return ic;
    }
}
