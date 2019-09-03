package android.example.house_assist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Checking If there is no current user logged in send it to Start Activity
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivity(new Intent(MainActivity.this,Activity_Start.class));
        }
    }
}
