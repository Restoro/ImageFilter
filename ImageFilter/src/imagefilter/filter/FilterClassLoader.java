/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.helper.Tools;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginDirectory))
        {
            directoryStream.forEach(x
                    -> 
                    {
                        if(!Files.isDirectory(x) && x.toString().endsWith(".class"))
                        {
                            try
                            {
                                readFile(x, out, buffer);
                                Class c = define(out.toByteArray(), 0, out.size());
                                if(FilterInterface.class.isAssignableFrom(c) && !FilterInterface.class.equals(c))
                                {
                                    FilterInterface filter = (FilterInterface) c.newInstance();
                                    filters.add(filter);
                                }
                            } catch(Throwable t)
                            {
                                Logger.getLogger(FilterClassLoader.class.getName()).log(Level.SEVERE, null, t);
                            }
                        }
            });
        } catch(IOException e)
        {

        }
        return filters;
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
}
