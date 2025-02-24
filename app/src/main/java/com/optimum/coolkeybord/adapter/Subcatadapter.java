package com.optimum.coolkeybord.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.models.Sub_catitemModel;

import java.util.ArrayList;

public class Subcatadapter extends   RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private final ArrayList<Sub_catitemModel> mDataset;
    private final Context context;

    private final int previousposition = -1;

    public Subcatadapter(ArrayList<Sub_catitemModel> myDataset, Context context) {
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

        holder.catnametxt.setText(mDataset.get(holder.getAdapterPosition()).getSubcategory());
        if(mDataset.get(position).isSelectedornot())
        {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selectedgif));
        }else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.disselectback));
        }
        holder.itemView.setOnClickListener(view -> {
            for(int i =0 ;i < mDataset.size();i++)
            {
                if( i !=position)
                {
                    mDataset.get(i).setSelectedornot(false);
                    notifyItemChanged(i);
                }else {
                    mDataset.get(position).setSelectedornot(true);
                    notifyItemChanged(position);
                }

            }

            Log.d("Sub " , "cat previousposition "+previousposition);
        });
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