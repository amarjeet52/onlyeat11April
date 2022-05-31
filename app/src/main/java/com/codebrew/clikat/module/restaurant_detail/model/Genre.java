package com.codebrew.clikat.module.restaurant_detail.model;


import com.codebrew.clikat.modal.other.ProductDataBean;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Genre extends ExpandableGroup<ProductDataBean> {

  public Genre(String title, List<ProductDataBean> items) {
    super(title, items);
  }
}

