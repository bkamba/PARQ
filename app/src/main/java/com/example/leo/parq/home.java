package com.example.leo.parq;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.leo.parq.Users.Lot;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class home extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_LOCATION = 199, MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private ClusterManager<MyItem> mClusterManager;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private boolean parqRequested = false;
    ImageView mLocationImage;
    private Random rand = new Random(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Obtain the AutoCompleteFragment and create listener
        PlaceAutocompleteFragment autocompFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        //AutoComplete Event Listener
        autocompFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
           public void onPlaceSelected(Place place) {
               Log.i(place.getId(), "Place: " + place.getName());
               Location newLocation = new Location("");
               newLocation.setLatitude(place.getLatLng().latitude);
               newLocation.setLongitude(place.getLatLng().longitude);
               changeLocation(newLocation);
           }

           public void onError(Status status) {
               Log.i(Integer.toString(status.getStatusCode()), "An error occurred: " + status);
           }
        });

        // Getting Google Play availability status
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability
                .isGooglePlayServicesAvailable(getBaseContext());

        //Google Play Services not available
        if (status != ConnectionResult.SUCCESS) {
            //if(googleApiAvailability.isUserResolvableError(status)) {
            googleApiAvailability.getErrorDialog(this, status, 2404).show();
            //}
            return;
        } else { // Google Play Services Available
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(this, this)
                        .build();
                Log.d("onCreate", "Api Client Built");
            }
        }
        /*
        mLocationImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //autofill users location
            }
        });
        */
        //get notified when the map is ready to be used.
        mapFragment.getMapAsync(this);
        createLocationRequest();
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
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        Log.d("Map Ready", "Waiting for location");

        /*
        LatLng umd = new LatLng(38.986918, -76.942554);
        mMap.addMarker(new MarkerOptions().position(umd).title("Marker in UMD"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(umd, 16));
        Log.d("Map Ready", "UMD's location");
        */
    }


    @Override
    public void onLocationChanged(Location location) {
        if(parqRequested) {
            mMap.clear();
            currentLocation = location;
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
        }
    }

    private void changeLocation(Location location) {
        mMap.clear();
        populateMapWithDummyData(location);
    }

     @Override
     protected void onPause(){
         super.onPause();
         if (parqRequested && mGoogleApiClient.isConnected()) {
             stopLocationUpdates();
         }
     }

    @Override
    protected void onResume(){
        super.onResume();
        if(parqRequested && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            locationOn();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            startLocationUpdates();
        } else {
            populateMapWithDummyData(location);
            //displayLocalMarkers.
        }
    }
    private void populateMapWithDummyData(Location location){
        currentLocation = location;
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        ArrayList<LatLng> locations = dummyLocations();
        for(LatLng l: locations){
            int n = rand.nextInt()%20;
            if (n < 0) n+=20;
            if (n < 5) n+=10;

            mMap.addMarker(new MarkerOptions().position(l)
                    .title(names[Math.abs(rand.nextInt()%6)])
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("$"+n)))
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
        }
        mMap.addMarker(new MarkerOptions().position(currentLatLng)
                .title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
    }
    private String[] names = {"John's Driveway", "Sal's Street", "Katy's Garage", "Jessica's Driveway",
            "Raph's Lawn", "Venessa's Garage"};
    private String[] descriptions = {"A lovely place to Park", "Closest parking tp venue",
            "Super close to campus"};
    private ArrayList<LatLng> dummyLocations(){
        ArrayList<LatLng> locations = new ArrayList<>();
        double lat = currentLocation.getLatitude();
        double lng = currentLocation.getLongitude();
        for(int i = 0; i < 20; i++){
            if(rand.nextInt()%3 == 0)
                locations.add(new LatLng(lat+ (rand.nextDouble()%20)*.007 , lng+(rand.nextDouble()%15)*.006));
            else if(rand.nextInt()%4 == 0)
                locations.add(new LatLng(lat- (rand.nextDouble()%15)*.008 , lng+(rand.nextDouble()%20)*.0015));
            else if (rand.nextInt() < 0)
                locations.add(new LatLng(lat- (rand.nextDouble()%20)*.006 , lng-(rand.nextDouble()%15)*.008));
            else
                locations.add(new LatLng(lat+ (rand.nextDouble()%15)*.005 , lng-(rand.nextDouble()%20)*.007));
        }
        return locations;
    }


    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private String mTitle;
        private String mSnippet;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        public MyItem(double lat, double lng, String title, String snippet) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        dummyLocations();
    }

    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);

//        ActivityCompat.requestPermissions(this,
  //              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
    //            MY_PERMISSIONS_REQUEST_LOCATION);
    }

    //check devices location setting
    public void locationOn(){
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
                        Log.d("onConnected", "location setting status code: SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d("onConnected", "location setting status code: RESOLUTION_REQUIRED");
                            status.startResolutionForResult(
                                    home.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("onConnected", "location setting status code: SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location Enabled", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Please Enable Location to Utilize PARQ", Toast.LENGTH_LONG).show();
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
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(3 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
