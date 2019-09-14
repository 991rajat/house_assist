package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_Profile extends AppCompatActivity {

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    private static final String TAG ="TAG" ;
    private LinearLayout layout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private FirebaseFirestore myDB;
    private String type;
    private CustomerUser_Data user_data;
    private ServiceProvider_Data serviceProviderData;
    private EditText address1,address2,pincode,state,locality,mobile;
    private TextView name,service;
    private Button Update;
    private ProgressBar progressBar;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit__profile);
        toolbar = findViewById(R.id.activity_edit_toolbar);
        myDB = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout = findViewById(R.id.activity_edit_linearlayout);
        address1 = findViewById(R.id.activity_edit_address1);
        name = findViewById(R.id.activity_edit_name);
        service = findViewById(R.id.activity_edit_service);
        coordinatorLayout = findViewById(R.id.activity_edit_coordinatorlayout);
        address2 = findViewById(R.id.activity_edit_address2);
        pincode = findViewById(R.id.activity_edit_pincode);
        locality = findViewById(R.id.activity_edit_locality);
        progressBar = findViewById(R.id.activity_edit_progressbar);
        mobile = findViewById(R.id.activity_edit_mobile);
        state = findViewById(R.id.activity_edit_state);
        Update = findViewById(R.id.activity_edit_update);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        if(type.equals("admin") || type.equals("user") || type.equals("guest")|| type.equals("normal")) {
            user_data = intent.getParcelableExtra("user_data");
            Log.d(TAG,"Edit : User Data"+user_data.getName()+" "+user_data.getEmail());
            UPDATEUICUSTOMER();
        } else if (type.equals("service")){
            serviceProviderData  = intent.getParcelableExtra("user_data");
            service.setVisibility(View.VISIBLE);
            Log.d(TAG,"Edit : Service Provider Data"+serviceProviderData.getName()+" "+serviceProviderData.getEmail());
            UPDATEUISERVICEPROVIDER();
        }
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTask myTask = new MyTask();
                myTask.execute();
            }
        });


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void UPDATEUICUSTOMER() {
        name.setText(user_data.getName());
        mobile.setText(user_data.getMobile());
        address1.setText(user_data.getAddress1());
        address2.setText(user_data.getAddress2());
        pincode.setText(user_data.getPincode());
        locality.setText(user_data.getLocality());
        state.setText(user_data.getState());

    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void UPDATEUISERVICEPROVIDER() {
        name.setText(serviceProviderData.getName());
        mobile.setText(serviceProviderData.getMobile());
        address1.setText(serviceProviderData.getAddress1());
        address2.setText(serviceProviderData.getAddress2());
        pincode.setText(serviceProviderData.getPincode());
        locality.setText(serviceProviderData.getLocality());
        state.setText(serviceProviderData.getState());
        service.setText(serviceProviderData.getService());
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public  class MyTask extends AsyncTask<String,Void,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Boolean doInBackground(String... params) {
//
            if(type.equals("service"))
            {
                Map<String,Object> map =new HashMap<>();
                map.put("mobile",mobile.getText().toString());
                //map.put("service_type",spinner.getSelectedItem().toString());
                map.put("address1",address1.getText().toString().trim());
                map.put("address2",address2.getText().toString().trim());
                map.put("locality",locality.getText().toString().trim());
                map.put("state",state.getText().toString().trim());
                map.put("pincode",pincode.getText().toString().trim());

                myDB.collection("service_providers").document(serviceProviderData.getEmail()).update(map).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                unsetProgressBar();
                                Snackbar.make(coordinatorLayout,"Updated", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        unsetProgressBar();
                        Snackbar.make(coordinatorLayout,"Failed", Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG,"Failed Updattion" + e.toString());
                    }
                });
            }
            else
            {
                Map<String,Object> map =new HashMap<>();
                map.put("mobile",mobile.getText().toString());
                //map.put("service_type",spinner.getSelectedItem().toString());
                map.put("address1",address1.getText().toString().trim());
                map.put("address2",address2.getText().toString().trim());
                map.put("locality",locality.getText().toString().trim());
                map.put("state",state.getText().toString().trim());
                map.put("pincode",pincode.getText().toString().trim());
                setProgressBar();
                myDB.collection("users").document(user_data.getEmail()).update(map).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                unsetProgressBar();
                                Snackbar.make(coordinatorLayout,"Updated", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        unsetProgressBar();
                        Snackbar.make(coordinatorLayout,"Failed", Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG,"Failed Updattion" + e.toString());
                    }
                });
            }
        return true;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void setProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
        layout.setFocusableInTouchMode(false);
    }

    void unsetProgressBar()
    {
        progressBar.setVisibility(View.GONE);
        layout.setFocusableInTouchMode(true);
    }

}
