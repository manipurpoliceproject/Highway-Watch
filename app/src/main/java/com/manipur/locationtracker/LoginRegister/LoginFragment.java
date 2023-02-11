package com.manipur.locationtracker.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.manipur.locationtracker.MainActivity;
import com.manipur.locationtracker.R;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.TimeStamp;
import com.manipur.locationtracker.Utils.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragmentTag";

    public LoginFragment() {
        // Required empty public constructor
    }

    EditText carNum, phoneNum;
    Button loginBtn;
    LinearLayout register;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        _init(view);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRegisterActivity activity = (LoginRegisterActivity) getActivity();
                activity.movePager(0);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allDataFilled()) {
                    showDialog();
                    String carNumber = carNum.getText().toString().trim().toLowerCase();
                    String phoneNumber = phoneNum.getText().toString().trim().toLowerCase();
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(carNumber)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    dismissDialog();
                                    if(dataSnapshot==null || !dataSnapshot.exists()){
                                        Toast.makeText(getContext(), "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    User user = dataSnapshot.getValue(User.class);
                                    if(user==null || user.getPhoneNumber()==null){
                                        Toast.makeText(getContext(), "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if(!user.getPhoneNumber().equalsIgnoreCase(phoneNumber)){
                                        Toast.makeText(getContext(), "Invalid Credentials!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Toast.makeText(getContext(), "Login Successful!!", Toast.LENGTH_SHORT).show();

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
            }
        });

        return view;
    }

    private boolean allDataFilled() {
        if(carNum.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please enter Car Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(phoneNum.getText().toString().trim().isEmpty() || phoneNum.getText().toString().length()!=10){
            Toast.makeText(getContext(), "Please enter valid Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void _init(View view) {
        carNum = view.findViewById(R.id.login_car_number);
        phoneNum = view.findViewById(R.id.login_phone_number);

        loginBtn = view.findViewById(R.id.login_login_btn);
        register = view.findViewById(R.id.login_register);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");
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