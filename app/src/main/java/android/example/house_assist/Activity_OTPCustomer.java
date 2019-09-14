package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Activity_OTPCustomer extends AppCompatActivity {

    private EditText otpCustomer;
    private ProgressBar progressBar;
    private TextView otpCustomer_mobile,otpcustomer_resend;
    private FirebaseAuth mAuth;
    private boolean mtoken = false;
    private Button otpSubmit;
    private ProgressDialog progressDialog;
    private String name,mobile,password,email;
    private FirebaseFirestore myDB;
    private static String TAG = "TAG";
    private String codeSent;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__otpcustomer);
        //Getting Data From Intent from Register Activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");
        progressDialog = new ProgressDialog(Activity_OTPCustomer.this);
        Log.d(TAG,name+" "+mobile+" "+email+" "+password);

        //initialize
        otpCustomer = findViewById(R.id.otpcustomer);
        otpSubmit = findViewById(R.id.otpcustomer_submit);
        otpCustomer_mobile = findViewById(R.id.otpcustomer_mobile);
        otpcustomer_resend = findViewById(R.id.otpcustomer_resend);
        mAuth = FirebaseAuth.getInstance();
        myDB = FirebaseFirestore.getInstance();
        //Running Phone Authentication OTP
        otpCustomer_mobile.setText("+91"+mobile);
        progressBar = findViewById(R.id.spin_kit);


        otpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otpCustomer.getText().toString().equals(""))
                {   otpCustomer.requestFocus();
                    otpCustomer.setError("Enter OTP.");

                }else
                {
                    if(mtoken) {
                        if(codeSent.equals(otpCustomer.getText().toString().trim())) {
                            signInWithEmailAndPassword();
                        }
                        else {
                            Toast.makeText(Activity_OTPCustomer.this, "OTP entered not valid.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        // Check If code not get.
                    }
                }
            }
        });

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        otpcustomer_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSet();
                sendVerificationCode("+91"+mobile);
            }
        });

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithEmailAndPassword();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBarUnset();
                Toast.makeText(Activity_OTPCustomer.this,"Authnetication Failed",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeSent = s;
                mtoken = true;
            }
        };
        if(intent.getStringExtra("type").equals("OTP"))
        {
            progressBarSet();
            sendVerificationCode("+91"+mobile);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void sendVerificationCode(final String phoneNumber) {
        Log.d(TAG,"send");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Activity_OTPCustomer.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void signInWithEmailAndPassword()
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            SharedPreferences sharedPreferences = getSharedPreferences("UID", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString("email",email).apply();
                            sharedPreferences.edit().putString("flag","user").apply();
                            sharedPreferences.edit().putString("location","not").apply();
                            Log.d(TAG, "createUserWithEmail:success");
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("mobile",mobile);
                            user.put("address1","NA");
                            user.put("address2","NA");
                            user.put("state","NA");
                            user.put("pincode","NA");
                            user.put("locality","NA");
                            user.put("fulladdress","NA");
                            user.put("lat",0.0);
                            user.put("lng",0.0);
                            myDB.collection("users").document(email).set(user).
                                 addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         progressBarUnset();
                                         Toast.makeText(Activity_OTPCustomer.this, "Welcome",
                                                 Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(Activity_OTPCustomer.this,MainActivity.class);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            progressBarUnset();
                            Toast.makeText(Activity_OTPCustomer.this, "Authentication failed. User Already Exist",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                        Map<String,Object> map = new HashMap<>();
                        map.put("time",0);
                        map.put("date",0);
                        map.put("service_provider_id",0);
                        map.put("name","name");
                        map.put("mobile","000000");
                        map.put("price","000000");
                        map.put("type","NA");
                        myDB.collection("orders").document(email).collection("history").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG,"orders");
                            }
                        });
                    }
                });


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void progressBarSet()
    {
        progressDialog.setMessage("OTP Verification!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void progressBarUnset()
    {
        progressDialog.dismiss();
    }


}
