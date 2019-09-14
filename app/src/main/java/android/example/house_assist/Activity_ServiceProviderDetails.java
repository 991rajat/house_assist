package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_ServiceProviderDetails extends AppCompatActivity {

    private EditText address1,address2,pincode,state,locality;
    private Spinner spinner;
    private TextView textView;
    private ProgressBar progressBar;
    private static final String TAG="MYTAG";
    private String email;
    private Button btn;
    private ProgressDialog progressDialog;
    private EditText price;
    private FirebaseFirestore myDB;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__service_provider_details);
        //Intialize
        address1 = findViewById(R.id.service_provider_address1);
        address2 = findViewById(R.id.service_provider_address2);
        pincode = findViewById(R.id.service_provider_pincode);
        locality = findViewById(R.id.service_provider_locality);
        state = findViewById(R.id.service_provider_state);
        price  = findViewById(R.id.service_provider_chargingfee);
        btn  = findViewById(R.id.service_provider_submitdetails);
        textView = findViewById(R.id.service_provider_name);
        progressDialog = new ProgressDialog(Activity_ServiceProviderDetails.this);
        progressBar = findViewById(R.id.service_providerdetails_progressbar);
        spinner = (Spinner) findViewById(R.id.serviceprovider_servicespinner);
        List<String> list = new ArrayList<String>();
        list.add("Electrician");
        list.add("Plumber");
        list.add("Carpenter");
        list.add("Massage");
        list.add("Cleaning");
        list.add("Painting");
        list.add("Appliances");
        list.add("Salon");
        list.add("PackersandMovers");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        textView.setText("Hello "+getIntent().getStringExtra("name"));
        spinner.setAdapter(dataAdapter);
        myDB = FirebaseFirestore.getInstance();
        email = getIntent().getStringExtra("email");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    progressBarSet();
                Map<String,Object> map = new HashMap<>();
                map.put("service_type",spinner.getSelectedItem().toString());
                map.put("address1",address1.getText().toString().trim());
                map.put("address2",address2.getText().toString().trim());
                map.put("locality",locality.getText().toString().trim());
                map.put("state",state.getText().toString().trim());
                map.put("pincode",pincode.getText().toString().trim());
                map.put("price",price.getText().toString().trim());
                map.put("fulladdress","NA");
                myDB.collection("service_providers").document(email).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBarUnset();
                        Toast.makeText(Activity_ServiceProviderDetails.this, "Welcome",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_ServiceProviderDetails.this,MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarSet();
                        Log.d(TAG,"Failure Details"+e.toString());
                        Toast.makeText(Activity_ServiceProviderDetails.this, "Unable to Update", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        });



    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private boolean validate()
    {
        boolean istrue = true;
        if(address1.getText().toString().equals(""))
        {
            address1.setError("Please enter address1.");
            address1.requestFocus();
            istrue = false;
        }

        if(address2.getText().toString().equals(""))
        {
            address2.setError("Please enter address2.");
            address2.requestFocus();
            istrue = false;
        }
        if(pincode.getText().toString().equals(""))
        {
            pincode.setError("Please enter pincode.");
            pincode.requestFocus();
            istrue = false;
        }
        if(state.getText().toString().equals(""))
        {
            state.setError("Please enter state.");
            state.requestFocus();
            istrue = false;
        }
        if(locality.getText().toString().equals(""))
        {
            locality.setError("Please enter locality.");
            locality.requestFocus();
            istrue = false;
        }
        return istrue;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void progressBarSet()
    {
        progressDialog.setMessage("Registering In!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void progressBarUnset()
    {
        progressDialog.dismiss();
    }
}
