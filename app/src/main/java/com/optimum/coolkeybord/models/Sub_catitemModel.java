package com.optimum.coolkeybord.models;

public class Sub_catitemModel {
   String subcategory;
   String subcategoryid;
    public Boolean selectedornot;
       public Sub_catitemModel(String subcategory, String subcategoryid ,Boolean selectedornotx) {
       this.subcategory = subcategory;
       this.subcategoryid = subcategoryid;
           this.selectedornot = selectedornotx;
   }

    public boolean isSelectedornot() {
        return selectedornot;
    }

    public void setSelectedornot(Boolean selectedornot) {
        this.selectedornot = selectedornot;
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
