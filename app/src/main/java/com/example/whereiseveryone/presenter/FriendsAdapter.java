package com.example.whereiseveryone.presenter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.Friend> {
    @NonNull
    @Override
    public Friend onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Friend holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class Friend extends RecyclerView.ViewHolder {

        public Friend(@NonNull View itemView) {
            super(itemView);
        }
    }
}
