package com.upc.applerta.ui.main;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upc.applerta.MainActivity;
import com.upc.applerta.R;
import com.upc.applerta.data.model.Alarm;
import com.upc.applerta.data.model.Confirmation;
import com.upc.applerta.data.util.GpsTracker;
import com.upc.applerta.data.util.RadiusAnimation;
import com.upc.applerta.ui.login.ForgotPasswordActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private View fragmentView;

    private static final int REQUEST_LOCATION = 1;
    private AnimationSet breadingAnimations;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_maps, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        breadingAnimations = new AnimationSet(true);
    }


    public static void getCurrentLocation(Context context, Activity activity, GoogleMap googleMap) {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    if(googleMap != null){
                        LatLng xDesign = new LatLng(latitude, longitude);

                        googleMap.addMarker(new MarkerOptions().position(xDesign).title("Tu ubicación actual"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xDesign, 16f));
                    }

                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);


        inicializarFirebase();

        databaseReference.child("Alarm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                googleMap.clear();
                getCurrentLocation(getContext(), getActivity(), googleMap);

                for(DataSnapshot item:snapshot.getChildren()){
                    Alarm a = item.getValue(Alarm.class);

                    LatLng xDesign = new LatLng(a.getLat(), a.getLon());
                    googleMap.addMarker(new MarkerOptions()
                            .position(xDesign).title("Alarma")
                            .alpha(0.7f)
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_flag_24))
                    .snippet("Registrada por " + a.getDisplayName() + " el " + a.getDateTime()));
                    drawCircle(xDesign);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void drawCircle(LatLng point){

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(100);
        circleOptions.strokeColor(Color.WHITE);
        circleOptions.fillColor(0x30ff0000);
        circleOptions.strokeWidth(2);

        googleMap.addCircle(circleOptions);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.danger);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void moveCamaraMapReady(double latitude, double longitude) {
        LatLng xDesign = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(xDesign, 16f));

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public int confirmAlarm(String alarmId, String displayName, double lat, double lon) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getDisplayName().equals(displayName)){
            Toast.makeText(getActivity(), "No puedes confirmar tu propia alarma.",Toast.LENGTH_SHORT).show();
            return 1;
        }
        else{
            Confirmation a = new Confirmation();
            a.setConfirmationId(UUID.randomUUID().toString());
            a.setAlarmId(alarmId);
            a.setDisplayName(displayName);
            Date currentTime = Calendar.getInstance().getTime();
            a.setDateTime(currentTime.toString());
            a.setLat(lat);
            a.setLon(lon);
            a.setAuthId(user.getUid());

            databaseReference.child("Confirmation").child(a.getConfirmationId()).setValue(a);
            Toast.makeText(getActivity(), "Confirmación de alarma realizada con éxito.",Toast.LENGTH_SHORT).show();
            return 0;
        }

    }
}