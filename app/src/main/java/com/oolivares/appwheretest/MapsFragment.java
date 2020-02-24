package com.oolivares.appwheretest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private String place;
    private GoogleMap mMap;
    private FusedLocationProviderClient fused;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    private Location location;
    private Double latitud;
    private Double longitud;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private MerchantsInterface listener;
    private List<Merchants> merchants;


    public static MapsFragment newInstance() {
        MapsFragment f = new MapsFragment();
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        fused = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myMAPF.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                createLocationRequest();
            }
        });

        merchants = listener.getMerchants();
        return rootView;
    }

    public void setinterface(MerchantsInterface listener){
        this.listener = listener;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getlocation();
    }

    private void getlocation() {
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED && checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
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

    public interface MerchantsInterface{
        List<Merchants> getMerchants();
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
                    mMap.setMyLocationEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, 15));
                        setMarkers();
                    fused.removeLocationUpdates(callback);
                }
            }
        };
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addAllLocationRequests(Collections.singleton(locationRequest));

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        Log.d("settings",settingsClient.toString());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getlocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {

                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),0);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }

    private void setMarkers() {
        for (Merchants merchant :merchants){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(merchant.getLatitude(),merchant.getLongitude()))
                    .title(merchant.getMerchantName())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ico_localization))
            );
            markers.add(marker);
        }
    }

    public interface GetMerchants{
        List<Merchants> getMerchants();
    }
}
