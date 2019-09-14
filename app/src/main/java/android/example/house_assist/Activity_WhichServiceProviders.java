package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.example.house_assist.Adapters.Admin_Recylcer_View_Adapter;
import android.example.house_assist.Adapters.ServiceProviderListAdpter;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;

public class Activity_WhichServiceProviders extends AppCompatActivity {

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private String type;
    private double userLat,userLng;
    private static String TAG="TAG";
    private Point originLocation;
    private Point destinantionLocation;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private FirebaseFirestore myDB;
    private ArrayList<ServiceProvider_Data> list;
    private RecyclerView recyclerView;
    private MaterialSearchView searchView;
    private CustomerUser_Data user_data;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__which_service_providers);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Intialize
        myDB = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.activity_whichserviceprovider_progressbar);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.activity_whichserviceprovider_recyclerview);
        toolbar = findViewById(R.id.activity_whichserviceprovider_toolbar);
        searchView = findViewById(R.id.activity_activity_whichserviceprovider_tooba_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

        //DETAILS
        type = getIntent().getStringExtra("type");
        userLat = getIntent().getDoubleExtra("userLat",-1);
        userLng = getIntent().getDoubleExtra("userLng",-1);
        getSupportActionBar().setTitle(type);
        originLocation = Point.fromLngLat(userLng,userLat);
        Log.d(TAG,type+" Lat"+userLat+" Lng"+userLng);
        DownloadServiceProvidersRequest downloadServiceProvidersRequest = new DownloadServiceProvidersRequest();
        downloadServiceProvidersRequest.execute(type);
        //Search View Events
        searchView.closeSearch();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.allusers_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search_all);
        searchView.setMenuItem(item);

        return true;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public class DownloadServiceProvidersRequest extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(String... strings) {
            myDB.collection("service_providers").whereEqualTo("service_type",strings[0]).whereEqualTo("booked","no").whereEqualTo("verification","ok")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                    {

                        Double distance=0.0;
                        Log.d(TAG,"YO");
                        destinantionLocation = Point.fromLngLat(Double.parseDouble(queryDocumentSnapshot.getData().get("lng").toString()),Double.parseDouble(queryDocumentSnapshot.getData().get("lat").toString()));
                        distance = TurfMeasurement.distance(originLocation,destinantionLocation);
                        distance = (double) Math.round(distance * 100) / 100;
                        Log.d(TAG,String.valueOf(distance));
                        if(distance<=30)
                        {
                            ServiceProvider_Data data = new ServiceProvider_Data();
                            data.setName(queryDocumentSnapshot.getData().get("name").toString());
                            data.setEmail(queryDocumentSnapshot.getData().get("email").toString());
                            data.setMobile(queryDocumentSnapshot.getData().get("mobile").toString());
                            data.setService(queryDocumentSnapshot.getData().get("service_type").toString());
                            data.setAddress1(queryDocumentSnapshot.getData().get("address1").toString());
                            data.setAddress2(queryDocumentSnapshot.getData().get("address2").toString());
                            data.setLocality(queryDocumentSnapshot.getData().get("locality").toString());
                            data.setPincode(queryDocumentSnapshot.getData().get("pincode").toString());
                            data.setState(queryDocumentSnapshot.getData().get("state").toString());
                            data.setLatitude(queryDocumentSnapshot.getData().get("lat").toString());
                            data.setLongitude(queryDocumentSnapshot.getData().get("lng").toString());
                            data.setRating(queryDocumentSnapshot.getData().get("rating").toString());
                            data.setFull_Address(queryDocumentSnapshot.getData().get("fulladdress").toString());
                            data.setPrice(queryDocumentSnapshot.getData().get("price").toString());
                            data.setNo_Of_Orders(queryDocumentSnapshot.getData().get("no_of_orders").toString());
                            data.setDistance(String.valueOf(distance));
                            list.add(data);
                        }

                    }
                    Log.d(TAG,"Data fetched");
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Activity_WhichServiceProviders.this));
                    ServiceProviderListAdpter serviceProviderListAdpter = new ServiceProviderListAdpter(list,Activity_WhichServiceProviders.this);
                    recyclerView.setAdapter(serviceProviderListAdpter);
                    unsetProgressBar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

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
