/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.filter;

import java.awt.image.BufferedImage;

/**
 *
 * @author Fritsch
 */
public interface FilterInterface {
    public BufferedImage processImage(BufferedImage image);
}
