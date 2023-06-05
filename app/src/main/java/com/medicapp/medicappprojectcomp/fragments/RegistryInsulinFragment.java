package com.medicapp.medicappprojectcomp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.LoginActivity;
import com.medicapp.medicappprojectcomp.databinding.FragmentRegistryInsulinBinding;
import com.medicapp.medicappprojectcomp.models.Insulin;
import com.medicapp.medicappprojectcomp.servicies.AlertsHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;


public class RegistryInsulinFragment extends BaseFragment {
    FragmentRegistryInsulinBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistryInsulinBinding.inflate(inflater);
        alerts=new AlertsHelper(getContext());
        addCalendatInputText();
        defineState();
        binding.buttonAddInsulin.setOnClickListener(v->addRegister());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addRegister() {
        Insulin insulin=getDataInsulin();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");;
        String insulinKey = userRef.child("history_insulin").push().getKey();
        userRef.child(user.getUid()).child("history_insulin").child(insulinKey).setValue(insulin)
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
        binding.comment.getEditText().setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Insulin getDataInsulin() {
        Insulin insulin=new Insulin();
        String number=binding.measureTextInput.getEditText().getText().toString();
        insulin.setQuantity(Double.parseDouble(number.contains(".")?number:number+".01"));
        insulin.setDate(binding.dateTextInput.getEditText().getText().toString());
        int hour = binding.timePickerInsul.getHour();
        int minute = binding.timePickerInsul.getMinute();
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        insulin.setHour(time);
        insulin.setState(state);
        insulin.setComment(binding.comment.getEditText().getText().toString());
        return insulin;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void defineState(){
        LinearLayout linearLayout = binding.linearState;
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica a ejecutar cuando se hace clic en cualquiera de los ImageView
                switch (v.getId()) {
                    case R.id.depressiveImageIns:
                        state="1";
                        break;
                    case R.id.verySadImageIns:
                        state="2";
                        break;
                    case R.id.sadImageIns:
                        state="3";
                        break;
                    case R.id.happyImageIns:
                        state="4";
                        break;
                    case R.id.happyTImageIns:
                        state="5";
                        break;
                }
            }
        };


        binding.depressiveImageIns.setOnClickListener(clickListener);
        binding.verySadImageIns.setOnClickListener(clickListener);
        binding.sadImageIns.setOnClickListener(clickListener);
        binding.happyImageIns.setOnClickListener(clickListener);
        binding.happyTImageIns.setOnClickListener(clickListener);
    }
}