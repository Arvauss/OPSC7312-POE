package com.example.marksapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.marksapp.Adapters.FavLmrkAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

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

        // drawer layout instance to toggle the menu icon to open (The IIE, 2022)
        //drawer and back button to close drawer (geeksforgeeks.org, 2022).
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //Instantiating burgerNavigationView and binding it to view (Pulak, 2017).
        navigationView = findViewById(R.id.nav_view);
        //Setting navigation item listener (Pulak, 2017).
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        // pass the Open and Close toggle for the drawer layout listener (The IIE, 2022)
        // to toggle the button (geeksforgeeks.org, 2022).
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar (geeksforgeeks.org, 2022).
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.nav_MainMenu){
            Intent goMainM = new Intent(this, MainActivity.class);
            startActivity(goMainM);
        } else
        if (id == R.id.nav_Map){
            Intent goMap = new Intent(this, MapsActivity.class);
            startActivity(goMap);
        } else
        if (id == R.id.nav_favLmrks){
            Intent goFavs = new Intent(this, FavLmrks.class);
            startActivity(goFavs);
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}