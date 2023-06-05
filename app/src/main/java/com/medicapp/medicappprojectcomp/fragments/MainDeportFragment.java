package com.medicapp.medicappprojectcomp.fragments;

import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.adapters.Sport;
import com.medicapp.medicappprojectcomp.adapters.SportAdapter;
import com.medicapp.medicappprojectcomp.databinding.FragmentMedicalDiagnosticBinding;
import com.medicapp.medicappprojectcomp.utils.ReadFiles;
import com.medicapp.medicappprojectcomp.databinding.FragmentMainDeportBinding;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainDeportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainDeportFragment extends Fragment {

    FragmentMainDeportBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    SportAdapter sportAdapter;

    public MainDeportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainDeportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainDeportFragment newInstance(String param1, String param2) {
        MainDeportFragment fragment = new MainDeportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainDeportBinding.inflate(inflater);
        iniElement();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    View.OnClickListener buttonAddG=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            println("test");
        }
    };

    private  void iniElement(){


        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Sport> sportList = new ArrayList<>();

        sportList = readData();

        sportAdapter = new SportAdapter(sportList, getContext());

        binding.recycler.setAdapter(sportAdapter);


    }

    private List<Sport> readData(){
        List<Sport> sportList = new ArrayList<>();
        String jsonFileContent = null;
        try {
            jsonFileContent = ReadFiles.readJson(getContext(), "sports.json");

            JSONObject myJsonBase = new JSONObject(jsonFileContent);
            JSONArray myJsonData = myJsonBase.getJSONArray("Sports");


            for(int i =0;i<myJsonData.length();i++){

                String name = myJsonData.getJSONObject(i).get("Name").toString();
                String level = myJsonData.getJSONObject(i).get("Level").toString();
                String routine = myJsonData.getJSONObject(i).get("Routine").toString();
                String days = myJsonData.getJSONObject(i).get("Days").toString();
                String sport = myJsonData.getJSONObject(i).get("Sport").toString();
                String sportPng = myJsonData.getJSONObject(i).get("SportPng").toString();

                sportList.add(new Sport( name, level, routine, days, sport,  sportPng));

            }
            return sportList;

        }
        catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
