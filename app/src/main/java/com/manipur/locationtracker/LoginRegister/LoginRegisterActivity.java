package com.manipur.locationtracker.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.manipur.locationtracker.R;
import com.manipur.locationtracker.RoadConnection;
import com.manipur.locationtracker.Utils.HighwayConnection;
import com.manipur.locationtracker.Utils.ViewPagerAdapter;

import java.util.ArrayList;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterTag";
    ViewPager viewPager;
    ProgressDialog progressDialog;
    ArrayList<RoadConnection> roadConnectionList = new ArrayList<>();
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Initialising views
        _init();

        // Fetch Highways Data
        showDialog();
        HighwayConnection.getConnection(new HighwayConnection.ConnectionList() {
            @Override
            public void getList(ArrayList<RoadConnection> connections) {
                dismissDialog();
                roadConnectionList = connections;

                Intent intent = new Intent("com.manipur.locationtracker.LoginRegister.action");
/*
                Bundle bundle = new Bundle();
                bundle.putSerializable("RoadConnectionList", connections);
                intent.putExtra("Bundle", bundle);
*/
                sendBroadcast(intent);
            }

            @Override
            public void getError(Exception e) {
                dismissDialog();
                Log.d(TAG, "getError: " + e.getMessage());

                Intent intent = new Intent("com.manipur.locationtracker.LoginRegister.action");
                Bundle bundle = new Bundle();
                bundle.putSerializable("RoadConnectionList", roadConnectionList);
                intent.putExtra("Bundle", bundle);
                sendBroadcast(intent);

                Toast.makeText(LoginRegisterActivity.this, "Error occurred\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        SetupViewPagerMotion();
    }

    public ArrayList<RoadConnection> getRoadConnectionList(){
        return roadConnectionList;
    }

    private void SetupViewPagerMotion() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    titleView.setText("Registration");
                }
                else{
                    titleView.setText("Login");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void _init() {
        viewPager = findViewById(R.id.login_register_viewpager);
        titleView = findViewById(R.id.login_register_title);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RegisterFragment(), "Register");
        adapter.addFragment(new LoginFragment(), "Login");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching locations");
        progressDialog.setMessage("Please wait...");

    }

    private void showDialog(){
        if(progressDialog!=null){
            progressDialog.show();
        }
    }

    private void dismissDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    public void movePager(int pos){
        viewPager.setCurrentItem(pos);
    }
}