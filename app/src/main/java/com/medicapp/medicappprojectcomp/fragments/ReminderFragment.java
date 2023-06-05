package com.medicapp.medicappprojectcomp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.LoginActivity;
import com.medicapp.medicappprojectcomp.databinding.FragmentReminderBinding;
import com.medicapp.medicappprojectcomp.models.Glucose;
import com.medicapp.medicappprojectcomp.models.Reminder;
import com.medicapp.medicappprojectcomp.servicies.AlertsHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ReminderFragment extends BaseFragment {

    FragmentReminderBinding binding;
    List<Glucose> listGlucose;
    LineChart lineChart;
    Map<String,Boolean> mapDays;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReminderBinding.inflate(inflater);
        alerts=new AlertsHelper(getContext());
        mapDays=new HashMap<>();
        addCalendatInputText();
        binding.buttonAddReminder.setOnClickListener(v->addReminder());
        return binding.getRoot();
    }

    private void loadCheckBox() {
        mapDays.put("Lunes",binding.chackMonday.isChecked());
        mapDays.put("Martes",binding.chackTuesday.isChecked());
        mapDays.put("Miercoles",binding.chackWenesday.isChecked());
        mapDays.put("Jueves",binding.chackThursday.isChecked());
        mapDays.put("Viernes",binding.chackFriday.isChecked());
        mapDays.put("Sabado",binding.chackSaturday.isChecked());
        mapDays.put("Domingo",binding.chackSunday.isChecked());
    }

    private void addReminder() {
        Reminder reminder=loadReminder();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");;
        String key = userRef.child("reminder").push().getKey();
        userRef.child(user.getUid()).child("reminder").child(key).setValue(reminder)
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
        binding.textTitle.setText("");
        binding.dateStartTextInput.getEditText().setText("");
        binding.dateEndTextInput.getEditText().setText("");
        binding.chackMonday.setChecked(false);
        binding.chackTuesday.setChecked(false);
        binding.chackWenesday.setChecked(false);
        binding.chackThursday.setChecked(false);
        binding.chackFriday.setChecked(false);
        binding.chackSaturday.setChecked(false);
        binding.chackSunday.setChecked(false);
    }


    private Reminder loadReminder() {
        Reminder reminder=new Reminder();
        List<String> days = new ArrayList<>();
        reminder.setTitle(binding.textTitle.getText().toString());
        reminder.setDateStart(binding.dateStartTextInput.getEditText().getText().toString());
        reminder.setDateEnd(binding.dateEndTextInput.getEditText().getText().toString());
        loadCheckBox();
        mapDays.forEach((k,v)->{
            if(v){
                days.add(k);
            }
        });
        reminder.setDays(days);
        int hour = binding.datePicker.getHour();
        int minute = binding.datePicker.getMinute();
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        reminder.setHour(time);
        return reminder;
    }

    private void addCalendatInputText() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialogDateStart = new DatePickerDialog(getContext(),
                R.style.datePickerDialog,(view, year1, monthOfYear, dayOfMonth) -> {
            String monthZero = String.format("%02d", (monthOfYear + 1));
            String dayZero = String.format("%02d", dayOfMonth);
            String selectedDate =  year1+"-"+ monthZero + "-" +dayZero;
            binding.dateStartEditText.setText(selectedDate);
        }, year, month, day);

        binding.dateStartEditText.setOnClickListener(v -> datePickerDialogDateStart.show());
        binding.dateStartEditText.setInputType(InputType.TYPE_NULL);

        DatePickerDialog datePickerDialogDateEnd = new DatePickerDialog(getContext(),
                R.style.datePickerDialog,(view, year1, monthOfYear, dayOfMonth) -> {
            String monthZero = String.format("%02d", (monthOfYear + 1));
            String dayZero = String.format("%02d", dayOfMonth);
            String selectedDate =  year1+"-"+ monthZero + "-" +dayZero;
            binding.timeEditText.setText(selectedDate);
        }, year, month, day);

        binding.timeEditText.setOnClickListener(v -> datePickerDialogDateEnd.show());
        binding.timeEditText.setInputType(InputType.TYPE_NULL);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser() == null){
            exit();
        } else {
            user = firebaseAuth.getCurrentUser();
        }
    }

    public void exit(){
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}