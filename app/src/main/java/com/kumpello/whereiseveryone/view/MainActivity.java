package com.kumpello.whereiseveryone.view;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.kumpello.whereiseveryone.R;
import com.kumpello.whereiseveryone.databinding.ActivityMainBinding;
import com.kumpello.whereiseveryone.mvp.BaseActivity;
import com.kumpello.whereiseveryone.presenter.MainPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    private NavController navController;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.bringToFront();
        NavigationUI.setupWithNavController(bottomNav, navController);

    }
}