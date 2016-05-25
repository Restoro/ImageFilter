package imagefilter.view;

import imagefilter.filter.FilterInterface;
import imagefilter.model.Model;
import javax.swing.JList;

/**
 *
 * @author hoellinger
 */
public class FilterList extends JList<FilterInterface>
{
    private final Model model;
    
    public FilterList(Model model)
    {
        this.model = model;
    }
}
