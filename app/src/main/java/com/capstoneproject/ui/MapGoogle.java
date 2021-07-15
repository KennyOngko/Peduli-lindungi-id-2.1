package com.capstoneproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.capstoneproject.R;

import java.util.Map;


public class MapGoogle extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient client;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_google);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);
        client = LocationServices.getFusedLocationProviderClient(MapGoogle.this);
        latitude = getIntent().getExtras().getString("LATITUDE");
        longitude = getIntent().getExtras().getString("LONGITUDE");

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(longitude), Double.parseDouble(latitude)) , 20));
                map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(longitude), Double.parseDouble(latitude))).title("Here"));
            }
        });

    }

}