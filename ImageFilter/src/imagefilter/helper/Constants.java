/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.helper;

import java.awt.image.BufferedImage;

/**
 *
 * @author Fritsch
 */
public class Constants {
    //The programm guarantees that an input image to a filter is always
    //of the type 3BYTE_BGR.
    public static final int IMAGE_STANDARD_TYPE = BufferedImage.TYPE_3BYTE_BGR;
}
