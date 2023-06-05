package com.medicapp.medicappprojectcomp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.databinding.ActivityRecoveryPasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecoveryPasswordActivity extends BaseActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    ActivityRecoveryPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecoveryPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonSend.setOnClickListener(v->{forgotPass();});
    }


    private void forgotPass(){
        if(binding.userName.getEditText().getText().toString().isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.userName.getEditText().getText().toString()).matches()){
            binding.userName.setError(getString(R.string.error_email));
            return;
        } else{
            binding.userName.setErrorEnabled(false);
        }
        firebaseAuth.sendPasswordResetEmail(binding.userName.getEditText().getText().toString())
                .addOnSuccessListener(aVoid -> {
                    Handler handler = new Handler();
                    Intent intent = new Intent(this, LoginActivity.class);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    },2000);
                    alerts.indefiniteSnackbar(binding.getRoot(), "Por favor valide su correo");
                }).addOnFailureListener(e -> {
                    alerts.indefiniteSnackbar(binding.getRoot(), e.getLocalizedMessage());
                });
    }
}