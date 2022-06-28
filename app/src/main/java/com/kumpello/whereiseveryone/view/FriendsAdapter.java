package com.kumpello.whereiseveryone.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.kumpello.whereiseveryone.R;
import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.presenter.FriendsPresenter;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private ArrayList<User> friendList;
    private FriendsPresenter presenter;

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final FriendsPresenter presenter;

        private final TextView userEmail;
        private final AppCompatImageButton remove;
        private ImageView userType;

        public FriendViewHolder(View v, FriendsPresenter friendsPresenter) {
            super(v);
            userEmail = v.findViewById(R.id.friendTextView);
            remove = v.findViewById(R.id.removeFriend);
            userType = v.findViewById(R.id.accountType);
            presenter = friendsPresenter;
        }

        public void bind(final User u) {
            userEmail.setText(u.email);
            switch (u.accountType) {
                case GOOGLE:
                    userType.setImageResource(R.drawable.googleg_standard_color_18);
                    break;
                case EMAIL:
                    userType.setImageResource(R.drawable.email_icon);
            }

            remove.setOnClickListener(v -> {
                presenter.removeFriend(u.email, u.accountType);
            });
        }

    }

    public FriendsAdapter(ArrayList<User> dataSet, FriendsPresenter presenter) {
        friendList = dataSet;
        this.presenter = presenter;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friend, viewGroup, false);

        return new FriendViewHolder(v, presenter);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, final int position) {
        holder.bind(friendList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void addUser(User user) {
        friendList.add(user);
    }

    public boolean removeUser(final String user) {
        // TODO: verify concurrency is safe (removing two users at once)
        // find index of user with email
        int position = -1;
        for (int i = 0; i < friendList.size(); i++) {
            if (friendList.get(i).email.equals(user)) {
                position = i;
            }
        }

        if (position != -1) {
            friendList.remove(position);
            notifyItemRemoved(position);
        }
        return position != -1;
    }
}
