package com.codebrew.clikat.module.pre_delivery.Model;

public class ListItem {
    private String name;
    private String Header_name;
    private int image;

    public String getHeader_name() {
        return Header_name;
    }

    public void setHeader_name(String header_name) {
        Header_name = header_name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public ListItem(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public ListItem(String header_name,int image)
   {
this.Header_name=header_name;
this.image=image;
   }
  public   ListItem(String name)
    {
this.name=name;
    }
}
