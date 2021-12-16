package com.optimum.coolkeybord.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.models.Gifdata;


import java.util.ArrayList;

public class Gifgridviewadapter extends   RecyclerView.Adapter<Gifgridviewadapter.ViewHolder> {
    private ArrayList<Gifdata> mDataset;
    private Context context;
    private String subType;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
//        public TextView catnametxt;
//        public pl.droidsonroids.gif.GifImageView gifitemgg;
        public ImageView gifitemgg;
        public Button button;

        public ViewHolder(View v) {
            super(v);
            gifitemgg = v.findViewById(R.id.gifitemgg);
//        button = v.findViewById(R.id.button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Gifgridviewadapter(ArrayList<Gifdata> myDataset, Context context, String subType) {
        mDataset = myDataset;
        this.context = context;
        this.subType = subType;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Gifgridviewadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.gifitemlayout, parent, false);
        return new Gifgridviewadapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Gifgridviewadapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.catnametxt.setText(mDataset.get(position).getId());
//        Glide.with(context)
//                .load(mDataset.get(position).getThumbnailGif())
//                .into(holder.gifitemgg);

        Glide.with(context).load(mDataset.get(position).getGif())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.exo_rounded_rectangle)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        Log.e("Execption" , "on glide is"+model.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.gifitemgg);
//        holder.gifitemgg.setBackgroundResource(mDataset.get(position).getId());
//        holder.button.setOnClickListener((View v) -> {
//            DatabaseManager db = new DatabaseManager(context);
//            try {
//                db.delete(mDataset.get(position).getId(), subType);
//            } catch (Exception e) {
//                Log.d("Exception Error", String.valueOf(e));
//            }
//
//            mDataset.remove(position);
//            this.notifyDataSetChanged();
//            Toast.makeText(context, R.string.removed_word, Toast.LENGTH_LONG).show();
//        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}