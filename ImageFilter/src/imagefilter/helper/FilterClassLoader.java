/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import imagefilter.filter.FilterInterface;
import java.awt.image.BufferedImage;
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

public class FilterClassLoader extends ClassLoader
{
    private Path pluginDirectory;

    public Path getPluginDirectory()
    {
        return pluginDirectory;
    }

    public void setPluginDirectory(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
    }

    public FilterClassLoader(String pluginDirectory)
    {
        this(Paths.get(pluginDirectory));
    }

    public FilterClassLoader(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
    }

    public FilterClassLoader()
    {

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
                    filters.add((FilterInterface) filter.newInstance());
                }
            }
        } catch(ClassNotFoundException | IOException | InstantiationException | IllegalAccessException ex)
        {
            Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filters;
    }

    public List<FilterInterface> getPluginFilters()
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
                                filter.setPreview(getPreview(img));
                                filters.add(filter);
                            }
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

        try
        {
            readFile(Paths.get(path), out, buffer);
            Class c = define(out.toByteArray(), 0, out.size());
            if(FilterInterface.class.isAssignableFrom(c) && !FilterInterface.class.equals(c))
            {
                return (FilterInterface) c.newInstance();
            }
        } catch(Throwable t)
        {
        }
        return null;
    }

    public List<FilterInterface> getAllFilters()
    {
        List<FilterInterface> filters = getProjectFilters();
        filters.addAll(getPluginFilters());
        return filters;
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

    private BufferedImage getPreview(Path img) throws IOException
    {
        return ImageIO.read(img.toFile());
    }
}
