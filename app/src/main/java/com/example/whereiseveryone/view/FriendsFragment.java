package com.example.whereiseveryone.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whereiseveryone.databinding.FragmentFriendsBinding;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.BaseFragment;
import com.example.whereiseveryone.presenter.FriendsPresenter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends BaseFragment<FriendsPresenter> implements FriendsView {

    private FragmentFriendsBinding binding;
    private FriendsAdapter friendsAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show progress ...

        // fetch list of friends ...
        ArrayList<User> friends = new ArrayList<>();
        friends.add(new User("abc@abc.com"));
        friends.add(new User("szok@szok.com"));

        friendsAdapter = new FriendsAdapter(friends, presenter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(friendsAdapter);

        binding.addFriend.setOnClickListener(v -> {
            addFriend();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void addFriend() {
        presenter.addFriend();
    }

    @Override
    public void removeFriend() {

    }


}