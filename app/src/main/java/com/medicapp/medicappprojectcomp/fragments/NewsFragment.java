package com.medicapp.medicappprojectcomp.fragments;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.adapters.AdapterNews;
import com.medicapp.medicappprojectcomp.adapters.AdapterPointMap;
import com.medicapp.medicappprojectcomp.databinding.FragmentMapBinding;
import com.medicapp.medicappprojectcomp.databinding.FragmentNewsBinding;
import com.medicapp.medicappprojectcomp.models.News;
import com.medicapp.medicappprojectcomp.models.PositionMap;
import com.medicapp.medicappprojectcomp.servicies.GeocoderService;
import com.medicapp.medicappprojectcomp.servicies.LocationService;
import com.medicapp.medicappprojectcomp.utils.BitmapUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewsFragment extends Fragment {

    FragmentNewsBinding binding;

    @Inject
    LocationService locationService;
    @Inject
    GeocoderService geocoderService;

    //Map interaction variables
    GoogleMap googleMap;
    static final int INITIAL_ZOOM_LEVEL = 18;
    Marker userPosition;
    Polyline userRoute;
    //light sensor
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorEventListener;
    private LocationManager locationManager;
    Marker marker;
    LatLng latLngUser;
    private static AdapterNews adapter;
    List<News> news;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater);
        news=loadInfo();
        adapter= new AdapterNews(news);
        RecyclerView recyclerViewPoint=(RecyclerView) binding.listNews;
        recyclerViewPoint.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPoint.setAdapter(adapter);
        return binding.getRoot();
    }


    private List<News> loadInfo(){
        List<News> news=new ArrayList<>();
        news.add( new News("Haz más actividad física.","Leer noticia..."));
        news.add(new News("Consume grasas saludables","Leer noticia..."));
        news.add(new News("Mantener una hidratación adecuada","Leer noticia..."));
        news.add(new News("Actuar rápido ante una hipoglucemia","Leer noticia..."));
        return news;
    }






}