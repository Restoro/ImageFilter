package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.model.Model;
import imagefilter.model.Model.FilterPair;
import java.util.Collection;
import javax.swing.DefaultListModel;

/**
 *
 * @author hoellinger
 */
public class FilterListModel extends DefaultListModel<FilterInterface>
{
    private final Model model;
    private final FilterListRenderer listRenderer;

    public FilterListModel(Model model, FilterListRenderer listRenderer)
    {
        this.model = model;
        model.addApplyingFiltersChangedListener(new ApplyingFiltersChanged());
        this.listRenderer = listRenderer;
    }

    public void delete(int i)
    {
        model.removeApplyingFilter(i);
    }

    public void select(int i)
    {
        model.setDisplayImage(i);
    }

    private class ApplyingFiltersChanged implements ApplyingFiltersChangedListener
    {
        @Override
        public void addApplyingFilter(FilterInterface filter)
        {
            FilterListModel.this.addElement(filter);
        }

        @Override
        public void removeApplyingFilter(int index)
        {
            FilterListModel.this.remove(index);
            
        }

        @Override
        public void applyingFiltersChanged(Collection<FilterInterface> filters)
        {
            FilterListModel.this.removeAllElements();
            for(FilterInterface f : filters)
            {
                FilterListModel.this.addElement(f);
            }
        }

        @Override
        public void startApplyingFilter(int index)
        {
            FilterListModel.this.listRenderer.setCurrentOpIndex(index);
        }

        @Override
        public void finished(FilterPair filterPair)
        {
            FilterListModel.this.listRenderer.setCurrentOpIndex(FilterListModel.this.size());
        }

        @Override
        public void applyingFiltersChangedPair(Collection<FilterPair> filters) {
            FilterListModel.this.removeAllElements();
            for(FilterPair f : filters)
            {
                FilterListModel.this.addElement(f.filter);
            }
        }

    }
}
