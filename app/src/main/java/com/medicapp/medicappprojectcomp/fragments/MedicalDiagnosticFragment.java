package com.medicapp.medicappprojectcomp.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ekn.gruzer.gaugelibrary.FullGauge;
import com.ekn.gruzer.gaugelibrary.contract.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;
import com.medicapp.medicappprojectcomp.databinding.FragmentMedicalDiagnosticBinding;
import com.medicapp.medicappprojectcomp.models.Glucose;
import com.medicapp.medicappprojectcomp.models.Insulin;
import com.medicapp.medicappprojectcomp.models.Month;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class MedicalDiagnosticFragment extends BaseFragment {

    FragmentMedicalDiagnosticBinding binding;
    List<Insulin> listInsulin;
    Map<Month, Double> monthListInsulin;
    List<Glucose> listGlucose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicalDiagnosticBinding.inflate(inflater);
        validUser();
        loadGraphics();
        loadGraphicsGlucose();
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void viewGauge() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        AtomicReference<String> formattedValue= new AtomicReference<>("");

        FullGauge gaugeDay = binding.donutDay;
        gaugeDay.setMinValue(0);
        gaugeDay.setMaxValue(100);
        gaugeDay.setGaugeBackGroundColor(Color.parseColor("#FF018786"));


        gaugeDay.setFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(double value) {
                int intValue = Double.valueOf(value).intValue();
                return String.valueOf(intValue);
            }
        });

        FullGauge gaugeWeek = binding.donutWeek;
        gaugeWeek.setMinValue(0);
        gaugeWeek.setMaxValue(200);
        gaugeWeek.setGaugeBackGroundColor(Color.parseColor("#FF9800"));

        gaugeWeek.setFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(double value) {
                int intValue = Double.valueOf(value).intValue();
                return String.valueOf(intValue);
            }
        });

        FullGauge gaugeMonth = binding.donutMonth;
        gaugeMonth.setMinValue(0);
        gaugeMonth.setMaxValue(400);
        gaugeMonth.setGaugeBackGroundColor(Color.parseColor("#1F186C"));
        gaugeMonth.setFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(double value) {
                int intValue = Double.valueOf(value).intValue();
                return String.valueOf(intValue);
            }
        });

        // Establece el valor actual de la escala

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        listGlucose.stream().forEach(i->{
            LocalDate date = LocalDate.parse(i.getDate(),formatter);
            if(LocalDate.now().equals(date)){
                gaugeDay.setValue(gaugeDay.getValue()+i.getQuantity());
            }
            if(LocalDate.now().minusDays(7).isBefore(date)){
                gaugeWeek.setValue(gaugeWeek.getValue()+i.getQuantity());
            }
            if(LocalDate.now().minusDays(30).isBefore(date)){
                gaugeMonth.setValue(gaugeMonth.getValue()+i.getQuantity());
            }
        });
    }

    private void loadGraphics() {
        listInsulin=new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        DatabaseReference databaseRef = database.getReference("users/"+user.getUid()+"/history_insulin");
        Query query = databaseRef.orderByChild("date");
        monthListInsulin = new HashMap<>();
        for (Month month : Month.values()) {
            monthListInsulin.put(month,0.0);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(data->{
                    DataSnapshot element = snapshot.getChildren().iterator().next();
                    listInsulin.add(loadDataInsulin(data));
                });
                loadDataWithGraphics(listInsulin,monthListInsulin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
    private void loadGraphicsGlucose() {
        listGlucose=new ArrayList<>();
        DatabaseReference databaseRef = database.getReference("users/"+user.getUid()+"/history_glucose");
        Query query = databaseRef.orderByChild("date");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(data->{
                    DataSnapshot element = snapshot.getChildren().iterator().next();
                    listGlucose.add(loadDataGlucose(data));
                });
                viewGauge();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private Glucose loadDataGlucose(DataSnapshot element) {
        Glucose glucose=new Glucose();
        glucose.setHour((String) element.child("hour").getValue());
        glucose.setDate((String) element.child("date").getValue());
        Double dateQua=(Double) element.child("quantity").getValue();
        glucose.setQuantity(dateQua);
        return glucose;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadDataWithGraphics(List<Insulin> listInsulin, Map<Month, Double> mapData) {
        BarChart barChart=binding.idBarChart;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ArrayList<BarEntry> barItem = new ArrayList<>();
        int monthNow=LocalDate.now().getMonth().getValue();
        listInsulin.stream().forEach(i->{
            LocalDate date= null;
            date = LocalDate.parse(i.getDate(),formatter);
            if(date.getYear()== LocalDate.now().getYear()){
                Double prom=mapData.get(Month.fromValue(date.getMonthValue()))+i.getQuantity();
                mapData.put(Month.fromValue(date.getMonthValue()),prom);
            }

        });

        mapData.forEach((k,v)->{
             if(k.getValue()<=monthNow){
                 BarEntry barEntry = new BarEntry(k.getValue(),v.floatValue());
                 barItem.add(barEntry);
             }
        });
        BarDataSet barDataSet = new BarDataSet(barItem, "Insulina mensual");
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();

    }

    private Insulin loadDataInsulin(DataSnapshot element) {
        Insulin insulin=new Insulin();
        insulin.setHour((String) element.child("hour").getValue());
        insulin.setDate((String) element.child("date").getValue());
        Double dateQua=(Double) element.child("quantity").getValue();
        insulin.setQuantity(dateQua);
        return insulin;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.imageFood.setVisibility(View.INVISIBLE);
        binding.imageSport.setVisibility(View.INVISIBLE);
        binding.buttonMore.setOnClickListener(buttonListener);
        binding.imageSport.setOnClickListener(imageSportListener);
        binding.imageFood.setOnClickListener(imageFoodListener);
        binding.buttonAdd.setOnClickListener(buttonAddIns);
        binding.buttonAddG.setOnClickListener(buttonAddGlu);
        binding.buttonRemember.setOnClickListener(buttonRemember);
    }
    View.OnClickListener buttonRemember=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                changeFragment(new ReminderFragment());
        }
    };
    View.OnClickListener buttonListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            binding.imageFood.setVisibility(View.VISIBLE);
            binding.imageSport.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener buttonAddIns=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeFragment(new RegistryInsulinFragment());
        }
    };

    View.OnClickListener buttonAddGlu=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeFragment(new RegistryGlucoseFragment());
        }
    };
    View.OnClickListener imageSportListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeFragment(new MainDeportFragment());
        }
    };

    View.OnClickListener imageFoodListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           changeFragment(new FoodMainFragment());
        }
    };
    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        validUser();
    }

    private void validUser() {
        if(firebaseAuth.getCurrentUser() == null){
            if (this.getContext() instanceof PrincipalPatientActivity) {
                PrincipalPatientActivity activity = (PrincipalPatientActivity) getContext();
                activity.exit();
            }
        } else {
            user = firebaseAuth.getCurrentUser();
        }
    }

}