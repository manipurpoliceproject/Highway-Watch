package com.manipur.locationtracker.LoginRegister;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.manipur.locationtracker.MainActivity;
import com.manipur.locationtracker.R;
import com.manipur.locationtracker.RoadConnection;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.TimeStamp;
import com.manipur.locationtracker.Utils.User;

import java.util.ArrayList;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragmentTag";

    public RegisterFragment() {
        // Required empty public constructor
    }

    EditText carNum, phoneNum, inChargeName;
    Spinner highwaySpinner, zoneSpinner, sectorSpinner;
    Button register;
    LinearLayout login;
    ScrollView mainLl;
    ProgressDialog progressDialog;


    private ArrayList<RoadConnection> roadConnectionArrayList = new ArrayList<>();

    private BroadcastReceiver locationListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase("com.manipur.locationtracker.LoginRegister.action")) {
                    LoginRegisterActivity activity = (LoginRegisterActivity) getActivity();
                    roadConnectionArrayList = activity.getRoadConnectionList();
                    if (roadConnectionArrayList == null) {
                        roadConnectionArrayList = new ArrayList<>();
                    }

                    Log.d(TAG, "onReceive: Size: " + roadConnectionArrayList.size());
                    SetupHighways();
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Views init
        _init(view);

        // Setup Spinner motion
        SetupSpinnerMotion();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRegisterActivity activity = (LoginRegisterActivity) getActivity();
                activity.movePager(1);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allDataFilled()) {
                    showDialog();

                    // Check if car num alr registered or not...
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(carNum.getText().toString().trim().toLowerCase())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot == null || !dataSnapshot.exists()) {
                                        RegisterUser();
                                    } else {
                                        dismissDialog();
                                        Toast.makeText(getContext(), "Car number already registered!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dismissDialog();
                                    Toast.makeText(getContext(), "An error occurred!!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }

    private void RegisterUser() {
        User user = getUser();
        TimeStamp.getTimeStamp(new TimeStamp.TimeListener() {
            @Override
            public void getTime(long time) {
                long serverTime = time;
                user.setTime(serverTime);

                FirebaseDatabase.getInstance().getReference("User")
                        .child(user.getCarNumber())
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dismissDialog();
                                Toast.makeText(getContext(), "User Registered!!", Toast.LENGTH_SHORT).show();

                                String gson = new Gson().toJson(user);
                                SharedPrefHelper.addString(getContext(), "User", gson);

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dismissDialog();
                                Toast.makeText(getContext(), "An error occurred!!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private User getUser() {
        User user = new User();
        user.setCarNumber(carNum.getText().toString().trim().toLowerCase());
        user.setPoliceInChargeName(inChargeName.getText().toString().trim().toLowerCase());
        user.setPhoneNumber(phoneNum.getText().toString().trim().toLowerCase());

        user.setHighwayNumber(highwaySpinner.getSelectedItem().toString().toLowerCase());
        user.setZoneNumber(zoneSpinner.getSelectedItem().toString().toLowerCase());
        user.setSectorNumber(sectorSpinner.getSelectedItem().toString().toLowerCase());

        return user;
    }

    private boolean allDataFilled() {
        if (carNum.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter Car number!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (inChargeName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter Police InCharge Name!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phoneNum.getText().toString().trim().isEmpty() || phoneNum.getText().toString().length() != 10) {
            Toast.makeText(getContext(), "Please enter valid Phone number!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (highwaySpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please select a valid Highway", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sectorSpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please select a valid Sector", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zoneSpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please select a valid Zone", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            getContext().registerReceiver(locationListReceiver, new IntentFilter("com.manipur.locationtracker.LoginRegister.action"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            getContext().unregisterReceiver(locationListReceiver);
        }
    }

    private void _init(View view) {
        carNum = view.findViewById(R.id.register_car_number);
        phoneNum = view.findViewById(R.id.register_phone_number);
        inChargeName = view.findViewById(R.id.register_police_incharge_name);
        highwaySpinner = view.findViewById(R.id.register_highway_spinner);
        zoneSpinner = view.findViewById(R.id.register_zones_spinner);
        sectorSpinner = view.findViewById(R.id.register_sector_spinner);
        mainLl = view.findViewById(R.id.register_main_ll);

        register = view.findViewById(R.id.register_register_btn);
        login = view.findViewById(R.id.register_login);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Please wait...");

//        mainLl.getBackground().setAlpha(60);
    }

    private void SetupSpinnerMotion() {
        highwaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SetupSpinner(zoneSpinner, new ArrayList<>());
                SetupSpinner(sectorSpinner, new ArrayList<>());

                ArrayList<String> zones = new ArrayList<>(), sector = new ArrayList<>();
                String high = highwaySpinner.getSelectedItem().toString();
                for (RoadConnection connection : roadConnectionArrayList) {
                    if (connection.getHighway().equalsIgnoreCase(high)) {
                        String zone = connection.getZone();
                        String sec = connection.getSector();
                        if (!zones.contains(zone)) {
                            zones.add(zone);
                        }

                        if (!sector.contains(sec)) {
                            sector.add(sec);
                        }
                    }
                }

                SetupSpinner(zoneSpinner, zones);
                SetupSpinner(sectorSpinner, sector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        zoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SetupSpinner(sectorSpinner, new ArrayList<>());

                String high = highwaySpinner.getSelectedItem().toString();
                String zone = zoneSpinner.getSelectedItem().toString();

                ArrayList<String> sector = new ArrayList<>();

                for (RoadConnection connection : roadConnectionArrayList) {
                    if (connection.getHighway().equalsIgnoreCase(high) && connection.getZone().equalsIgnoreCase(zone)) {
                        String sec = connection.getSector();
                        if (!sector.contains(sec)) {
                            sector.add(sec);
                        }
                    }
                }

                SetupSpinner(sectorSpinner, sector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void SetupSpinner(Spinner spinner, ArrayList<String> list) {
        String[] listArr = list.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listArr);
        spinner.setAdapter(adapter);
    }

    private void SetupHighways() {
        ArrayList<String> highways = new ArrayList<>();
        for (RoadConnection connection : roadConnectionArrayList) {
            String high = connection.getHighway();
            if (!highways.contains(high)) {
                highways.add(high);
            }
        }

        SetupSpinner(highwaySpinner, highways);
    }

    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}