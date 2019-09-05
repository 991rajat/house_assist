package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_OTPCustomer extends AppCompatActivity {

    private EditText otpCustomer;
    private ProgressDialog progressDialog;
    private TextView otpCustomer_mobile,otpcustomer_resend;
    private FirebaseAuth mAuth;
    private boolean mtoken = false;
    private Button otpSubmit;
    private String name,mobile,password,email;
    private static String TAG = "TAG";
    private String codeSent;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
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
        Log.d(TAG,name+" "+mobile+" "+email+" "+password);

        //initialize
        otpCustomer = findViewById(R.id.otpcustomer);
        otpSubmit = findViewById(R.id.otpcustomer_submit);
        otpCustomer_mobile = findViewById(R.id.otpcustomer_mobile);
        otpcustomer_resend = findViewById(R.id.otpcustomer_resend);
        mAuth = FirebaseAuth.getInstance();
        //Running Phone Authentication OTP
        otpCustomer_mobile.setText("+91"+mobile);
        progressDialog = new ProgressDialog(this);


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

        otpcustomer_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCancelable(true);
                progressDialog.setTitle("Hold ryt there sparky");
                progressDialog.setMessage("OTP verification in Progress");
                progressDialog.getWindow().setGravity(Gravity.CENTER);
                progressDialog.show();
                sendVerificationCode("+91"+mobile);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithEmailAndPassword();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
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
            progressDialog.setCancelable(true);
            progressDialog.setTitle("Hold ryt there sparky");
            progressDialog.setMessage("OTP verification in Progress");
            progressDialog.getWindow().setGravity(Gravity.CENTER);
            progressDialog.show();
            sendVerificationCode("+91"+mobile);
        }
    }

    private void sendVerificationCode(final String phoneNumber) {
        Log.d(TAG,"send");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Activity_OTPCustomer.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private void signInWithEmailAndPassword()
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startActivity(new Intent(Activity_OTPCustomer.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(Activity_OTPCustomer.this, "Authentication failed. User Already Exist",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }


}
