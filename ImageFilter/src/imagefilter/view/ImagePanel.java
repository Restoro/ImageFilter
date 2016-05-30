/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.model.Model;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Fritsch
 */
public class ImagePanel extends JPanel
{
    private BufferedImage image;
    private final Model model;

    public ImagePanel(Model model, BufferedImage image)
    {
        this.image = image;
        this.model = model;
        if(model != null)
        {
            model.addDisplayImageChangedListener(filterPair
                    -> 
                    {
                        setImage(filterPair.image);
            });
        }
    }

    public ImagePanel(Model model)
    {
        this(model, null);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        BufferedImage img = scaleImage(image);
        if(img != null)
        {
            g.drawImage(img, getMiddle(img.getWidth(), this.getWidth()), getMiddle(img.getHeight(), this.getHeight()), null);
        }
    }

    public void setImage(BufferedImage image)
    {
        System.out.println("Image changed");
        this.image = image;
        repaint();
    }

    private BufferedImage scaleImage(BufferedImage image)
    {
        if(image == null)
        {
            return null;
        }
        int iWidth = image.getWidth();
        int iHeight = image.getHeight();
        int width = this.getWidth();
        int height = this.getHeight();
        boolean b = false;
        if(iWidth > width)
        {
            iHeight = width * iHeight / iWidth;
            iWidth = width;
            b = true;
        }
        if(iHeight > height)
        {
            iWidth = height * iWidth / iHeight;
            iHeight = height;
            b = true;
        }
        return b ? getScaledImage(image, iWidth, iHeight) : image;
    }

    private int getMiddle(int img, int panel)
    {
        return panel / 2 - img / 2;
    }

    private static BufferedImage getScaledImage(BufferedImage image, int width, int height)
    {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
                image,
                new BufferedImage(width, height, image.getType()));
    }
}
