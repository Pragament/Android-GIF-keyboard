package com.optimum.coolkeybord.adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.android.HistoryClicked;
import com.optimum.coolkeybord.database.Dao;
import com.optimum.coolkeybord.database.Historyviewmodel;
import com.optimum.coolkeybord.models.Historymodal;

import java.util.ArrayList;

public class Historyadapter extends RecyclerView.Adapter<Historyadapter.ViewHolder> {
    private final HistoryClicked historyClicked;
    private final ArrayList<Historymodal> mDataset;
    private final Context context;

    private final Historyviewmodel historyviewmodel;
    Dao userDao;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public Button button;
        boolean isdeleted = false;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.textView);
            button = v.findViewById(R.id.button);
        }
    }

    public Historyadapter(ArrayList<Historymodal> myDataset, Application context, Dao tempuserDao, HistoryClicked historyClickedx) {
        mDataset = myDataset;
        this.context = context;
        this.userDao = tempuserDao;
        this.historyClicked = historyClickedx;
        historyviewmodel = new Historyviewmodel(context);
    }

    @NonNull
    @Override
    public Historyadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_words_list, parent, false);
        return new Historyadapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Historyadapter.ViewHolder holder, int position) {

        holder.mTextView.setText(mDataset.get(holder.getAdapterPosition()).getTitle());
        holder.itemView.setOnClickListener(view -> historyClicked.OnHstoryclicked(mDataset.get(holder.getAdapterPosition())));
        holder.button.setOnClickListener((View v) -> {
            historyviewmodel.delete(mDataset.get(position));
            mDataset.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            Toast.makeText(context, R.string.removed_word, Toast.LENGTH_LONG).show();

            Log.e("Delete" , "success "+holder.isdeleted);


        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}