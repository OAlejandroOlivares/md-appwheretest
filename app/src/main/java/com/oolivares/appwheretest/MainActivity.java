package com.oolivares.appwheretest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, MerchantsFragment.MerchantsInterface, MapsFragment.MerchantsInterface {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FusedLocationProviderClient fused;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    private Location mlocation;
    private List<Merchants> merchants = new ArrayList<Merchants>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        fused = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        if (savedInstanceState == null){
            requestMerchants();
        }else{
            merchants = savedInstanceState.getParcelableArrayList("merchants");
            Fragment fragment= fragmentManager.getFragment(savedInstanceState,"restore");
            int selected = savedInstanceState.getInt("activebottomnav");
        }
        changeFragment(MapsFragment.newInstance());
    }

    private void requestMerchants() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://166.62.33.53:4590/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestInterface interfaz =retrofit.create(requestInterface.class);
        Call<ResponseBody> call = interfaz.getMerchants();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String a = response.body().string();
                    JSONObject jsonObject = new JSONObject(a);
                    Log.d("Success",jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("merchants");
                    merchants.clear();
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        merchants.add(new Merchants(
                                object.getString("id"),
                                object.getString("merchantName"),
                                object.getString("merchantAddress"),
                                object.getString("merchantTelephone"),
                                object.getDouble("latitude"),
                                object.getDouble("longitude"),
                                object.getString("registrationDate")
                        ));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.map:
                fragment = MapsFragment.newInstance();
                break;
            case R.id.sucursales:
                fragment = MerchantsFragment.newInstance();
                break;
            case R.id.agregar:
                fragment = AddMerchantFragment.newInstance();
                break;
        }
        changeFragment(fragment);
        return true;
    }

    private void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
    }

    private void getlocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }else {
            fused.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
            fused.getLocationAvailability().addOnCompleteListener(new OnCompleteListener<LocationAvailability>() {
                @Override
                public void onComplete(@NonNull Task<LocationAvailability> task) {
                    if (task.getResult().isLocationAvailable()) {
                        Log.d("available", "location available");
                    } else {
                        createLocationRequest();
                    }
                }
            });
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        callback = new LocationCallback(){
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null){
                    createLocationRequest();
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng miPosicion = new LatLng(location.getLatitude(), location.getLongitude());
                    mlocation = location;
                    //mMap.setMyLocationEnabled(true);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion,15));
                    //fused.removeLocationUpdates(callback);
                }
            }
        };
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addAllLocationRequests(Collections.singleton(locationRequest));

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Log.d("settings",settingsClient.toString());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                getlocation();
            }
        });
        final MainActivity activ = this;
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activ,0);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if(fragment instanceof MapsFragment){
            MapsFragment mapsFragment = (MapsFragment) fragment;
            mapsFragment.setinterface(this);
        }else if (fragment instanceof MerchantsFragment){
            MerchantsFragment merchantsFragment = (MerchantsFragment) fragment;
            merchantsFragment.setinterface(this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        fragmentManager.putFragment(outState,"restore",fragment);
        outState.putInt("activebottomnav",bottomNavigationView.getSelectedItemId());
        outState.putParcelableArrayList("merchants", new ArrayList<>(merchants));
    }


    @Override
    public List<Merchants> getMerchants() {
        return merchants;
    }

}
