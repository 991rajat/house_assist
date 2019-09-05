package android.example.house_assist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button signOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOut = findViewById(R.id.mainactivity_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,Activity_Start.class));
            }
        });

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
