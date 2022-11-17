package com.example.marksapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.model.Unit;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Navigation bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    item.setChecked(true);

    switch (item.getItemId()){
        case R.id.Home:
            break;

        case R.id.ViewMap:
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
            break;

        case R.id.AllLand:
            break;

            case R.id.Settings:
                break;

        case R.id.LogOut:
            Intent intents = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intents);
            break;
    }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isGranted = false;

    private static double destLat = 0, destLng = 0;

    CardView MapCard, HomeCard, WorkCard;
    ImageView MenuIcon;
    TextView emailText;
    SwitchCompat MeasurementSwitch;
    DrawerLayout dl;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    private Users curU;
    private List<LandmarksModel> favs;
    public DrawerLayout drawerlayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navigation Bar
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance
        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("Users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curU = snapshot.getValue(Users.class);

                List<LandmarksModel> favs = curU.getSavedLandmarks();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CheckPermissions();
        InitUI();
        initializeButtons();

    }


    public void InitUI(){
        MenuIcon = (ImageView) findViewById(R.id.menuButton);
        emailText = (TextView) findViewById(R.id.emailText);
        emailText.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        MapCard = (CardView) findViewById(R.id.Mapcard_id);
        HomeCard = (CardView) findViewById(R.id.Homecard_id);
        WorkCard = (CardView) findViewById(R.id.Workcard_id);
        MeasurementSwitch = (SwitchCompat) findViewById(R.id.SwitchID);

        MapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoToMap = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(GoToMap);
            }
        });
        HomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDestLatLng(1);
                Intent GoToMapHome = new Intent(getApplicationContext(), MapsActivity.class);
                GoToMapHome.putExtra("DestLat", destLat);
                GoToMapHome.putExtra("DestLng", destLng);
                if (destLat == 0 && destLng == 0)
                    GoToMapHome.putExtra("Mode", 1);
                startActivity(GoToMapHome);
            }
        });
        WorkCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDestLatLng(2);
                Intent GoToMapWork = new Intent(getApplicationContext(), MapsActivity.class);
                GoToMapWork.putExtra("DestLat", destLat);
                GoToMapWork.putExtra("DestLng", destLng);
                if (destLat == 0 && destLng == 0)
                    GoToMapWork.putExtra("Mode", 2);
                startActivity(GoToMapWork);
            }

        });

        MeasurementSwitch.setText(MeasurementSwitch.getTextOff());
        MeasurementSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                if (MeasurementSwitch.isChecked()){
                    MeasurementSwitch.setText(MeasurementSwitch.getTextOn());
                    Unit pref = Unit.IMPERIAL;
                    reference.child(firebaseUser.getUid()).child("measurementPref").setValue(Unit.IMPERIAL).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Measurement preference is now " + MeasurementSwitch.getTextOn(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    MeasurementSwitch.setText(MeasurementSwitch.getTextOff());
                    Unit pref = Unit.METRIC;
                    reference.child(firebaseUser.getUid()).child("measurementPref").setValue(Unit.METRIC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Measurement preference is now " + MeasurementSwitch.getTextOff(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
    public void initializeButtons() {
        dl = findViewById(R.id.drawer_layout);
        MenuIcon = findViewById(R.id.menuButton);
        MenuIcon.setOnClickListener(v -> OpenMenu(dl));
    }

    public void OpenMenu(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void GetDestLatLng(int code) {
        if (!favs.isEmpty()){
            loop: for (LandmarksModel lm : favs){
                switch (code) {
                    case 1:
                        if (lm.getLmName().equals("Home")){
                            destLat = lm.getLmLat();
                            destLng = lm.getLmLng();
                            break loop;
                        }
                        break;
                    case 2:
                        if (lm.getLmName().equals("Work")){
                            destLat = lm.getLmLat();
                            destLng = lm.getLmLng();
                            break loop;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            isGranted = true;
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0: if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                Toast.makeText(this, "Permissions Not Granted", Toast.LENGTH_SHORT).show();
        }


    }
}