package com.example.marksapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.marksapp.databinding.ActivityFavLmrksBinding;

public class FavLmrks extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityFavLmrksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFavLmrksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }


}