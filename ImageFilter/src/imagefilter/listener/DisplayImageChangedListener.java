/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.model.Model.FilterPair;

/**
 * A listener for changes of the display image object
 * @author hoellinger
 */
public interface DisplayImageChangedListener
{
    /**
     * Informs all listners when the current image to display has changed.
     * @param filterPair the applied filter and the result in form of a FilterPair object
     */
    public void displayImageChanged(FilterPair filterPair);
}
