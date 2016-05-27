/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.model.Model;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author hoellinger
 */
public class SelectFilterLabel extends JLabel
{
    private final FilterInterface filter;
    private final Model model;

    public SelectFilterLabel(Model model, FilterInterface filter)
    {
        this.model = model;
        this.filter = filter;
        init();
    }

    private void init()
    {
        setHorizontalAlignment(SwingConstants.CENTER);
        setIcon(filter.getPreview());
        setText(filter.toString());
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalTextPosition(JLabel.BOTTOM);
        addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {
                    lblDoubleClick();
                } else
                {
                    lblClick();
                }
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
            }
        });
    }

    private void lblDoubleClick()
    {
        model.addApplyingFilter(filter);
    }

    private void lblClick()
    {
        //model.setDisplayImage(filter);
        
        model.addApplyingFilter(filter);
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.width = 100;
        return d;
    }
}
