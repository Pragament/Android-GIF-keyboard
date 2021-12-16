package com.optimum.coolkeybord.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Subcategory {

    @SerializedName("sub-category")
    @Expose
    private String subCategory;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("sub-category-id")
    @Expose
    private String subCategoryId;
    @SerializedName("category-id")
    @Expose
    private String categoryId;

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}