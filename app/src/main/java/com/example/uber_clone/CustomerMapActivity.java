package com.example.uber_clone;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastlocation;
    LocationRequest locationRequest;
    Button customer_settings,customer_logout;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    boolean currentdriverlogoutstatus=false;
    Button Customercallcabbtn;
    FirebaseAuth customerId;
    String CurrentId;
    DatabaseReference customerDatabase;
    LatLng CustomerPickupLocation;
    DatabaseReference DriverLocationref;
    int radius=1;
    Boolean driverfound=false;
    String driverfoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        customer_logout=(Button) findViewById(R.id.customer_logout);
        customer_settings=findViewById(R.id.customer_setting);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        CurrentId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Customercallcabbtn=findViewById(R.id.customer_call_cab_button);
        customerDatabase=FirebaseDatabase.getInstance().getReference().child("Customer Request");
        DriverLocationref=FirebaseDatabase.getInstance().getReference().child("Drivers Availaible");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        customer_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Logoutdriver();
                mAuth.signOut();

            }
        });
        Customercallcabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoFire geoFire=new GeoFire(customerDatabase);
                geoFire.setLocation(CurrentId, new GeoLocation(lastlocation.getLatitude(), lastlocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
                CustomerPickupLocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(CustomerPickupLocation).title("Pickup customer from here"));

                Customercallcabbtn.setText("Getting your driver.....");
                GetClosestDriver();
            }
        });

    }
    public void GetClosestDriver(){

        GeoFire geoFire=new GeoFire(DriverLocationref);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(CustomerPickupLocation.latitude,CustomerPickupLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverfound){
                    driverfound=true;
                    driverfoundId=key;
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                    if(!driverfound){
                        radius=radius+1;
                        GetClosestDriver();
                    }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
            return;
        }
        buildGoogleApiclient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest=LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

            lastlocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));



    }
    protected synchronized void buildGoogleApiclient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CustomerMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiclient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


    }


    private void Logoutdriver() {

        Intent intent=new Intent(CustomerMapActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
