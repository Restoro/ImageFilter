/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author hoellinger
 */
public class FilterList extends JList<FilterInterface>
{
    public FilterList(FilterListModel filterListModel)
    {
        super(filterListModel);
        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                System.out.println("Delete ");
                if(e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                    int i = FilterList.this.getSelectedIndex();
                    if(i != -1)
                    {
                        System.out.println("Delete " + i);
                        filterListModel.delete(i);
                    }
                }
            }

        });
        this.addListSelectionListener((ListSelectionEvent e) ->
        {
                    int i = FilterList.this.getSelectedIndex();
                    filterListModel.select(i);
        });
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.width = 200;
        return d;
    }

}
