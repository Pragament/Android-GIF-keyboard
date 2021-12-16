package com.optimum.coolkeybord.models;

import java.util.ArrayList;

public class Categoriesmodel {
    public String catname;
    public String categoryid;
    public String subcategory;
    public String subcategoryid;
    public ArrayList<Sub_catitemModel>sub_catitemModels;
    public Categoriesmodel(String catname, String subcategory ,ArrayList<Sub_catitemModel> sub_catitemModelsx) {
        this.catname = catname;
        this.subcategory = subcategory;
        this.sub_catitemModels = sub_catitemModelsx;
    }


    public ArrayList<Sub_catitemModel> getSub_catitemModels() {
        return sub_catitemModels;
    }

    public void setSub_catitemModels(ArrayList<Sub_catitemModel> sub_catitemModels) {
        this.sub_catitemModels = sub_catitemModels;
    }

    public String getCatname() {
        return catname;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

}
