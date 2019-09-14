package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.house_assist.Adapters.Admin_Recylcer_View_Adapter;
import android.example.house_assist.Adapters.Orders_Adapter;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.Orders;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.example.house_assist.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;

public class Activity_Previous_Orders extends AppCompatActivity {

    private static final String TAG ="TAG" ;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore myDB;
    private ArrayList<Orders> arrayList;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private String type;
    private CustomerUser_Data user_data;
    private ServiceProvider_Data serviceProviderData;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__previous__orders);
        toolbar = findViewById(R.id.activity_previous_orders_toolbar);
        myDB = FirebaseFirestore.getInstance();
        recyclerView  = findViewById(R.id.activity_previous_orders_recyclerview);
        coordinatorLayout = findViewById(R.id.activity_previous_orders_coordinatorlayout);
        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.activity_previous_orders_progressbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Previous Orders");
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if(type.equals("admin") || type.equals("user") || type.equals("guest")|| type.equals("normal")) {
            user_data = intent.getParcelableExtra("user_data");
            Log.d(TAG,"Edit : User Data"+user_data.getName()+" "+user_data.getEmail());
            DownloadOrders downloadOrders = new DownloadOrders();
            downloadOrders.execute(user_data.getEmail());
            //UPDATEUICUSTOMER();
        } else if (type.equals("service")){
            serviceProviderData  = intent.getParcelableExtra("user_data");
            //service.setVisibility(View.VISIBLE);
            Log.d(TAG,"Edit : Service Provider Data"+serviceProviderData.getName()+" "+serviceProviderData.getEmail());
            DownloadOrdersService downloadOrders = new DownloadOrdersService();
            downloadOrders.execute(user_data.getEmail());
            //UPDATEUISERVICEPROVIDER();
        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    public class DownloadOrders extends AsyncTask<String,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(String... strings) {
            myDB.collection("orders").document(strings[0]).collection("history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            Orders orders = new Orders();
                            orders.setDate(documentSnapshot.getData().get("date").toString());
                            orders.setTime(documentSnapshot.getData().get("time").toString());
                            orders.setType(documentSnapshot.getData().get("type").toString());
                            orders.setEmail(documentSnapshot.getData().get("service_provider_id").toString());
                            orders.setPrice(documentSnapshot.getData().get("price").toString());
                            orders.setMobile(documentSnapshot.getData().get("mobile").toString());
                            orders.setName(documentSnapshot.getData().get("name").toString());
                            arrayList.add(orders);

                        }
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Previous_Orders.this));
                        Orders_Adapter orders_adapter = new Orders_Adapter(arrayList,Activity_Previous_Orders.this);
                        recyclerView.setAdapter(orders_adapter);
                        unsetProgressBar();
                    }
                }
            });

            return null;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public class DownloadOrdersService extends AsyncTask<String,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(String... strings) {
            myDB.collection("orders").document(strings[0]).collection("history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            Orders orders = new Orders();
                            orders.setDate(documentSnapshot.getData().get("date").toString());
                            orders.setTime(documentSnapshot.getData().get("time").toString());
                            orders.setType(documentSnapshot.getData().get("type").toString());
                            orders.setEmail(documentSnapshot.getData().get("service_provider_id").toString());
                            orders.setPrice(documentSnapshot.getData().get("price").toString());
                            orders.setMobile(documentSnapshot.getData().get("mobile").toString());
                            orders.setName(documentSnapshot.getData().get("name").toString());
                            arrayList.add(orders);

                        }
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Previous_Orders.this));
                        Orders_Adapter orders_adapter = new Orders_Adapter(arrayList,Activity_Previous_Orders.this);
                        recyclerView.setAdapter(orders_adapter);
                        unsetProgressBar();
                    }
                }
            });

            return null;
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
}
