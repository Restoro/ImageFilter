/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

import imagefilter.filter.FilterInterface;
import imagefilter.helper.FilterOperations;
import imagefilter.helper.FilterTask;
import imagefilter.listener.ApplyingFiltersChangedListener;
import imagefilter.listener.FiltersChangedListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import imagefilter.listener.DisplayImageChangedListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author Fritsch
 */
public class Model {

    private int lastSelectedIndex=-1;
    private BufferedImage referenceImage;
    private BufferedImage displayImage;
    private volatile int currentImage;

    private final Set<FilterInterface> allFilters;
    private final List<FilterPair> applyingFilters;

    private final List<DisplayImageChangedListener> displayImageChangedListeners;
    private final List<FiltersChangedListener> filtersChangedListeners;
    private final List<ApplyingFiltersChangedListener> applyingFiltersChangedListeners;

    private final ScheduledExecutorService filterProcessor;
    private final FilterOperations filterOpRunnable;
    private ScheduledFuture filterResult;

    public Model() {
        this.displayImageChangedListeners = new LinkedList<>();
        this.filtersChangedListeners = new LinkedList<>();
        this.applyingFiltersChangedListeners = new LinkedList<>();
        this.allFilters = new HashSet<>();
        this.applyingFilters = new LinkedList<>();
        this.filterProcessor = Executors.newSingleThreadScheduledExecutor();
        this.filterOpRunnable = new FilterOperations(this, applyingFilters);
    }

    public BufferedImage getDisplayImage() {
        return displayImage;
    }

    public BufferedImage getReferenceImage() {
        return referenceImage;
    }

    public void setReferenceImage(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
        this.currentImage = -1;
        deleteAllApplyingFilters();
        setDisplayImage(referenceImage);
    }

    public void setFilters(Collection<FilterInterface> filters) {
        this.allFilters.clear();
        this.allFilters.addAll(filters);
        fireFiltersChanged();
    }

    public void addFilter(FilterInterface filter) {
        if (this.allFilters.add(filter)) {
            fireFiltersChanged();
        }
    }

    public void removeFilter(FilterInterface filter) {
        if (this.allFilters.remove(filter)) {
            fireFiltersChanged();
        }
    }

    public Collection<FilterInterface> addFiltersChangedListener(FiltersChangedListener listener) {
        filtersChangedListeners.add(listener);
        return allFilters;
    }

    public void removeFiltersChangedListener(FiltersChangedListener listener) {
        filtersChangedListeners.remove(listener);
    }

    private void fireFiltersChanged() {
        Iterator<FiltersChangedListener> it = filtersChangedListeners.iterator();

        while (it.hasNext()) {
            try {
                it.next().filtersChanged(allFilters);
            } catch (Throwable t) {
                it.remove();
            }
        }
    }

    public void setDisplayImage(BufferedImage image) {
        this.displayImage = image;
        fireDisplayImageChanged();
        lastSelectedIndex = applyingFilters.size()-1;
    }

    public void setDisplayImage(FilterInterface filter) {
        if (referenceImage == null) {
            return;
        }
        setDisplayImage(filter.processImage(getCurrentImage()));
    }

    public void setDisplayImage(int index) {
        if (currentImage < index || index < 0) {
            return;
        }
        setDisplayImage(applyingFilters.get(index).image);
        lastSelectedIndex = index;
    }

    public void addDisplayImageChangedListener(DisplayImageChangedListener listener) {
        displayImageChangedListeners.add(listener);
    }

    public void removeDisplayImageChangedListener(DisplayImageChangedListener listener) {
        displayImageChangedListeners.remove(listener);
    }

    private void fireDisplayImageChanged() {
        Iterator<DisplayImageChangedListener> it = displayImageChangedListeners.iterator();

        while (it.hasNext()) {
            try {
                it.next().displayImageChanged(displayImage);
            } catch (Throwable t) {
                it.remove();
            }
        }
    }

    private void deleteAllApplyingFilters() {
        applyingFilters.clear();
        fireApplyingFiltersChanged(listener -> listener.applyingFiltersChanged(new LinkedList<>()));
    }

    public void addApplyingFilter(FilterInterface filter) {
        if (filter == null) {
            return;
        }
        if(lastSelectedIndex != applyingFilters.size()-1 && lastSelectedIndex != -1)
        {
            continueWithApplyingFilter(lastSelectedIndex);
        }
        fireApplyingFiltersChanged(listener -> listener.addApplyingFilter(filter));
        synchronized (applyingFilters) {
            applyingFilters.add(new FilterPair(filter, null));
        }
        applyFilters();
    }

    public void removeApplyingFilter(int index) {
        if (index >= applyingFilters.size() || index < 0) {
            return;
        }
        fireApplyingFiltersChanged(listener -> listener.removeApplyingFilter(index));
        synchronized (applyingFilters) {
            applyingFilters.remove(index);
        }
        applyFilters(index);
    }

    private void applyFilters() {
        if (filterResult == null || filterResult.isCancelled() || filterResult.isDone()) {
            filterOpRunnable.setCurrent(currentImage);
            filterResult = filterProcessor.schedule(filterOpRunnable, 0, TimeUnit.MILLISECONDS);
        }
    }

    private void applyFilters(int index) {
        if ((currentImage + 1) >= index) {
            filterResult.cancel(true);
            filterResult = null;
            currentImage = index - 1;
            applyFilters();
        }
    }
    
    private void continueWithApplyingFilter(int index) {
        if (index >= applyingFilters.size()) {
            return;
        }
        for (int toRemove = applyingFilters.size() - 1; toRemove > index; toRemove--) {
            //Lambda needs final variables as reference
            final int indexToRemove = toRemove;
            synchronized (applyingFilters) {
                applyingFilters.remove(indexToRemove);
            }
            fireApplyingFiltersChanged(listener -> listener.removeApplyingFilter(indexToRemove));
        }
        applyFilters(index);
    }

    public BufferedImage getCurrentImage() {
        return currentImage == -1 ? referenceImage : applyingFilters.get(currentImage).image;
    }

    public void addApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener) {
        applyingFiltersChangedListeners.add(listener);
    }

    public void removeApplyingFiltersChangedListener(ApplyingFiltersChangedListener listener) {
        applyingFiltersChangedListeners.remove(listener);
    }

    private void fireApplyingFiltersChanged(Consumer<ApplyingFiltersChangedListener> consumer) {
        Iterator<ApplyingFiltersChangedListener> it = applyingFiltersChangedListeners.iterator();

        while (it.hasNext()) {
            try {
                consumer.accept(it.next());
            } catch (Throwable t) {
                it.remove();
            }
        }
    }

    public void appliedFilter(BufferedImage img) {
        synchronized (applyingFilters) {
            currentImage++;
            applyingFilters.get(currentImage).image = img;
        }
        setDisplayImage(img);
    }

    public void finishedApplyingFilters() {
        synchronized (applyingFilters) {
            currentImage = applyingFilters.size() - 1;
        }
        fireApplyingFiltersChanged(listener -> listener.finished());
    }

    public FilterPair getApplyingFilterAndSourceImg(int current) {
        return new FilterPair(applyingFilters.get(current + 1).filter, getCurrentImage());
    }

    public void startApplyingNextFilter() {
        fireApplyingFiltersChanged(listener -> listener.startApplyingFilter(currentImage + 1));
    }

    public class FilterPair {

        public FilterInterface filter;
        public BufferedImage image;

        public FilterPair(FilterInterface filter, BufferedImage image) {
            this.filter = filter;
            this.image = image;
        }

    }
}
