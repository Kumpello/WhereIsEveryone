package com.kumpello.whereiseveryone.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whereiseveryone.R;
import com.example.whereiseveryone.databinding.FragmentFriendsBinding;
import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.mvp.BaseFragment;
import com.kumpello.whereiseveryone.presenter.FriendsPresenter;
import com.kumpello.whereiseveryone.utils.OnResult;
import com.kumpello.whereiseveryone.utils.TextUtils;

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

        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show progress ...

        // fetch list of friends ...

        ArrayList<User> userFriends = new ArrayList<>();

        friendsAdapter = new FriendsAdapter((ArrayList<User>) userFriends, presenter);
        layoutManager = new LinearLayoutManager(requireContext());

        //TODO Get this working
        presenter.getFriendsList();


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
        makeAlertWindow(getString(R.string.add_friend), new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isNullOrEmpty(result)) {
                    Log.d("FriendsFragment", "Email set to " + result);
                    presenter.addFriend(result.trim());
                } else {
                    Log.d("FriendsFragment", "Friend email is null or empty");
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("FriendsFragment", error.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFriendToAdapter(User user) {
        friendsAdapter.addUser(user);
        friendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeNick() {
        makeAlertWindow(getString(R.string.change_nick), new OnResult<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isNullOrEmpty(result)) {
                    Log.d("FriendsFragment", "Nick set to " + result);
                    presenter.changeNick(result.trim());
                } else {
                    Log.d("FriendsFragment", "Nick is null or empty");
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("FriendsFragment", error.getMessage());
            }
        });
    }

    @Override
    public void removeFriend(String email) {
        boolean removed = friendsAdapter.removeUser(email);
        assert (removed);
    }

    private void makeAlertWindow(String title, OnResult<String> handler) {
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
            Log.d("FriendsFragment", "value: " + friendsEmailEditText.getText().toString());
            handler.onSuccess(friendsEmailEditText.getText().toString());
        });

        friendsAlert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

        friendsAlert.create().show();
    }

}