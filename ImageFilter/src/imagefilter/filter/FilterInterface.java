/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import imagefilter.model.Setting;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**This is the basic interface, which all filters have to implement. 
 * It contains all the necassery methods the programm needs.
 *
 * @author Fritsch
 */
public interface FilterInterface {
    /**This methode creates a new BufferedImage of the size depeding on the input.
     * Then it applies the filter and store the data in the output image.
     * After it applied the filter, this method returns the new one.
     * 
     * @param image the input image of type BufferedImage.TYPE_3BYTE_BGR
     * @return the output image with the applied filter
     * 
    */
    public BufferedImage processImage(BufferedImage image);
    
    /**
     * This method returns a preview of this filter.
     * @return a preview of the filter.
     */
    public ImageIcon getPreview();
    
    /**
     * This method sets the preview of a filter. This method should only be called, 
     * after creating a new instance of this object.
     * @param preview the preview of this filter
     */
    public void setPreview(ImageIcon preview);
    
    /**Returns the settings of this images instance. You don't have to set the
     * settings again, because it is call by reference.
     * 
     * @return the settings of this images instance
     */
    public Setting[] getSettings();
    
    /**
     * This method should be overridden, even you do not have to, because this 
     * method is called very often to display the filters name.
     * @return the name of the filter
     */
    @Override
    public String toString();
}
