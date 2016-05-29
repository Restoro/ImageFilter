/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.Tools;
import imagefilter.model.Model;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 *
 * @author hoellinger
 */
public class SelectFilterPanel extends JPanel
{
    private final JButton btnLeft;
    private final JButton btnRight;
    private final JScrollPane scrpFilters;
    private final JScrollBar scrpFilterHorScrl;
    private final JPanel panFilters;
    private final Model model;

    public SelectFilterPanel(Model model)
    {
        this.btnLeft = new JButton();
        this.btnRight = new JButton();
        this.panFilters = new JPanel();
        this.scrpFilters = new JScrollPane();
        this.scrpFilterHorScrl = scrpFilters.getHorizontalScrollBar();
        init();

        this.model = model;
        updateFilters(model.addFiltersChangedListener(collection -> updateFilters(collection)));
    }

    private void init()
    {
        this.setLayout(new BorderLayout());

        btnLeft.setIcon(new ImageIcon(Tools.getResource("scrollleft.png")));
        btnLeft.addActionListener(a -> scrollLeft());

        btnRight.setIcon(new ImageIcon(Tools.getResource("scrollright.png")));
        btnRight.addActionListener(a -> scrollRight());

        scrpFilters.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrpFilters.setViewportView(panFilters);
        scrpFilterHorScrl.setBlockIncrement(105);

        panFilters.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.add(btnLeft, BorderLayout.WEST);
        this.add(scrpFilters, BorderLayout.CENTER);
        this.add(btnRight, BorderLayout.EAST);
    }

    private void scrollLeft()
    {
        scrpFilterHorScrl.setValue(scrpFilterHorScrl.getValue() - scrpFilterHorScrl.getBlockIncrement());
    }

    private void scrollRight()
    {
        scrpFilterHorScrl.setValue(scrpFilterHorScrl.getValue() + scrpFilterHorScrl.getBlockIncrement());
    }

    private void updateFilters(Collection<FilterInterface> filters)
    {
        panFilters.removeAll();

        for(FilterInterface filter : filters)
        {
            panFilters.add(new SelectFilterLabel(model, filter, this));
        }

        this.validate();
    }
}
