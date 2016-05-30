/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.listener;

import imagefilter.model.Model.FilterPair;

/**
 *
 * @author hoellinger
 */
public interface DisplayImageChangedListener
{
    public void displayImageChanged(FilterPair filterPair);
}
