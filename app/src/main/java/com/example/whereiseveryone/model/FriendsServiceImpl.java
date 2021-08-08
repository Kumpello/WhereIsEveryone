package com.example.whereiseveryone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsServiceImpl implements FriendsService {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;


    public FriendsServiceImpl() {
        database = FirebaseDatabase.getInstance("https://whereiseveryone-cc9db-default-rtdb.europe-west1.firebasedatabase.app/");
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
