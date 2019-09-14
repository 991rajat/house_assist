package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.example.house_assist.Adapters.Admin_Recylcer_View_Adapter;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Activity_All_ServiceUser extends AppCompatActivity {

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private static final String TAG ="TAG" ;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore myDB;
    private ArrayList<ServiceProvider_Data> arrayList;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__all__service_user);
        toolbar = findViewById(R.id.activity_allusers_toolbar);
        myDB = FirebaseFirestore.getInstance();
        recyclerView  = findViewById(R.id.activity_allusers_recyclerview);
        coordinatorLayout = findViewById(R.id.activity_allusers_coordinatorlayout);
        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.activity_allusers_progressbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Service Providers Requests");
        DownloadServiceProviderUsers downloadServiceProviderUsers = new DownloadServiceProviderUsers();
        downloadServiceProviderUsers.execute();



    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public class DownloadServiceProviderUsers extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            myDB.collection("service_providers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            ServiceProvider_Data data = new ServiceProvider_Data();
                            if(documentSnapshot.getData().get("verification").toString().equals("not_ok")) {
                                data.setName(documentSnapshot.getData().get("name").toString());
                                data.setEmail(documentSnapshot.getData().get("email").toString());
                                data.setMobile(documentSnapshot.getData().get("mobile").toString());
                                data.setService(documentSnapshot.getData().get("service_type").toString());
                                data.setAddress1(documentSnapshot.getData().get("address1").toString());
                                data.setAddress2(documentSnapshot.getData().get("address2").toString());
                                data.setLocality(documentSnapshot.getData().get("locality").toString());
                                data.setPincode(documentSnapshot.getData().get("pincode").toString());
                                data.setState(documentSnapshot.getData().get("state").toString());
                                data.setPrice(documentSnapshot.getData().get("price").toString());
                                arrayList.add(data);
                            }
                        }
                        Log.d(TAG, "All users data fetched");
                        Log.d(TAG,String.valueOf(arrayList.size()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_All_ServiceUser.this));
                        Admin_Recylcer_View_Adapter admin_recylcer_view_adapter = new Admin_Recylcer_View_Adapter(arrayList,Activity_All_ServiceUser.this);
                        recyclerView.setAdapter(admin_recylcer_view_adapter);
                        unsetProgressBar();
                        if(arrayList.size()==0)
                        {
                            Snackbar.make(coordinatorLayout,"No New Users", Snackbar.LENGTH_INDEFINITE).show();

                        }
                    } else {
                        Log.d(TAG, "No Data Found");
                    }

                }

            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void setProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    void unsetProgressBar()
    {
        progressBar.setVisibility(View.GONE);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public void UpdateDB(final String verification, final String email)
    {
        Log.d(TAG,email);
        myDB = FirebaseFirestore.getInstance();
        if(verification.equals("ok"))
        {

            myDB.collection("service_providers").document(email).update("verification","ok").
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Activity_All_ServiceUser.this,"Request Accepted",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,email+" "+verification+" "+e.toString());
                }
            });
        }else if(verification.equals("not_ok"))
        {
                myDB.collection("service_providers").document(email).delete().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Activity_All_ServiceUser.this,"Request Rejected",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,email+" "+verification+" "+e.toString());
                    }
                });
        }
    }
}


