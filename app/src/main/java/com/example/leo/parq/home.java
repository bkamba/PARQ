package com.example.leo.parq;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class home extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION = 199;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location startLocation;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.d("Api Client Built", "Done");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Log.d("Map Ready", "Waiting for location");

        if(startLocation != null) {
            LatLng here = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(here).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
            Log.d("Map Ready", "your location");
        } else {
            LatLng umd = new LatLng(38.986918, -76.942554);
            mMap.addMarker(new MarkerOptions().position(umd).title("Marker in UMD"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(umd,16));
            Log.d("Map Ready", "UMD's location");
        }
    }

    public void onLocationChanged(Location location){
        mMap.clear();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
    }

    protected void onStart() {
        //Connect to Google Api client
        //createLocationRequest();
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        //Disconnect to Google Api client
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // tell user to turn on location
           // String[] s = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            //int[] ints = {199};
            //ActivityCompat.requestPermissions(this,s,199);
            createLocationRequest();
            Log.d("onConnected", "no permission");
            //check devices location setting
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    //final LocationSettingsStates states= result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location requests here.
                            Log.d("onConnected", "location setting status code: SUCCESS");

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                Log.d("onConnected", "location setting status code: RESOLUTION_REQUIRED");
                                status.startResolutionForResult(
                                        home.this,
                                        REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            Log.d("onConnected", "location setting status code: SETTINGS_CHANGE_UNAVAILABLE");
                            break;
                    }
                }
            });
        }
        Log.d("onConnected", "entered");
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            startLocation = location;
            Log.d("onConnected", "location set: "+ startLocation.toString());
        }
        return;
        //fusedLocationProviderApi = LocationServices.FusedLocationApi;
        //fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,  mLocationRequest, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // This log is never called
        Log.d("onActivityResult()", Integer.toString(resultCode));

        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    //Creates a location request for the device, Sets the settings for the location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        //Fastes location update within 3000 milliseconds (3 seconds)
        mLocationRequest.setFastestInterval(3000);
        //request the most precise location possible. With this setting
        //the location services are more likely to use GPS to determine the location.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*
        //check devices location setting
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates states= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.d("onConnected", "location setting status code: SUCCESS");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.d("onConnected", "location setting status code: RESOLUTION_REQUIRED");
                            status.startResolutionForResult(
                                    home.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Log.d("onConnected", "location setting status code: SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });

    return;*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
