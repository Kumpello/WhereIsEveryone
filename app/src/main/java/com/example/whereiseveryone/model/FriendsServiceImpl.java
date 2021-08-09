package com.example.whereiseveryone.model;

import com.example.whereiseveryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsServiceImpl implements FriendsService {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;


    public FriendsServiceImpl() {
        database = FirebaseDatabase.getInstance(String.valueOf(R.string.server_address));
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
