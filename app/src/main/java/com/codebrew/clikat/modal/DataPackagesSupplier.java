package com.codebrew.clikat.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class DataPackagesSupplier {

@SerializedName("list")
@Expose
private java.util.List<ListPackagesSupplier> list = new ArrayList<ListPackagesSupplier>();

    public void setList(List<ListPackagesSupplier> list) {
        this.list = list;
    }

    /**
*

* @return
* The list
*/
public java.util.List<ListPackagesSupplier> getList() {
return list;
}
}