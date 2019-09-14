package android.example.house_assist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Activity_OTPServiceProvider extends AppCompatActivity {

    private EditText otpServiceprovider;
    private ProgressBar progressBar;
    private TextView otpServiceprovider_mobile,otpServiceprovider_resend;
    private FirebaseAuth mAuth;
    private boolean mtoken = false;
    private FirebaseFirestore myDB;
    private Button otpSubmit;
    private ProgressDialog progressDialog;
    private String name,mobile,password,email;
    private static String TAG = "TAG";
    private String codeSent;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__otpservice_provider);
        //Getting Data From Intent from Register Activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");
        Log.d(TAG,name+" "+mobile+" "+email+" "+password);
        progressDialog = new ProgressDialog(Activity_OTPServiceProvider.this);

        //initialize
        otpServiceprovider = findViewById(R.id.otpserviceprovider);
        otpSubmit = findViewById(R.id.otpserviceprovider_submit);
        otpServiceprovider_mobile = findViewById(R.id.otpserviceprovider_mobile);
        otpServiceprovider_resend = findViewById(R.id.otpserviceprovider_resend);
        mAuth = FirebaseAuth.getInstance();
        myDB = FirebaseFirestore.getInstance();
        //Running Phone Authentication OTP
        otpServiceprovider_mobile.setText("+91"+mobile);
        progressBar = findViewById(R.id.service_provider_progressbar);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

        otpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otpServiceprovider.getText().toString().equals(""))
                {   otpServiceprovider.requestFocus();
                    otpServiceprovider.setError("Enter OTP.");

                }else
                {
                    if(mtoken) {
                        if(codeSent.equals(otpServiceprovider.getText().toString().trim())) {
                            signInWithEmailAndPassword();
                        }
                        else {
                            Toast.makeText(Activity_OTPServiceProvider.this, "OTP entered not valid.", Toast.LENGTH_LONG).show();
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
        otpServiceprovider_resend.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(Activity_OTPServiceProvider.this,"Authnetication Failed OTP not Received",Toast.LENGTH_LONG).show();
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
                Activity_OTPServiceProvider.this,               // Activity (for callback binding)
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
                            sharedPreferences.edit().putString("flag","service").apply();
                            sharedPreferences.edit().putString("location","not").apply();
                            Log.d(TAG, "createUserWithEmail:success");
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("mobile",mobile);
                            user.put("service_type","NA");
                            user.put("address1","NA");
                            user.put("address2","NA");
                            user.put("state","NA");
                            user.put("pincode","NA");
                            user.put("locality","NA");
                            user.put("verification","not_ok");
                            user.put("booked","no");
                            user.put("lat",0.0);
                            user.put("lng",0.0);
                            user.put("rating",0.0);
                            user.put("no_of_orders",0);
                            myDB.collection("service_providers").document(email).set(user).
                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBarUnset();
                                            Intent intent = new Intent(Activity_OTPServiceProvider.this,Activity_ServiceProviderDetails.class);
                                            intent.putExtra("email",email);
                                            intent.putExtra("name",name);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Activity_OTPServiceProvider.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBarUnset();
                            startActivity(new Intent(Activity_OTPServiceProvider.this,Activity_ServiceProviderDetails.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressBarUnset();
                            Toast.makeText(Activity_OTPServiceProvider.this, "Authentication failed. User Already Exist",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                        Map<String,Object> map = new HashMap<>();
                        map.put("time",0);
                        map.put("date",0);
                        map.put("customer_id",0);
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
