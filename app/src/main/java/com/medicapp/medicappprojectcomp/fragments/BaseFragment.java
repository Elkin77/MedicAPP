package com.medicapp.medicappprojectcomp.fragments;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.medicapp.medicappprojectcomp.databinding.FragmentRegistryInsulinBinding;
import com.medicapp.medicappprojectcomp.servicies.AlertsHelper;

import javax.inject.Inject;

public class BaseFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String state;
    @Inject
    AlertsHelper alerts;

}
