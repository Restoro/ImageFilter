package imagefilter.model;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.FilterClassLoader;
import imagefilter.helper.Tools;
import imagefilter.listener.PluginChangesListener;
import imagefilter.listener.PluginsChangedListener;
import imagefilter.view.PluginsDialog;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * This model has a list of all plugins. If that list changes all observer get notified.
 * This model also manages possible changes. Only when applying the changes the list of plugins changed.
 * @author hoellinger
 */
public class PluginModel
{
    private final Path pluginDirectory;
    private final List<FilterInterface> plugins;
    private final List<FilterInterface> newPlugins;
    private final List<FilterInterface> removedPlugins;
    private final List<FilterInterface> currentPlugins;

    private final List<PluginsChangedListener> pluginsChangedListeners;
    private final List<PluginChangesListener> pluginChangesListeners;

    public PluginModel(Path pluginDirectory)
    {
        this.pluginDirectory = pluginDirectory;
        this.plugins = FilterClassLoader.getFilterClassLoader().getPluginFilters(pluginDirectory);
        this.newPlugins = new ArrayList<>();
        this.removedPlugins = new ArrayList<>();
        this.currentPlugins = new ArrayList<>(plugins);
        pluginsChangedListeners = new ArrayList<>();
        pluginChangesListeners = new LinkedList<>();
    }

    /**
     * Sets the plugin directory if possible. When the input directory has no subfolders
     * /class and/or /img it tries to create them. Occurs some exception the folder 
     * is not selectable. Else it writes the new directory to the settings file
     * and closes the programm because of to high complexity.
     * @param pluginDirectory the new plugin directory
     * @return true if new directory could be selected, else false
     */
    public boolean setPluginDirectory(Path pluginDirectory)
    {
        if(!pluginDirectory.equals(this.pluginDirectory))
        {
            Path classes = pluginDirectory.resolve("class");
            Path imgs = pluginDirectory.resolve("img");
            try
            {
                createNecessaryPluginFolder(imgs);
                createNecessaryPluginFolder(classes);
            } catch(IOException ex)
            {
                return false;
            }
            Tools.writeProperty("pluginDirectory", pluginDirectory.toString());
            //now have to restart
            return true;
        }
        return false;
    }

    /**
     * Adds a new filter. Note that changes have to be applied.
     * @param fi new filter to add
     */
    public void newPlugin(FilterInterface fi)
    {
        if(fi != null)
        {
            newPlugins.add(fi);
            currentPlugins.add(fi);
            firePluginChanges(fi, false);
        }
    }
    /**
     * Removes a filter. Note that changes have to be applied.
     * @param fi filter to remove
     */
    public void removePlugin(FilterInterface fi)
    {
        if(fi != null)
        {
            if(!newPlugins.remove(fi))
            {
                if(plugins.contains(fi))
                {
                    currentPlugins.remove(fi);
                    removedPlugins.add(fi);
                    firePluginChanges(fi, true);
                }
            } else
            {
                currentPlugins.remove(fi);
                firePluginChanges(fi, true);
            }
        }
    }

    /**
     * Undo all changes.
     */
    public void cancleChanges()
    {
        clearChanges();
        fireChangesCanceled(plugins);
    }

    /**
     * Apply all changes.
     */
    public void applayChanges()
    {
        for(FilterInterface fi : removedPlugins)
        {
            plugins.remove(fi);
            try
            {
                removeFilter(fi, FilterClassLoader.getPathFromFilter(fi));
            } catch(IOException ex)
            {
                Logger.getLogger(PluginsDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(FilterInterface fi : newPlugins)
        {
            plugins.add(fi);
            try
            {
                addFilter(fi, FilterClassLoader.getPathFromFilter(fi));
            } catch(IOException ex)
            {
                Logger.getLogger(PluginsDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        firePluginsChanged(removedPlugins, newPlugins, plugins);
        clearChanges();
    }

    private void clearChanges()
    {
        removedPlugins.clear();
        newPlugins.clear();
        currentPlugins.clear();
        currentPlugins.addAll(plugins);
    }

    private void addFilter(FilterInterface fi, Path pathFromFilter) throws IOException
    {
        String imgFileName = Tools.nameWithoutExtension(pathFromFilter) + ".jpg";
        Path img = pluginDirectory.resolve("img").resolve(imgFileName);
        ImageIO.write(Tools.fromImageIconToBufferedImage(fi.getPreview()), "jpg", Files.newOutputStream(img, StandardOpenOption.CREATE, StandardOpenOption.WRITE));

        Path _class = pluginDirectory.resolve("class").resolve(Tools.nameWithExtension(pathFromFilter));
        if(!Files.exists(_class))
        {
            Files.createFile(_class);
        }
        Files.copy(pathFromFilter, Files.newOutputStream(_class));
    }

    private void removeFilter(FilterInterface fi, Path path) throws IOException
    {
        Path _class = pluginDirectory.resolve("class").resolve(path.getFileName());
        Path img = pluginDirectory.resolve("img").resolve(Tools.nameWithoutExtension(path) + ".jpg");

        Files.deleteIfExists(_class);
        Files.deleteIfExists(img);
    }

    private void createNecessaryPluginFolder(Path p) throws IOException
    {
        if(!Files.exists(p))
        {
            Files.createDirectories(p);
        }
    }

    public List<FilterInterface> addPluginsChangedListener(PluginsChangedListener listener)
    {
        pluginsChangedListeners.add(listener);
        return plugins;
    }

    public void removePluginsChangedListener(PluginsChangedListener listener)
    {
        pluginsChangedListeners.remove(listener);
    }

    private void firePluginsChanged(List<FilterInterface> removedPlugins, List<FilterInterface> newPlugins, List<FilterInterface> allPlugins)
    {
        Iterator<PluginsChangedListener> it = pluginsChangedListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                it.next().pluginsChanged(removedPlugins, newPlugins, allPlugins);
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }

    public List<FilterInterface> addPluginChangesListener(PluginChangesListener listener)
    {
        pluginChangesListeners.add(listener);
        return currentPlugins;
    }

    public void removePluginChangesListener(PluginChangesListener listener)
    {
        pluginChangesListeners.remove(listener);
    }

    private void firePluginChanges(FilterInterface filterInterface, boolean remove)
    {
        Iterator<PluginChangesListener> it = pluginChangesListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                if(remove)
                {
                    it.next().removePlugin(filterInterface);
                } else
                {
                    it.next().newPlugin(filterInterface);
                }
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }

    private void fireChangesCanceled(List<FilterInterface> plugins)
    {
        Iterator<PluginChangesListener> it = pluginChangesListeners.iterator();

        while(it.hasNext())
        {
            try
            {
                it.next().changesCanceled(plugins);
            } catch(Throwable t)
            {
                it.remove();
            }
        }
    }
}
