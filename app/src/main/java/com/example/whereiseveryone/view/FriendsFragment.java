package com.example.whereiseveryone.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whereiseveryone.databinding.FragmentFriendsBinding;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.BaseFragment;
import com.example.whereiseveryone.presenter.FriendsPresenter;
import com.example.whereiseveryone.utils.TextUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends BaseFragment<FriendsPresenter> implements FriendsView {

    private FragmentFriendsBinding binding;
    private FriendsAdapter friendsAdapter;
    private LinearLayoutManager layoutManager;

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
        layoutManager = new LinearLayoutManager(requireContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(friendsAdapter);

        binding.addFriend.setOnClickListener(v -> addFriend());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void addFriend() {
        String email = makeAlertWindow("Add friend");
        if (!TextUtils.isNullOrEmpty(email)) {
            presenter.addFriend(email);
        }
    }

    @Override
    public void changeNick() {
        String nick = makeAlertWindow("Change nick");
        if (!TextUtils.isNullOrEmpty(nick)) {
            presenter.changeNick(nick);
        }
    }

    @Override
    public void removeFriend(String email) {
        boolean removed = friendsAdapter.removeUser(email);
        assert(removed);
    }

    @Nullable
    private String makeAlertWindow(String title) {
        final String[] friendsEmail = new String[1];
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        AlertDialog.Builder friendsAlert = new AlertDialog.Builder(getActivity());
        EditText friendsEmailEditText = new EditText(getContext());
        friendsEmailEditText.setHint("Enter Email");

        friendsAlert.setTitle(title);
        friendsAlert.setView(friendsEmailEditText);

        LinearLayout friendsAlertLayout = new LinearLayout(getContext());
        friendsAlertLayout.setOrientation(LinearLayout.VERTICAL);
        friendsAlertLayout.addView(friendsEmailEditText);
        friendsAlert.setView(friendsAlertLayout);

        friendsAlert.setPositiveButton("Continue", (dialog, whichButton) -> {
            friendsEmail[0] = friendsEmailEditText.getText().toString();
            if (friendsEmailEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "enter email address", Toast.LENGTH_SHORT).show();
            } else {
                if (friendsEmailEditText.getText().toString().trim().matches(emailPattern)) {
                    Toast.makeText(getContext(), "valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendsAlert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

        friendsAlert.create().show();

        return friendsEmail[0];
    }

}