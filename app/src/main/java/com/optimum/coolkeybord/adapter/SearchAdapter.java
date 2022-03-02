package com.optimum.coolkeybord.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private Context context;
    private String subType;
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public Button button;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.textView);
            button = v.findViewById(R.id.button);
        }
    }

    public SearchAdapter(ArrayList<String> myDataset, Context context, String subType) {
        mDataset = myDataset;
        this.context = context;
        this.subType = subType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_words_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mDataset.get(position));
        holder.button.setOnClickListener((View v) -> {

            mDataset.remove(position);
            this.notifyDataSetChanged();
            Toast.makeText(context, R.string.removed_word, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}