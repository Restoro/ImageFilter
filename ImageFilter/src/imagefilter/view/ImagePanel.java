/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.model.Model;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author Fritsch
 */
public class ImagePanel extends JPanel
{
    private Image image;
    private final Model model;

    public ImagePanel(Model model, Image image)
    {
        this.image = image;
        this.model = model;
        model.addDisplayImageChangedListener(img
                -> 
                {
                    setImage(img);
        });
    }

    public ImagePanel(Model model)
    {
        this(model, null);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void setImage(Image image)
    {
        System.out.println("Image changed");
        this.image = image;
        repaint();
    }
}
