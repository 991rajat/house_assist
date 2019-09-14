package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Activity_LogInCustomer extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private TextView new_User;
    private EditText login_customer_email,login_customer_password;
    private Button customer_login;
    private ProgressBar progressBar;
    private TextView forgetpass;
    private FirebaseAuth mAuth;
    private static final String TAG = "LOGIN";
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__log_in_customer);
        //Intialize
        new_User = findViewById(R.id.login_customer_register);
        toolbar = findViewById(R.id.activity_login_toolbar);
        login_customer_email = findViewById(R.id.login_customer_email);
        login_customer_password = findViewById(R.id.login_customer_password);
        customer_login = findViewById(R.id.login_customer_login);
        forgetpass = findViewById(R.id.login_customer_forgot_password);
        progressBar = findViewById(R.id.spin_kitlogin);
        progressDialog = new ProgressDialog(Activity_LogInCustomer.this);
        mAuth = FirebaseAuth.getInstance();
        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Events
        new_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_LogInCustomer.this,Activity_RegisterCustomer.class));

            }
        });

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        customer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    progressBarSet();
                    signInWithEmailAndPassword(login_customer_email.getText().toString().trim(),login_customer_password.getText().toString().trim());
                }
            }
        });

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!login_customer_email.equals(""))
                {
                    mAuth.sendPasswordResetEmail(login_customer_email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Activity_LogInCustomer.this,"Password Resend Link send to email.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    login_customer_email.setError("Enter Email");
                }
            }
        });

    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void signInWithEmailAndPassword(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            progressBarUnset();
                            SharedPreferences sharedPreferences = getSharedPreferences("UID", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString("email",email).apply();
                            if(email.equals("admin@admin.com"))
                                sharedPreferences.edit().putString("flag","admin").apply();
                            else
                                sharedPreferences.edit().putString("flag","user").apply();
                            sharedPreferences.edit().putString("location","ok").apply();
                            //CHANGE OT
                            Toast.makeText(Activity_LogInCustomer.this, "Welcome.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Activity_LogInCustomer.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBarUnset();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Activity_LogInCustomer.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private boolean validate()
    {
        boolean istrue = true;
        if(login_customer_email.getText().toString().equals(""))
        {
            istrue = false;
            login_customer_email.setError("Enter email.");
            login_customer_email.requestFocus();
        }
        if(login_customer_email.getText().toString().matches("^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$"))
        {
            login_customer_email.setError("Please enter valid email.");
            login_customer_email.requestFocus();
            istrue = false;
        }
        if(login_customer_password.getText().toString().equals(""))
        {
            istrue = false;
            login_customer_password.setError("Enter password.");
            login_customer_password.requestFocus();
        }
        if(login_customer_password.getText().toString().length()<5)
        {
            istrue = false;
            login_customer_password.setError("Enter valid password(minimum 6 alphanumeric).");
            login_customer_password.requestFocus();
        }
        return istrue;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void progressBarSet()
    {
        progressDialog.setMessage("Signing In");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void progressBarUnset()
    {
        progressDialog.dismiss();
    }
}
