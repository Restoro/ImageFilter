/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JSlider;

/**
 *
 * @author Fritsch
 */
public class Tools
{
    public static int boundaryCheck(double value, int max)
    {
        return (int) Math.min(Math.max(Math.round(value), 0), 255);
    }

    public static int boundaryCheck(double value)
    {
        return (int) Math.min(Math.max(Math.round(value), 0), 255);
    }

    public static JSlider getJSlider(int minValue, int maxValue, int startValue, int tickSpacing, Hashtable labelTable)
    {
        JSlider newSlider = new JSlider(JSlider.HORIZONTAL,
                minValue, maxValue, startValue);

        newSlider.setMajorTickSpacing(tickSpacing);
        newSlider.setMinorTickSpacing(tickSpacing);
        newSlider.setSnapToTicks(false);
        newSlider.setPaintTicks(true);
        newSlider.setLabelTable(labelTable);

        newSlider.setPaintLabels(true);
        return newSlider;
    }

    public static void setTickSpacingOfJSlider(JSlider slider, int tickSpacing)
    {
        slider.setMajorTickSpacing(tickSpacing);
        slider.setMinorTickSpacing(tickSpacing);
        slider.setPaintTicks(true);
    }

    public static JSlider getJSlider(int minValue, int maxValue, int startValue)
    {
        JSlider newSlider = new JSlider(JSlider.HORIZONTAL,
                minValue, maxValue, startValue);
        newSlider.setPaintLabels(true);
        return newSlider;
    }

    public static BufferedImage convertToStandardType(BufferedImage sourceImage)
    {
        return convertToType(sourceImage, Constants.IMAGE_STANDARD_TYPE);
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType)
    {
        BufferedImage image;
        if(sourceImage.getType() == targetType)
        {
            image = sourceImage;
        } else
        {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    //returns all Classes in specific Package Name
    //used to add filters dynamically
    public static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while(resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for(File directory : dirs)
        {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    //returns all classes with package Name in specific directory
    //checks recursive through directory
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<>();
        if(!directory.exists())
        {
            return classes;
        }
        File[] files = directory.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if(file.getName().endsWith(".class"))
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static URL getResource(String resource)
    {
        return Tools.class.getClassLoader().getResource(resource);
    }

    public static double checkBoundaries(double value, double min, double max)
    {
        if(value < min)
        {
            return min;
        }
        if(value > max)
        {
            return max;
        }
        return value;
    }

    public static String getProperty(String property)
    {
        InputStream in = Tools.class.getClassLoader().getResourceAsStream("settings.properties");
        if(in != null)
        {
            Properties prop = new Properties();
            try
            {
                prop.load(in);
                return prop.getProperty(property);
            } catch(IOException ex)
            {
                System.err.println(ex);
            }
        }
        return null;
    }
    
    public static void fromImageIconToBufferedImage(ImageIcon in, BufferedImage out)
    {
        out.getGraphics().drawImage(in.getImage(), 0, 0, null);
    }
    
    public static BufferedImage fromImageIconToBufferedImage(ImageIcon in)
    {
        BufferedImage out = new BufferedImage(in.getIconWidth(), in.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
        fromImageIconToBufferedImage(in, out);
        return out;
    }
    
    public static String getExtension(Path path)
    {
        String fileName = path.toString();
        int i = fileName.lastIndexOf('.');
        if(i > 0)
        {
            return fileName.substring(i + 1);
        }
        return null;
    }
    

    public static String nameWithoutExtension(Path path)
    {
        String fileName = path.toString();
        int i = fileName.lastIndexOf('.');
        int j = fileName.lastIndexOf(FileSystems.getDefault().getSeparator());
        if(i > 0&&j>0)
        {
            return fileName.substring(j+1, i);
        }
        return null;
    }
    

    public static String nameWithExtension(Path path)
    {
        String fileName = path.toString();
        int j = fileName.lastIndexOf(FileSystems.getDefault().getSeparator());
        if(j>0)
        {
            return fileName.substring(j+1, fileName.length());
        }
        return null;
    }
}
