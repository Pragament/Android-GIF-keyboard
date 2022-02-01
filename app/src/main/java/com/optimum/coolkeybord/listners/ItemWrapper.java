package com.optimum.coolkeybord.listners;

import com.optimum.coolkeybord.models.Categoriesmodel;

import java.util.Objects;

public class ItemWrapper {

  public   Categoriesmodel item;

    public ItemWrapper(Categoriesmodel item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemWrapper)) return false;
        ItemWrapper other = (ItemWrapper) obj;
        return  Objects.equals(item.getCategoryid(), other.item.getCategoryid()) ;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}