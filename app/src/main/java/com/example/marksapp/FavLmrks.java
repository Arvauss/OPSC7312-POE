package com.example.marksapp;

import android.os.Bundle;

import com.example.marksapp.Adapters.FavLmrkAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marksapp.databinding.ActivityFavLmrksBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavLmrks extends AppCompatActivity {

    private ArrayList<LandmarksModel> arrLmrks = new ArrayList<LandmarksModel>();
    RecyclerView lmrkRV;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private AppBarConfiguration appBarConfiguration;
    private ActivityFavLmrksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_lmrks);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        InitLmrks();

    }

    private void InitLmrks() {

        dbRef.child(mAuth.getUid()).child("savedLandmarks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrLmrks.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    arrLmrks.add(snap.getValue(LandmarksModel.class));
                    Log.d("1234567", "onDataChange: lmrk loaded");
                }
                InitRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitRecycler(){
        lmrkRV = findViewById(R.id.rv_favlmrks);

        FavLmrkAdapter adap = new FavLmrkAdapter(this, arrLmrks);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        lmrkRV.setAdapter(adap);
        lmrkRV.setLayoutManager(llm);
    }




}