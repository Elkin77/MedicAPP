package com.medicapp.medicappprojectcomp.fragments;

import static android.graphics.Color.RED;
import static android.service.controls.ControlsProviderService.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.adapters.AdapterPointMap;
import com.medicapp.medicappprojectcomp.databinding.FragmentMapBinding;
import com.medicapp.medicappprojectcomp.models.PositionMap;
import com.medicapp.medicappprojectcomp.servicies.GeocoderService;
import com.medicapp.medicappprojectcomp.servicies.LocationService;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    FragmentMapBinding binding;

    @Inject
    LocationService locationService;
    @Inject
    GeocoderService geocoderService;

    MapView map;
    MyLocationNewOverlay myLocation;
    org.osmdroid.views.overlay.Marker markerPosition;

    FirebaseDatabase database = FirebaseDatabase.getInstance();



    static final int INITIAL_ZOOM_LEVEL = 18;
    Marker userPosition;

    //light sensor
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorEventListener;
    private LocationManager locationManager;
    Marker marker;
    LatLng latLngUser;
    private static AdapterPointMap adapter;
    List<PositionMap> points;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater);
        adapter = new AdapterPointMap(points);
        RecyclerView recyclerViewPoint = (RecyclerView) binding.listPoints;
        recyclerViewPoint.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewPoint.setAdapter(adapter);
        map = binding.getRoot().findViewById(R.id.map1);
        //validateRoute();
        return binding.getRoot();

    }

    private void validateRoute() {
        RoadManager roadManager = new OSRMRoadManager(getContext());
        roadManager.addRequestOption("profile=driving");
        GeoPoint startPoint = new GeoPoint(4.6287655353352966, -74.06460013146082); // Torre Eiffel
        GeoPoint endPoint = new GeoPoint(4.6243382701642615, -74.06476106400758); // Big Ben
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        org.osmdroid.views.overlay.Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setColor(RED);
        roadOverlay.setWidth(5);
        List<GeoPoint> routePoints = roadOverlay.getActualPoints();
        for (GeoPoint point : routePoints) {
            Log.d(TAG, "Latitud: " + point.getLatitude() + " Longitud: " + point.getLongitude());
        }
        map.getOverlayManager().add(roadOverlay);
        map.invalidate();
        System.out.println();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        binding.addressInput.getEditText().setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_GO) {
                filterPoint();
                return true;
            }
            return false;
        });
        adapter.setOnItemClickListener(new AdapterPointMap.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PositionMap positionSel=adapter.getItem(position);
                map.getOverlays().forEach(overlay->{
                    if(overlay instanceof org.osmdroid.views.overlay.Marker){
                        org.osmdroid.views.overlay.Marker markerPos=(org.osmdroid.views.overlay.Marker) overlay;
                        if(markerPos.getTitle().contains(positionSel.getTitle())) {
                            markerPos.setIcon(getResources().getDrawable(R.drawable.ic_hospital_2));
                        }
                    }
                });
            }
        });

    }

    private void filterPoint() {
        List<PositionMap> poinstFilter;
        poinstFilter = points.stream().filter(c -> c.getTitle().contains(binding.addressInput.getEditText().getText())).collect(Collectors.toList());
        adapter.setList(poinstFilter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        points=loadInfo();
        if(points!=null && adapter!=null) {
            adapter.setDataSet(points);
        }
        super.onCreate(savedInstanceState);
    }

    private void findPlace() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                geocoderService.findPlacesByNameInRadius(binding.addressInput.getEditText().getText().toString(), userPosition.getPosition()).forEach(address -> {
                   LatLng lat = new LatLng(address.getLatitude(), address.getLongitude());

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(map!=null){
            loadHospital();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(@NotNull SensorEvent sensorEvent) {
                if (sensorEvent.values[0] > 10000) {
                    map.getOverlayManager().getTilesOverlay().setColorFilter(null);
                } else {
                    map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                }
                map.invalidate();
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.setBuiltInZoomControls(true);
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        map.getOverlays().add(scaleBarOverlay);
        validateRoute();
        loadHospital();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        if(markerPosition==null){
                            markerPosition = new org.osmdroid.views.overlay.Marker(map);
                            markerPosition.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                            markerPosition.setTitle("Mi posición");
                            markerPosition.setIcon(getResources().getDrawable(R.drawable.ic_point));
                            map.getOverlays().add(markerPosition);

                        }
                        markerPosition.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        map.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        // Convertir la posición del sensor a coordenadas de píxeles en el mapa
                       }
                    }
            }
        }, Looper.getMainLooper());
    }

    private List<PositionMap> loadInfo(){
        List<PositionMap> pointsMap=new ArrayList<>();
        DatabaseReference databaseRef = database.getReference("medicalCenters");
        Query query = databaseRef.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(point->{
                    DataSnapshot element = snapshot.getChildren().iterator().next();
                    pointsMap.add(loadPoint(point));
                });
                points=pointsMap;
                adapter.setDataSet(points);
                loadHospital();
            }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {
            }
         });
        return pointsMap;
    }
    public void loadHospital(){

        points.stream().forEach(p->{
            org.osmdroid.views.overlay.Marker markerHospital = new org.osmdroid.views.overlay.Marker(map);
            markerHospital.setPosition(new GeoPoint(p.getPosition().latitude, p.getPosition().longitude));
            markerHospital.setTitle(p.getTitle());
            markerHospital.setIcon(getResources().getDrawable(R.drawable.ic_point_hos));
            map.getOverlays().add(markerHospital);
        });
    }

    private PositionMap loadPoint(DataSnapshot element){
        PositionMap position=new PositionMap();
        position.setTitle((String) element.child("name").getValue());
        position.setAddress((String) element.child("address").getValue());
        position.setPhone((String) element.child("phone").getValue());
        position.setPosition(new LatLng(Double.parseDouble(element.child("latitude").getValue().toString()),
                Double.parseDouble(element.child("longitude").getValue().toString())));
        return position;

    }



}