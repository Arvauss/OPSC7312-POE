package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.model.Unit;

import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public static boolean isGranted = false;

    CardView MapCard, HomeCard, WorkCard;
    SwitchCompat MeasurementSwitch;

    FirebaseAuth mAuth;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance

        CheckPermissions();
        InitUI();

    }


    public void InitUI(){
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
                double hlat = 0, hlng = 0;
                Intent GoToMapHome = new Intent(getApplicationContext(), MapsActivity.class);
                GoToMapHome.putExtra("DestLat", hlat);
                GoToMapHome.putExtra("DestLng", hlng);
                startActivity(GoToMapHome);
            }
        });
        WorkCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double wlat = 0, wlng = 0;
                Intent GoToMapWork = new Intent(getApplicationContext(), MapsActivity.class);
                GoToMapWork.putExtra("DestLat", wlat);
                GoToMapWork.putExtra("DestLng", wlng);
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