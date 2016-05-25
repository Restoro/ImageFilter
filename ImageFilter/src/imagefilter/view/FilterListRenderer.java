/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.helper.Tools;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author hoellinger
 */
public class FilterListRenderer extends DefaultListCellRenderer
{
    private int currentOpIndex;
    private static final ImageIcon FILTER_OP_FINISHED;
    private static final ImageIcon FILTER_OP;
    private static final ImageIcon PLACEHOLDER;

    static
    {
        FILTER_OP = new ImageIcon(Tools.getResource("loading.gif"));
        FILTER_OP_FINISHED = new ImageIcon(Tools.getResource("check.png"));
        PLACEHOLDER = new ImageIcon(Tools.getResource("placeholder.png"));
    }

    public FilterListRenderer(int currentOpIndex)
    {
        this.currentOpIndex = currentOpIndex;
    }

    public FilterListRenderer()
    {
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(index < currentOpIndex)
        {
            setIcon(FILTER_OP_FINISHED);
        } else if(index == currentOpIndex)
        {
            setIcon(FILTER_OP);
            FILTER_OP.setImageObserver(list);
        } else
        {
            setIcon(PLACEHOLDER);
        }
        return this;
    }

    public void setCurrentOpIndex(int currentOpIndex)
    {
        this.currentOpIndex = currentOpIndex;
    }

}
