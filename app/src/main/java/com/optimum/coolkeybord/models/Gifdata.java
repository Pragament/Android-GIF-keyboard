package com.optimum.coolkeybord.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gifdata {
    @SerializedName("multiline_text")
    @Expose
    private final String multilineText;
    @SerializedName("gif")
    @Expose
    private String gif;
    @SerializedName("thumbnail_gif")
    @Expose
    private final String thumbnailGif;
    @SerializedName("youtube_url")
    @Expose
    private final String youtubeUrl;
//    @SerializedName("youtube_url")
//    @Expose
    private Boolean selectedornot;

    public Gifdata(String multilineText, String gif, String thumbnailGif, String youtubeUrl, Boolean selectedornotx) {
        this.multilineText = multilineText;
        this.gif = gif;
        this.thumbnailGif = thumbnailGif;
        this.youtubeUrl = youtubeUrl;
        this.selectedornot = selectedornotx;
    }

    public Boolean getSelectedornot() {
        return selectedornot;
    }

    public void setSelectedornot(Boolean selectedornot) {
        this.selectedornot = selectedornot;
    }

    public String getMultilineText() {
        return multilineText;
    }

//    public void setMultilineText(String multilineText) {
//        this.multilineText = multilineText;
//    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getThumbnailGif() {
        return thumbnailGif;
    }

//    public void setThumbnailGif(String thumbnailGif) {
//        this.thumbnailGif = thumbnailGif;
//    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

//    public void setYoutubeUrl(String youtubeUrl) {
//        this.youtubeUrl = youtubeUrl;
//    }
//    final int id;
//    final String gifname;
//    final String gifurl;
//
//    public Gifdata(int id, String gifname, String gifurl) {
//
//        this.id = id;
//        this.gifname = gifname;
//        this.gifurl = gifurl;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public String getGifname() {
//        return gifname;
//    }
//
//    public String getGifurl() {
//        return gifurl;
//    }
}
//public class SubCatGifmodel {
//
//    @SerializedName("total_count")
//    @Expose
//    private Integer totalCount;
//    @SerializedName("items_per_page")
//    @Expose
//    private Integer itemsPerPage;
//    @SerializedName("total_pages")
//    @Expose
//    private Integer totalPages;
//    @SerializedName("current_page")
//    @Expose
//    private Integer currentPage;
//    @SerializedName("next_page")
//    @Expose
//    private Integer nextPage;
//    @SerializedName("sub-category-id")
//    @Expose
//    private String subCategoryId;
//    @SerializedName("next_page_api")
//    @Expose
//    private String nextPageApi;
//    @SerializedName("items")
//    @Expose
//    private List<Gifdata> items = null;
//
//    public Integer getTotalCount() {
//        return totalCount;
//    }
//
//    public void setTotalCount(Integer totalCount) {
//        this.totalCount = totalCount;
//    }
//
//    public Integer getItemsPerPage() {
//        return itemsPerPage;
//    }
//
//    public void setItemsPerPage(Integer itemsPerPage) {
//        this.itemsPerPage = itemsPerPage;
//    }
//
//    public Integer getTotalPages() {
//        return totalPages;
//    }
//
//    public void setTotalPages(Integer totalPages) {
//        this.totalPages = totalPages;
//    }
//
//    public Integer getCurrentPage() {
//        return currentPage;
//    }
//
//    public void setCurrentPage(Integer currentPage) {
//        this.currentPage = currentPage;
//    }
//
//    public Integer getNextPage() {
//        return nextPage;
//    }
//
//    public void setNextPage(Integer nextPage) {
//        this.nextPage = nextPage;
//    }
//
//    public String getSubCategoryId() {
//        return subCategoryId;
//    }
//
//    public void setSubCategoryId(String subCategoryId) {
//        this.subCategoryId = subCategoryId;
//    }
//
//    public String getNextPageApi() {
//        return nextPageApi;
//    }
//
//    public void setNextPageApi(String nextPageApi) {
//        this.nextPageApi = nextPageApi;
//    }
//
//    public List<Gifdata> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Gifdata> items) {
//        this.items = items;
//    }
//
//
//}
