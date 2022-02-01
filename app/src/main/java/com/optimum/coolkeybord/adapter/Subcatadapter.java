package com.optimum.coolkeybord.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.models.Categoriesmodel;
import com.optimum.coolkeybord.models.Sub_catitemModel;

import java.util.ArrayList;

public class Subcatadapter extends   RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private ArrayList<Sub_catitemModel> mDataset;
    private Context context;
    private String subType;
    private  int previousposition = -1;
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView catnametxt;
        public Button button;

        public ViewHolder(View v) {
            super(v);
            catnametxt = v.findViewById(R.id.catnametxt);
//        button = v.findViewById(R.id.button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Subcatadapter(ArrayList<Sub_catitemModel> myDataset, Context context, String subType) {
        mDataset = myDataset;
        this.context = context;
        this.subType = subType;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.categroeisitem, parent, false);
        return new CategoriesAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.catnametxt.setText(mDataset.get(position).getSubcategory());
        if(mDataset.get(position).isSelectedornot())
        {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selectedgif));
        }else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.disselectback));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeColour(holder ,position);
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
            }

           
        });

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

    private void changeColour(CategoriesAdapter.ViewHolder holder, int position) {
        if(previousposition == -1)
        {

            mDataset.get(position).setSelectedornot(true);
            holder.itemView.setBackground(context.getDrawable(R.drawable.selectedgif));
            previousposition = position ;
            notifyDataSetChanged();
        }else {

            if(!mDataset.get(position).isSelectedornot())
            {
                if(previousposition == position)
                {
                    mDataset.get(position).setSelectedornot(false);
                    holder.itemView.setBackground(context.getDrawable(R.drawable.disselectback));
                }else {
                    mDataset.get(position).setSelectedornot(true);
                    holder.itemView.setBackground(context.getDrawable(R.drawable.selectedgif));


                }
                previousposition = position ;
            }else {

                mDataset.get(previousposition).setSelectedornot(false);
                holder.itemView.setBackground(context.getDrawable(R.drawable.disselectback));
                notifyItemChanged(previousposition);

            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
      return   position;
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
}