package android.example.house_assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_RegisterCustomer extends AppCompatActivity {

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private Toolbar toolbar;
    private TextView customer_Login;
    private EditText customer_name,customer_email,customer_mobile,customer_password;
    private Button customer_SignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register_customer);
        // Intialize
        customer_Login = findViewById(R.id.register_customer_LogIn);
        toolbar = findViewById(R.id.activity_register_toolbar);
        customer_name = findViewById(R.id.register_customer_name);
        customer_mobile = findViewById(R.id.register_customer_mobile);
        customer_email = findViewById(R.id.register_customer_email);
        customer_password = findViewById(R.id.register_customer_password);
        customer_SignUp = findViewById(R.id.register_customer_signUp);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Toolbar Intialize
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Events
        customer_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_RegisterCustomer.this,Activity_LogInCustomer.class));
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        customer_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    Intent intent = new Intent(Activity_RegisterCustomer.this,Activity_OTPCustomer.class);
                    intent.putExtra("name",customer_name.getText().toString().trim());
                    intent.putExtra("mobile",customer_mobile.getText().toString().trim());
                    intent.putExtra("email",customer_email.getText().toString().trim());
                    intent.putExtra("password",customer_password.getText().toString().trim());
                    intent.putExtra("type","OTP");
                    startActivity(intent);

                }

            }
        });
    }





    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    private boolean validate()
    {
        boolean istrue = true;
        if(customer_name.getText().toString().equals(""))
        {
            customer_name.setError("Please enter name.");
            customer_name.requestFocus();
            istrue = false;
        }

        if(customer_mobile.getText().toString().equals(""))
        {
            customer_mobile.setError("Please enter mobile no.");
            customer_mobile.requestFocus();
            istrue = false;
        }
        if(customer_mobile.getText().toString().length()!=10)
        {
            customer_mobile.setError("Please enter valid mobile no.");
            customer_mobile.requestFocus();
            istrue = false;
        }
        if(customer_email.getText().toString().equals(""))
        {
            customer_email.setError("Please enter email.");
            customer_email.requestFocus();
            istrue = false;
        }
        if(customer_email.getText().toString().matches("^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$"))
        {
            customer_email.setError("Please enter valid email.");
            customer_email.requestFocus();
            istrue = false;
        }
        if(customer_password.getText().toString().equals(""))
        {
            customer_password.setError("Please enter password.");
            customer_password.requestFocus();
            istrue = false;
        }
        if(customer_password.getText().toString().length()<5)
        {
            customer_password.setError("Please enter valid password(minimum 6 alphanumeric).");
            customer_password.requestFocus();
            istrue = false;
        }
        return istrue;
    }
}
