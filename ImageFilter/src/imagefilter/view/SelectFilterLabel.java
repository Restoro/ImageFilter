/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.model.Model;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
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
    private final Color PRESSED_COLOR = new Color(0, 25, 50);
    private final Color UNPRESSED_COLOR;
    private final SelectFilterPanel parent;
    private final FilterInterface filter;
    private final Model model;

    public SelectFilterLabel(Model model, FilterInterface filter, SelectFilterPanel parent)
    {
        this.model = model;
        this.filter = filter;
        this.UNPRESSED_COLOR = this.getBackground();
        this.parent = parent;
        init();
    }

    private void init()
    {
        this.setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setIcon(filter.getPreview());
        setText(filter.toString());
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalTextPosition(JLabel.BOTTOM);
        addMouseListener(new MouseAdapter()
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
                SelectFilterLabel.this.setBackground(PRESSED_COLOR);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
        SelectFilterLabel.this.setBackground(UNPRESSED_COLOR);
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
