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

import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.android.HistoryClicked;
import com.optimum.coolkeybord.database.Dao;
import com.optimum.coolkeybord.database.Historyviewmodel;
import com.optimum.coolkeybord.models.Historymodal;

import java.util.ArrayList;

public class Historyadapter extends RecyclerView.Adapter<Historyadapter.ViewHolder> {
    private final HistoryClicked historyClicked;
    private ArrayList<Historymodal> mDataset;
    private Context context;
    private String subType;
    private Historyviewmodel historyviewmodel;
    Dao userDao;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public Button button;
        boolean isdeleted = false;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.textView);
            button = v.findViewById(R.id.button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Historyadapter(ArrayList<Historymodal> myDataset, Application context, Dao tempuserDao, HistoryClicked historyClickedx) {
        mDataset = myDataset;
        this.context = context;
        this.userDao = tempuserDao;
        this.historyClicked = historyClickedx;
        historyviewmodel = new Historyviewmodel(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Historyadapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_words_list, parent, false);
        return new Historyadapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Historyadapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(holder.getAdapterPosition()).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyClicked.OnHstoryclicked(mDataset.get(holder.getAdapterPosition()));
            }
        });
        holder.button.setOnClickListener((View v) -> {
            historyviewmodel.delete(mDataset.get(position));
            mDataset.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            Toast.makeText(context, R.string.removed_word, Toast.LENGTH_LONG).show();
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        userDao.delete(mDataset.get(holder.getAdapterPosition()));
//                        Log.e("Delete" , "success");
//                        holder.isdeleted = true;
//                    } catch (Exception e) {
//                        Log.d("Exception Error", String.valueOf(e));
//                        holder.isdeleted = false;
//                    }
//                }
//            });
            Log.e("Delete" , "success "+holder.isdeleted);


        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}