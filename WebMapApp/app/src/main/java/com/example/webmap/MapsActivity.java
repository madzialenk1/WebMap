package com.example.webmap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;

    Polyline currentPolyline;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText latitudeEditSource, longitudeEditSource, latitudeEditDestination, longitudeEditDestination;
    private Button search, showTrackButton, locationButton;

    MarkerOptions place1, place2;

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        showTrackButton = (Button) findViewById(R.id.showTrackButton);
        locationButton = (Button) findViewById(R.id.locationButton);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {


                        MarkerOptions currentLocation = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(getAddress(location.getLatitude(), location.getLongitude()));
                        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                        mMap.addMarker(currentLocation);

                    }
                };

                if (Build.VERSION.SDK_INT < 23) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {

                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                    }
                }
            }
        });


        showTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                System.out.println(address);

                if (address.getPostalCode() != null)
                    result.append(address.getPostalCode()).append("\n");
                if (address.getThoroughfare() != null) {
                    result.append((address.getThoroughfare())).append("\n");
                }
                if (address.getCountryName() != null)
                    result.append(address.getCountryName());
            }
        } catch (IOException e) {

        }

        return result.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }
    }

    public void setPlace1(LatLng loc, String description) {
        place1 = new MarkerOptions().position(loc).title(description);
    }

    public void setPlace2(LatLng loc, String description) {
        place2 = new MarkerOptions().position(loc).title(description);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra(MainActivity.LAT_VALUE, 0);
        double log = intent.getDoubleExtra(MainActivity.LONG_VALUE, 0);
        mMap = googleMap;


        MarkerOptions place = new MarkerOptions().position(new LatLng(lat, log)).title(getAddress(lat, log));
        mMap.addMarker(place);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, log)));


    }


    public void addTrack(LatLng loc1, LatLng loc2) {

        setPlace1(loc1, getAddress(loc1.latitude, loc1.longitude));
        setPlace2(loc2, getAddress(loc2.latitude, loc2.longitude));


        mMap.addMarker(place1);
        mMap.addMarker(place2);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc1));

        String url = getUrl(place1.getPosition(), place2.getPosition(), getResources().getString(R.string.driving));

        new FetchURL(MapsActivity.this).execute(url, getResources().getString(R.string.driving));

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {

        String strOrigin = getResources().getString(R.string.origin) + origin.latitude + getResources().getString(R.string.coma) + origin.longitude;

        String strDestination = getResources().getString(R.string.destination) + dest.latitude + getResources().getString(R.string.coma) + dest.longitude;

        String mode = getResources().getString(R.string.mode) + directionMode;

        String parameters = strOrigin + getResources().getString(R.string.one) + strDestination + getResources().getString(R.string.one) + directionMode;

        String output = getResources().getString(R.string.json);

        String url = getResources().getString(R.string.url) + output + "?" + parameters + getResources().getString(R.string.key) + getString(R.string.google_maps_key);

        return url;

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();

        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    public void createDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View locationPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        latitudeEditSource = (EditText) locationPopupView.findViewById(R.id.latitudeSource);
        longitudeEditSource = (EditText) locationPopupView.findViewById(R.id.longitudeSource);
        latitudeEditDestination = (EditText) locationPopupView.findViewById(R.id.latitudeDestination);
        longitudeEditDestination = (EditText) locationPopupView.findViewById(R.id.longitudeDestination);

        search = (Button) locationPopupView.findViewById(R.id.searchButton);
        dialogBuilder.setView(locationPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
        mMap.clear();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    LatLng locSource = new LatLng(Double.parseDouble(latitudeEditSource.getText().toString()), Double.parseDouble(longitudeEditSource.getText().toString()));
                    LatLng locDestination = new LatLng(Double.parseDouble(latitudeEditDestination.getText().toString()), Double.parseDouble(longitudeEditDestination.getText().toString()));
                    addTrack(locSource, locDestination);
                    dialog.dismiss();
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.coordinate_spec), Toast.LENGTH_LONG).show();
                }

            }

        });


    }

}

