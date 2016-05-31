/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import java.awt.Component;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

/**
 *
 * @author hoellinger
 */
public class PluginsListRenderer extends DefaultListCellRenderer
{
    private static final FontRenderContext FRC = new FontRenderContext(new AffineTransform(),true,true);
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText("   " + getText());
        ImageIcon img = ((FilterInterface)value).getPreview();
        int textWidth = (int) getFont().getStringBounds(getText(), FRC).getWidth();
        setIcon(img);
        setHorizontalTextPosition(SwingConstants.LEFT);
        setIconTextGap(list.getWidth() - textWidth - img.getIconWidth() - 20);
        return this;
    }
}
