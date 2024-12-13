package com.hi.live.oflineModels.Filters;


import com.hi.live.R;
import com.hi.live.oflineModels.gif.GifRoot;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    public static List<FilterRoot> filterRoots = new ArrayList<>();
    public static List<GifRoot> gifRoots = new ArrayList<>();

    public static void setFilters() {

        FilterUtils.filterRoots.add(new FilterRoot(0, "None"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.bubble, "Bubbles"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.fires, "Fires"));
        FilterUtils.filterRoots.add(new FilterRoot(R.drawable.heartsfilter_a, "Heart"));
    }

    public static void setGifs() {

        FilterUtils.gifRoots.add(new GifRoot(0, "None"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif1, "Bubbles1"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif2_a, "Bubbles2"));
        FilterUtils.gifRoots.add(new GifRoot(R.drawable.livegif3, "Bubbles3"));
        // FilterUtils.gifRoots.add(new GifRoot(R.drawable.fires,"Fires"));
        //  FilterUtils.gifRoots.add(new GifRoot(R.drawable.heartsfilter,"Heart"));
    }

    public static int getDraw(String title) {
        if (title.equalsIgnoreCase("None")) {
            return 0;
        } else if (title.equalsIgnoreCase("Bubbles")) {
            return R.drawable.bubble;
        } else if (title.equalsIgnoreCase("Fires")) {
            return R.drawable.fires;
        } else if (title.equalsIgnoreCase("Heart")) {
            return R.drawable.heartsfilter_a;
        } else if (title.equalsIgnoreCase("Bubbles1")) {
            return R.drawable.livegif1;
        } else if (title.equalsIgnoreCase("Bubbles2")) {
            return R.drawable.livegif2_a;
        } else if (title.equalsIgnoreCase("Bubbles3")) {
            return R.drawable.livegif3;
        }
        return 0;
    }

}
