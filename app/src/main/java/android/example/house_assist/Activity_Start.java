package android.example.house_assist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Activity_Start extends AppCompatActivity {
    private Button customer;
    private Button service_Provider,guest;
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__start);
        //Intilization
            customer = findViewById(R.id.start_customer);
            service_Provider = findViewById(R.id.start_service_provider);
            guest = findViewById(R.id.start_guest_user);
        //Onlick Events
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Start.this,Activity_LogInCustomer.class));
            }
        });
        // Onclick event for service provider
        service_Provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Start.this, Activity_LogInServiceProvider.class));
            }
        });
        //Onclick event for guest
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UID", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("email","guest@guest.com").apply();
                sharedPreferences.edit().putString("flag","guest").apply();
                sharedPreferences.edit().putString("location","ok").apply();
                startActivity(new Intent(Activity_Start.this,MainActivity.class));
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(Activity_Start.this,MainActivity.class));
        }
    }
}
