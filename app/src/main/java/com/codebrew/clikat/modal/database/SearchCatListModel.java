
package com.codebrew.clikat.modal.database;

import io.realm.RealmList;
import io.realm.RealmObject;


public class SearchCatListModel extends RealmObject {
    @SuppressWarnings("unused")
    private RealmList<SearchCategoryModel> itemList;

     public RealmList<SearchCategoryModel> getItemList() {
        return itemList;
    }
}
