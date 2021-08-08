package com.example.whereiseveryone.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whereiseveryone.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.Friend> {

    private String[] mDataSet;

    public static class Friend extends RecyclerView.ViewHolder {
        private final TextView name;

        public Friend(View v) {
            super(v);
            name = null;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public TextView getTextView() {
            return name;
        }
    }

    public FriendsAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }


    @Override
    public Friend onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friend, viewGroup, false);

        return new Friend(v);
    }

    @Override
    public void onBindViewHolder(Friend friend, final int position) {

        friend.getTextView().setText(mDataSet[position]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
