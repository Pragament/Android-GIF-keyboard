package com.optimum.coolkeybord.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.models.Categoriesmodel;

import java.util.ArrayList;

public class CategoriesAdapter extends   RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
private final ArrayList<Categoriesmodel> mDataset;
private final Context context;


public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView catnametxt;
    public Button button;

    public ViewHolder(View v) {
        super(v);
        catnametxt = v.findViewById(R.id.catnametxt);
    }
}

    public CategoriesAdapter(ArrayList<Categoriesmodel> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;

    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.categroeisitem, parent, false);
        return new CategoriesAdapter.ViewHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder,  int position) {
        Log.e("pos" , ""+mDataset.get(position).isSelectedornot());
        holder.catnametxt.setText(mDataset.get(position).getCatname());
        if(mDataset.get(position).isSelectedornot())
        {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selectedgif));
        }else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.disselectback));
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return   position;
    }
}