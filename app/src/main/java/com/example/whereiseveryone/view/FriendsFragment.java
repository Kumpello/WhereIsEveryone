package com.example.whereiseveryone.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whereiseveryone.R;
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
    private String nick;
    private String friendsEmail;

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
        ArrayList<User> friends = (ArrayList<User>) presenter.getFriendsList();

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
        binding.changeNick.setOnClickListener(v -> changeNick());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void addFriend() {
        makeAlertWindow(getString(R.string.add_friend));
        if (!TextUtils.isNullOrEmpty(friendsEmail)) {
            presenter.addFriend(friendsEmail);
        }
    }

    @Override
    public void changeNick() {
        makeAlertWindow(getString(R.string.change_nick));
        if (!TextUtils.isNullOrEmpty(nick)) {
            Log.d("FriendsFragment", "Nick set to " + nick);
            presenter.changeNick(nick);
        } else {
            Log.d("FriendsFragment", "Nick is null or empty");
        }
    }

    @Override
    public void removeFriend(String email) {
        boolean removed = friendsAdapter.removeUser(email);
        assert(removed);
    }

    @Nullable
    private void makeAlertWindow(String title) {
        final String[] value = new String[1];

        AlertDialog.Builder friendsAlert = new AlertDialog.Builder(getActivity());
        EditText friendsEmailEditText = new EditText(getContext());
        String addFriend = getString(R.string.add_friend);
        String changeNick = getString(R.string.change_nick);
        String hint;
        if (title.equals(addFriend)) {
            hint = getString(R.string.enter_email);
        } else if (title.equals(changeNick)) {
            hint = getString(R.string.enter_nick);
        } else {
            hint = getString(R.string.enter_value);
        }
        friendsEmailEditText.setHint(hint);

        friendsAlert.setTitle(title);
        friendsAlert.setView(friendsEmailEditText);

        LinearLayout friendsAlertLayout = new LinearLayout(getContext());
        friendsAlertLayout.setOrientation(LinearLayout.VERTICAL);
        friendsAlertLayout.addView(friendsEmailEditText);
        friendsAlert.setView(friendsAlertLayout);

        friendsAlert.setPositiveButton("Continue", (dialog, whichButton) -> {
            value[0] = friendsEmailEditText.getText().toString();
            Log.d("FriendsFragment", "value: " + value[0]);
            if (title.equals(addFriend)) {
                setFriendsEmail(value[0]);
            } else if (title.equals(changeNick)) {
                setNick(value[0]);
            }
        });

        friendsAlert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

        friendsAlert.create().show();
    }

    private void setNick(String nick) {
        this.nick = nick;
    }

    private void setFriendsEmail(String email) {
        this.friendsEmail = email;
    }

}