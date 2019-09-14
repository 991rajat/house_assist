package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Service_Booked extends AppCompatActivity {
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private ServiceProvider_Data serviceProviderData;
    private CustomerUser_Data customerUser_data;
    private TextView name,service,price,service_type,distance;
    private Button submit;
    private TextView rating;
    CircleImageView imageView;
    private ProgressDialog progressDialog;
    private CoordinatorLayout layout;
    private Toolbar toolbar;
    private String UID;
    private String TAG="TAG";
    private FirebaseFirestore myDB;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__service__booked);
        name = findViewById(R.id.activity_service_booked_name);
        service = findViewById(R.id.activity_service_booked_service);
        price = findViewById(R.id.activity_service_booked_money);
        service_type = findViewById(R.id.activity_service_booked_service_type);
        rating = findViewById(R.id.activity_service_booked_rating);
        submit = findViewById(R.id.activity_service_booked_submit);
        distance = findViewById(R.id.activity_service_booked_distance);
        toolbar  = findViewById(R.id.activity_service_booked_toolbar);
        layout = findViewById(R.id.activity_service_booked_layout);
        progressDialog = new ProgressDialog(Activity_Service_Booked.this);
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        SharedPreferences sp = getSharedPreferences("UID", Context.MODE_PRIVATE);
        UID = sp.getString("email","NA");
        Gson gson = new Gson();
        String json = sp.getString("user","NA");
        customerUser_data    = gson.fromJson(json,CustomerUser_Data.class);
        serviceProviderData = getIntent().getParcelableExtra("service");
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service Provider");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = FirebaseFirestore.getInstance();
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        UPDATEUI();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"hhhhhhhhhhhhhh");
                Updatetask updatetask = new Updatetask();
                updatetask.execute(serviceProviderData.getEmail());

            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void UPDATEUI() {
        name.setText(serviceProviderData.getName());
        service.setText(serviceProviderData.getService());
        service_type.setText(serviceProviderData.getService());
        distance.setText(serviceProviderData.getDistance()+"Km");
        rating.setText(serviceProviderData.getRating());
        price.setText("Rs"+serviceProviderData.getPrice().toString());
        //Add rating,price,distance
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public class Updatetask extends AsyncTask<String,Void,Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSet();
        }
        @Override
        protected Void doInBackground(String... strings) {
            Calendar c = Calendar.getInstance();
            String time = String.valueOf(c.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate= dateFormat.format(c.getTime());

            //Service BOOKED
            myDB.collection("service_providers").document(strings[0]).update("booked", UID)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            Toast.makeText(Activity_Service_Booked.this,"Service Booked",Toast.LENGTH_LONG).show();
                            Log.d(TAG,"BOOKED");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Activity_Service_Booked.this,"Failed Booked",Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Failed"+e.toString());
                }
            });

            //Update ServiceMen Orders
            Map<String,Object> map1 = new HashMap<>();
            map1.put("customer_id",UID);
            map1.put("time",time);
            map1.put("date",formattedDate);
            map1.put("type",serviceProviderData.getService());
            map1.put("mobile",serviceProviderData.getEmail());
            map1.put("price",serviceProviderData.getPrice());
            map1.put("name",serviceProviderData.getName());
            myDB.collection("orders").document(strings[0]).collection("history").add(map1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG,"order placed");
                }
            });
            //Update Customer Order
            Map<String,Object> map2 = new HashMap<>();
            map2.put("service_provider_id",strings[0]);
            map2.put("time",time);
            map2.put("date",formattedDate);
            map2.put("type",serviceProviderData.getService());
            map2.put("mobile",customerUser_data.getMobile());
            map2.put("price",serviceProviderData.getPrice());
            map2.put("name",customerUser_data.getName());
            myDB.collection("orders").document(UID).collection("history").add(map2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG,"order placed");
                }
            });


            progressBarUnset();

            return null;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void progressBarSet()
    {
        progressDialog.setTitle("Booking Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void progressBarUnset()
    {
        progressDialog.dismiss();
    }
}
