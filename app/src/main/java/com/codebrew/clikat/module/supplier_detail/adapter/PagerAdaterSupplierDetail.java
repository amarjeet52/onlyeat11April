package com.codebrew.clikat.module.supplier_detail.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabAbout;
import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabDescription;
import com.codebrew.clikat.module.supplier_detail.supplier_tabs.TabReviews;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PagerAdaterSupplierDetail extends FragmentPagerAdapter {
    private final String[] tabTitles;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public PagerAdaterSupplierDetail(FragmentManager fragmentManager, String[] tabTitles) {
        super(fragmentManager);
        this.tabTitles = tabTitles;

        fragmentList.add(new TabDescription());
        fragmentList.add(new TabAbout());
        fragmentList.add(new TabReviews());
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
//        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}