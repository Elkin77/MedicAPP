package com.medicapp.medicappprojectcomp.fragments;

import static androidx.fragment.app.FragmentManager.TAG;
import static java.text.DateFormat.getDateInstance;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;
import com.medicapp.medicappprojectcomp.databinding.FragmentProfileBinding;
import com.medicapp.medicappprojectcomp.models.Patient;
import com.medicapp.medicappprojectcomp.servicies.AlertsHelper;
import com.medicapp.medicappprojectcomp.servicies.PermissionService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Patient patient;

    private Uri picturePath = null;
    private Bitmap bitMap=null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    static final int RESULT_LOAD_IMAGE = 3;
    static final int RESULT_LOAD_VIDEO = 4;

    FirebaseStorage storage;
    @Inject
    AlertsHelper alerts;
    @Inject
    PermissionService permissionService;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        storage=FirebaseStorage.getInstance();
        binding.buttonUpdate.setOnClickListener(view->updateProfile());
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickTake();
            }
        });
        alerts=new AlertsHelper(getContext());
        permissionService=new PermissionService(getContext());
        View root = binding.getRoot();
        validUser();
        getProfile();
        addCalendatInputText();
        binding.userNameEdit.setEnabled(false);
        return root;
    }

    private void updateProfile() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", binding.name.getEditText().getText().toString());
        updates.put("height", binding.height.getEditText().getText().toString());
        updates.put("weight", binding.weight.getEditText().getText().toString());
        updates.put("birthday", binding.birthday.getEditText().getText().toString());
        updates.put("dateDiagnostic", binding.dateDiagnostic.getEditText().getText().toString());
        ref.child(patient.getId()).updateChildren(updates);
        alerts.indefiniteSnackbar(binding.getRoot(),getResources().getString(R.string.updateSuccesfull));
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
                    binding.birthdayEdit.setText(selectedDate);
                }, year, month, day);
        DatePickerDialog datePickerDialogDiagnostic = new DatePickerDialog(getContext(),
                R.style.datePickerDialog,(view, year1, monthOfYear, dayOfMonth) -> {
                    String monthZero = String.format("%02d", (monthOfYear + 1));
                    String dayZero = String.format("%02d", dayOfMonth);
                    String selectedDate =  year1+"-"+ monthZero+ "-" +dayZero;
                    binding.dateDiagnosticEdit.setText(selectedDate);
                }, year, month, day);
        binding.birthdayEdit.setOnClickListener(v -> datePickerDialogBirthday.show());
        binding.birthdayEdit.setInputType(InputType.TYPE_NULL);
        binding.dateDiagnosticEdit.setOnClickListener(v -> datePickerDialogDiagnostic.show());
        binding.dateDiagnosticEdit.setInputType(InputType.TYPE_NULL);
    }


    private void getProfile() {
        DatabaseReference databaseRef = database.getReference("users");
        Query query = databaseRef.orderByChild("email").equalTo(user.getEmail().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot element = snapshot.getChildren().iterator().next();
                patient = new Patient();
                patient.setId(element.getKey());
                patient.setLastName((String) element.child("lastName").getValue());
                patient.setName((String) element.child("name").getValue());
                patient.setEmail((String) element.child("email").getValue());
                try {
                    patient.setBirthday(new SimpleDateFormat("yyyy-mm-dd").parse(element.child("birthday").getValue() != null ? (String) element.child("birthday").getValue() : "1900-01-01"));
                    patient.setDateDiagnostic(new SimpleDateFormat("yyyy-mm-dd").parse(element.child("dateDiagnostic").getValue() != null ? (String) element.child("dateDiagnostic").getValue(): "1900-01-01"));

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                patient.setHeight((Double) Double.parseDouble(element.child("height").getValue() != null ? element.child("height").getValue().toString() : "0.0"));
                patient.setWeight((Long) Long.parseLong(element.child("weight").getValue() != null ? element.child("weight").getValue().toString() : "0"));
                loadView(patient);
                getImageProfile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    private void getImageProfile() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+patient.getId());
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this)
                        .load(uri.toString())
                        .into(binding.imageUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

    }

    private void loadView(Patient patient) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        binding.name.getEditText().setText(patient.getName());
        binding.userName.getEditText().setText(patient.getEmail());
        binding.birthday.getEditText().setText(dateFormat.format(patient.getBirthday()));
        binding.dateDiagnostic.getEditText().setText(dateFormat.format(patient.getDateDiagnostic()));
        binding.height.getEditText().setText(String.valueOf(patient.getHeight()));
        binding.weight.getEditText().setText(String.valueOf(patient.getWeight()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    private void clickTake() {
        if ( !permissionService.isMCameraPermissionGranted()) {
            permissionService.getCameraPermission(getActivity());
        } else {
            takePhoto();
        }
    }
    @SuppressLint("RestrictedApi")
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String time = getDateInstance().format(new Date());
        String fileName = String.format("%s.jpg", time);
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + fileName);
        picturePath = FileProvider.getUriForFile(getContext(), "com.medicapp.medicappprojectcomp.fileprovider", file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picturePath);
        try {
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
    private void loadImage() {
        ImageView img= binding.imageUser;
        img.setImageURI(picturePath);
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" +(picturePath.getLastPathSegment()));
        if(picturePath!=null) {
            fileRef.getParent().child(patient.getId()).putFile(picturePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Carga exitosa
                    alerts.indefiniteSnackbar(binding.getRoot(), getResources().getString(R.string.updateSuccesfull));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Error al cargar
                    alerts.indefiniteSnackbar(binding.getRoot(), getResources().getString(R.string.updateError));

                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            bitMap=null;
            loadImage();
        } else if (requestCode == RESULT_LOAD_IMAGE  && data != null) {
            picturePath= data.getData();
            try {
                bitMap = MediaStore.Images.Media.getBitmap( this.getActivity().getContentResolver(),picturePath);
                bitMap=Bitmap.createScaledBitmap(bitMap,1000,1200,false);
                loadImage();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
