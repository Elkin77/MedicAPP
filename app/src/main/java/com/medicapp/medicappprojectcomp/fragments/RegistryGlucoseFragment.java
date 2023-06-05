package com.medicapp.medicappprojectcomp.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;
import com.medicapp.medicappprojectcomp.databinding.FragmentRegistryGlucoseBinding;
import com.medicapp.medicappprojectcomp.models.Glucose;
import com.medicapp.medicappprojectcomp.servicies.AlertsHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RegistryGlucoseFragment extends BaseFragment {

    FragmentRegistryGlucoseBinding binding;
    List<Glucose> listGlucose;
    LineChart lineChart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistryGlucoseBinding.inflate(inflater);
        alerts=new AlertsHelper(getContext());
        addCalendatInputText();
        validUser();
        loadGraphicsGlucose();
        binding.buttonAddG.setOnClickListener(v->addRegisterGlucose());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addRegisterGlucose() {
        Glucose glucose=getDataGlucose();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");;
        String insulinKey = userRef.child("history_glucose").push().getKey();
        userRef.child(user.getUid()).child("history_glucose").child(insulinKey).setValue(glucose)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alerts.indefiniteSnackbar(binding.getRoot(), getResources().getString(R.string.updateSuccesfull));
                        clearControls();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alerts.indefiniteSnackbar(binding.getRoot(), getResources().getString(R.string.updateError));
                    }
                });
    }

    private void clearControls() {
        binding.measureTextInput.getEditText().setText("");
        binding.dateTextInput.getEditText().setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Glucose getDataGlucose() {
        Glucose glucose=new Glucose();
        String number=binding.measureTextInput.getEditText().getText().toString();
        glucose.setQuantity(Double.parseDouble(number.contains(".")?number:number+".01"));
        glucose.setDate(binding.dateTextInput.getEditText().getText().toString());
        int hour = binding.timePickerGluc.getHour();
        int minute = binding.timePickerGluc.getMinute();
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        glucose.setHour(time);
        return glucose;
    }

    private void addCalendatInputText() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogBirthday = new DatePickerDialog(getContext(),
                R.style.datePickerDialog,(view, year1, monthOfYear, dayOfMonth) -> {
            String monthZero = String.format("%02d", (monthOfYear + 1));
            String dayZero = String.format("%02d", dayOfMonth);
            String selectedDate =  year1+"-"+ monthZero + "-" +dayZero;
            binding.dateEditText.setText(selectedDate);
        }, year, month, day);

        binding.dateEditText.setOnClickListener(v -> datePickerDialogBirthday.show());
        binding.dateEditText.setInputType(InputType.TYPE_NULL);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void viewGauge() {

        lineChart = binding.lineChart;
        List<Entry> entries = new ArrayList<>();
        LocalDate now=LocalDate.now().minusDays(15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<Integer, Integer> dataReport=new HashMap<>();
        // Crear datos de muestra
        listGlucose.stream().forEach(i->{
            LocalDate date = LocalDate.parse(i.getDate(),formatter);
            if(date.isAfter(now)){
                if(dataReport.get(date.getDayOfMonth())!=null){
                    dataReport.put(date.getDayOfMonth(),(int) (dataReport.get(date.getDayOfMonth())+Math.round(i.getQuantity())));
                }else{
                    dataReport.put(date.getDayOfMonth(),(int) Math.round(i.getQuantity()));
                }
          }
        });
         dataReport.forEach((k,v)->{
             entries.add(new Entry(k, v));
         });

        // Crear conjunto de datos de línea
        LineDataSet dataSet = new LineDataSet(entries, "Glucosa aplicada por día");

        // Configurar el conjunto de datos
        dataSet.setColor(Color.parseColor("#8F95C3"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.parseColor("#f44336"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(6f);
        dataSet.setValueTextSize(12f);


        // Crear una instancia de LineData y agregar el conjunto de datos
        LineData lineData = new LineData(dataSet);

        // Configurar el gráfico de líneas
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(true);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisRight().setEnabled(true);

        // Configurar la leyenda
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        lineChart.invalidate();

    }
    private Glucose loadDataGlucose(DataSnapshot element) {
        Glucose glucose=new Glucose();
        glucose.setHour((String) element.child("hour").getValue());
        glucose.setDate((String) element.child("date").getValue());
        Double dateQua=(Double) element.child("quantity").getValue();
        glucose.setQuantity(dateQua);
        return glucose;
    }
}