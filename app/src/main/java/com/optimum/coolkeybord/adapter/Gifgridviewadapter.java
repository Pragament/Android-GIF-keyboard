package com.optimum.coolkeybord.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.models.Gifdata;
import com.optimum.coolkeybord.settingssession.SettingSesson;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class Gifgridviewadapter extends   RecyclerView.Adapter<Gifgridviewadapter.ViewHolder> {
    private final ArrayList<Gifdata> mDataset;
    private final Context context;
    private final SettingSesson session;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView gifitemgg;
        public ImageButton btnFavorite; // ⭐ Add favorite button
        public TextView gifText;

        public ViewHolder(View v) {
            super(v);
            gifitemgg = v.findViewById(R.id.gifitemgg);
            gifText = v.findViewById(R.id.multiline_text);
            btnFavorite = v.findViewById(R.id.btn_favorite); // ⭐ Link button to XML
        }
    }


    public Gifgridviewadapter(ArrayList<Gifdata> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
        this.session = new SettingSesson(context);

    }

    @NonNull
    @Override
    public Gifgridviewadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gifitemlayout, parent, false);
        return new Gifgridviewadapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Gifgridviewadapter.ViewHolder holder, int position) {
        Gifdata gifData = mDataset.get(position);
        String gifUrl = gifData.getThumbnailGif(); // or gifData.getGifUrl() if needed

// Load favorite state from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        prefs.edit().putString(gifUrl, "true").apply();
        boolean isFavorite = prefs.contains(gifUrl);

// Set initial icon
        holder.btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_border
        );

// Toggle onClick
        holder.btnFavorite.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (prefs.contains(gifUrl)) {
                editor.remove(gifUrl);
                holder.btnFavorite.setImageResource(R.drawable.ic_star_border);
            } else {
                editor.putString(gifUrl, "true");
                holder.btnFavorite.setImageResource(R.drawable.ic_star_filled);
            }
            editor.apply();
        });


        boolean showTextOnly = session.showTextInsteadOfThumbnail();

        if (showTextOnly) {
            holder.gifitemgg.setVisibility(View.GONE);
            holder.gifText.setVisibility(View.VISIBLE);
            holder.gifText.setText(gifData.getMultilineText()); // Display text instead of GIF
        } else {
            holder.gifitemgg.setVisibility(View.VISIBLE);
            holder.gifText.setVisibility(View.GONE);

            Glide.with(context).load(gifData.getThumbnailGif())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .error(R.mipmap.ic_launcher)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Exception", "Glide load failed for: " + model.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(holder.gifitemgg);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
