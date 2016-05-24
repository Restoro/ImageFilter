/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import java.awt.image.BufferedImage;

/**
 *
 * @author Fritsch
 */
public class Model {
    private BufferedImage referenceImage;
    private BufferedImage currentImage;

    public BufferedImage getReferenceImage() {
        return referenceImage;
    }

    public void setReferenceImage(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(BufferedImage currentImage) {
        this.currentImage = currentImage;
    }
    
    
}
