package com.optimum.coolkeybord.models;

public class Sub_catitemModel {
   String subcategory;
   String subcategoryid;
       public Sub_catitemModel(String subcategory, String subcategoryid) {
       this.subcategory = subcategory;
       this.subcategoryid = subcategoryid;
   }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(String subcategoryid) {
        this.subcategoryid = subcategoryid;
    }
}
