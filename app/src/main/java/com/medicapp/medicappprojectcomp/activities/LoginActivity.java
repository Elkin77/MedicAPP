package com.medicapp.medicappprojectcomp.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.databinding.ActivityLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity  extends BaseActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        binding.buttonLogin.setOnClickListener(v -> validLogin());
        binding.buttonRecovery.setOnClickListener(v -> goRecovery());

    }

    private void goRecovery() {
        startActivity(new Intent(this, RecoveryPasswordActivity.class));
    }


    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        updateUserCurrent(currentUser);
    }

    private void updateUserCurrent(FirebaseUser currentUser) {
        if(currentUser!=null){
            Intent intent=new Intent(getBaseContext(),PrincipalPatientActivity.class);
            intent.putExtra("user",currentUser.getEmail());
            startActivity(intent);
        }else{
            binding.userName.getEditText().setText("");
            binding.textPassword.getEditText().setText("");
        }
    }

    private void validLogin(){
        String email=binding.userName.getEditText().getText().toString();
        String password=binding.textPassword.getEditText().getText().toString();
        binding.userName.setError(null);
        binding.textPassword.setError(null);
        if(email.isEmpty()){
            binding.userName.setError(getString(R.string.error_empty));
           return;
        }else{
            binding.userName.setErrorEnabled(false);
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.userName.getEditText().getText().toString()).matches()){
            binding.userName.setErrorIconDrawable(R.drawable.ic_mail_error);
            binding.userName.setError(getString(R.string.error_email));
            return;
        }else {
            binding.userName.setErrorEnabled(false);
        }
        if(password.isEmpty()){
            binding.textPassword.setError(getString(R.string.error_empty));
            return ;
        }else{
            binding.textPassword.setErrorEnabled(false);
        }
        firebaseAuth.signInWithEmailAndPassword(binding.userName.getEditText().getText().toString(),
                binding.textPassword.getEditText().getText().toString()).addOnSuccessListener(authResult -> {
            startActivity(new Intent(this, PrincipalPatientActivity.class));
        }).addOnFailureListener(e -> {
            alerts.indefiniteSnackbar(binding.getRoot(),getResources().getString(R.string.messageInfo));
        });

    }


    }