package com.sumeshdesh.locationtodegrees;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int LOCATION_REQUEST_INT = 123;

    TextView txtLong;
    TextView txtLat;

    Location currentLocation;
    LocationRequest locationRequest;
    GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLong= (TextView) findViewById(R.id.txtLong);
        txtLat= (TextView) findViewById(R.id.txtLat);
        
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000 * 5);   //5 sec
        locationRequest.setFastestInterval(1000 * 1);   //1 sec
    }

    @Override
    protected void onStart(){
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(client.isConnected()) {
            client.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(client != null){
            startLocationUpdates();
            Toast.makeText(this,"Connected", Toast.LENGTH_SHORT).show();
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_INT);
            }
        }
    }

    protected void startLocationUpdates(){
        if(LocationServices.FusedLocationApi == null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        client, locationRequest, this);
            } catch (SecurityException e) {
                return;
            }
        }


        if(LocationServices.FusedLocationApi != null){

            try{

                currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);
                txtLat.setText(String.valueOf(currentLocation.getLatitude()));
                txtLong.setText(String.valueOf(currentLocation.getLongitude()));


            } catch(SecurityException | NullPointerException e){
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Woah", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
        try {
            client.disconnect();
            client.connect();
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            if( currentLocation  != null){
                txtLat.setText(String.valueOf(currentLocation.getLatitude()));
                txtLong.setText(String.valueOf(currentLocation.getLongitude()));
            }
        } catch(SecurityException e){
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
        }

    }
}
