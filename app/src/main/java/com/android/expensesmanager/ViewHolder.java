package com.android.expensesmanager;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mCategory;
    public TextView mAmount;
    public TextView mTypes;
    public TextView mDate;

    private ViewHolder.ClickListener mClickListener;

    View mView;

    public ViewHolder(@NonNull View itemView)
    {
        super(itemView);
        mView = itemView;

        mCategory=itemView.findViewById(R.id.tCategory);
        mAmount=itemView.findViewById(R.id.tAmount);
        mDate=itemView.findViewById(R.id.tDate);
        mTypes=itemView.findViewById(R.id.tTypes);
        

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

    }

    public void setAmount(TextView amount) {
        mAmount = amount;
    }


    public interface ClickListener{
        void onItemClick(View view,int position);

    }

    public void setInfo(Context context, String categories, String userAmount, String amountType, String date){
        TextView nCategory = mView.findViewById(R.id.tCategory);
        TextView nAmount = mView.findViewById(R.id.tAmount);
        TextView nType = mView.findViewById(R.id.tTypes);
        TextView nDate = mView.findViewById(R.id.tDate);

        nCategory.setText(""+categories);
        nAmount.setText("$"+userAmount);
        nDate.setText(""+date);
        nType.setText(""+amountType);
    }


    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }














}
