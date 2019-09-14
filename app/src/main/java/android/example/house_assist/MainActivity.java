package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView nav_header_name,nav_header_email;
    private RatingBar ratingBar;
    private Button rating_Button,feedbackButton;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private View navHeader;
    private Bundle bundle;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Button signOut;
    private BottomNavigationView bottomNavigationView;
    private BottomSheetDialog bottomSheetDialogRating,bottomSheetDialogFeedback;
    private FirebaseFirestore myDB;
    private static String TAG = "TAG";
    private String UID,Type;
    private Double userLat,userLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Intialize
        toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = FirebaseFirestore.getInstance();
        bundle = new Bundle();
        progressDialog = new ProgressDialog(MainActivity.this);

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

        //Getting Previous Saved User
        SharedPreferences sp = getSharedPreferences("UID", Context.MODE_PRIVATE);
        UID = sp.getString("email","NA");
        Type = sp.getString("flag","NA");


        Log.d(TAG,UID+" "+Type);

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Getting details of USERS on basis of sharedprefrences
        if(Type.equals("user"))
        {
            Log.d(TAG,"I'm Customer");
            bundle.putString("type","user");
            DownloadDetailsCustomer downloadDetails = new DownloadDetailsCustomer();
            downloadDetails.execute(UID);
        }
        else if(Type.equals("service"))
        {
            Log.d(TAG,"I'm Service Provider");
            bundle.putString("type","service");
            DownloadDetailsServiceProvider downloadDetails = new DownloadDetailsServiceProvider();
            downloadDetails.execute(UID);
        }
        else if(Type.equals("guest"))
        {
            Log.d(TAG,"I'm Guest");
            bundle.putString("type","guest");
            DownloadDetailsCustomer downloadDetails = new DownloadDetailsCustomer();
            downloadDetails.execute(UID);
        }
        else if(Type.equals("admin"))
        {

            Log.d(TAG,"I'm Admin");
            DownloadDetailsCustomer admin = new DownloadDetailsCustomer();
            admin.execute(UID);
            bundle.putString("type","admin");
        }

 //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.activit_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment=null;
                switch (menuItem.getItemId())
                {
                    case R.id.main_activity_home:
                        fragment = new Fragment_main();
                        getSupportActionBar().setTitle("Home");
                        bundle.putDouble("userLat",userLat);
                        bundle.putDouble("userLng",userLng);
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                        break;
                    case R.id.main_activity_orders:
                        fragment = new Fragment_Orders();
                        getSupportActionBar().setTitle("Orders");
                        loadFragment(fragment);
                        break;
                    case R.id.main_activity_message:
                        fragment = new Fragment_Message();
                        getSupportActionBar().setTitle("Message");
                        loadFragment(fragment);
                        break;
                }
                return true;
            }
        });


//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Naviagtion Drawer

        drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.whiteTextColor));
        drawer.addDrawerListener(actionBarDrawerToggle);
        navHeader = navigationView.getHeaderView(0);
        nav_header_name = navHeader.findViewById(R.id.nav_header_name);
        nav_header_email = navHeader.findViewById(R.id.nav_header_email);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment=null;
                switch (menuItem.getItemId())
                {

                    case R.id.nav_home:
                        fragment = new Fragment_main();
                        bundle.putDouble("userLat",userLat);
                        bundle.putDouble("userLng",userLng);
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                        break;
                    case R.id.nav_orders:
                        fragment = new Fragment_Orders();
                        getSupportActionBar().setTitle("Orders");
                        loadFragment(fragment);
                        break;
                    case R.id.nav_dashboard:
                        fragment = new Fragment_Account(UID,MainActivity.this);
                        fragment.setArguments(bundle);
                        getSupportActionBar().setTitle("Dashboard");
                        loadFragment(fragment);
                        break;
                    case R.id.nav_about_us:
                        bottomSheetDialogRating = new BottomSheetDialog(MainActivity.this);
                        View bottomrate = getLayoutInflater().inflate(R.layout.rateus_bottomsheet,null);
                        bottomSheetDialogRating.setContentView(bottomrate);
                        ratingBar = bottomrate.findViewById(R.id.ratingBar);
                        rating_Button = bottomrate.findViewById(R.id.ratingButton);
                        bottomSheetDialogRating.show();
                        rating_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this,"Thank You"+ratingBar.getRating(),Toast.LENGTH_SHORT).show();
                                bottomSheetDialogRating.hide();
                            }
                        });
                        break;
                    case R.id.nav_privacy_policy:
                        bottomSheetDialogFeedback = new BottomSheetDialog(MainActivity.this);
                        View bottomfeed = getLayoutInflater().inflate(R.layout.bottomsheet_feedback,null);
                        bottomSheetDialogFeedback.setContentView(bottomfeed);
                        feedbackButton = bottomfeed.findViewById(R.id.feedbackButton);
                        bottomSheetDialogFeedback.show();
                        feedbackButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialogFeedback.hide();
                                Toast.makeText(MainActivity.this,"Thank You",Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.nav_logout:
                        logout();break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    //Options MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    //Option SELECTED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.my_location:
                updateLocation();
                break;

        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    //Update Location If not
    private void updateLocation() {
        Intent intent = new Intent(MainActivity.this,Activity_Map.class);
        if(Type.equals("user")||Type.equals("guest")||Type.equals("admin"))
        {
            intent.putExtra("type","normal");
            intent.putExtra("email",UID);
        }else if(Type.equals("service"))
        {
            intent.putExtra("type","service");
            intent.putExtra("email",UID);
        }
        startActivity(intent);
    }


    //LogOut
    private void logout() {

            SharedPreferences sp = getSharedPreferences("UID",Context.MODE_PRIVATE);
            sp.edit().clear().apply();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,Activity_Start.class));
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //Load Fragment
    private void loadFragment(Fragment fragment) {
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,fragment).commit();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //On Start Activity
    @Override
    protected void onStart() {
        super.onStart();
        //Check IF LOCATION IS NOT PRESENT
        SharedPreferences sp = getSharedPreferences("UID", Context.MODE_PRIVATE);
        if(sp.getString("location","NA").equals("not"))
        {
            Toast.makeText(this, "Update The Location First", Toast.LENGTH_SHORT).show();
            sp.edit().putString("location","ok").apply();
            updateLocation();
        }


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    //DETAILS of User/Admin/Guest
    public class DownloadDetailsCustomer extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSet();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            myDB.collection("users").document(params[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        CustomerUser_Data data = new CustomerUser_Data();
                        data.setName(documentSnapshot.getData().get("name").toString());
                        data.setEmail(documentSnapshot.getData().get("email").toString());
                        data.setMobile(documentSnapshot.getData().get("mobile").toString());
                        data.setAddress1(documentSnapshot.getData().get("address1").toString());
                        data.setAddress2(documentSnapshot.getData().get("address2").toString());
                        data.setLocality(documentSnapshot.getData().get("locality").toString());
                        data.setPincode(documentSnapshot.getData().get("pincode").toString());
                        data.setState(documentSnapshot.getData().get("state").toString());
                        data.setFull_Address(documentSnapshot.getData().get("fulladdress").toString());
                        data.setLatitude(documentSnapshot.getData().get("lat").toString());
                        data.setLongitude(documentSnapshot.getData().get("lng").toString());
                        Log.d(TAG,"Data reterived");
                        bundle.putParcelable("customer_data",data);
                        getSupportActionBar().setTitle("Welcome, "+data.getName());
                        nav_header_email.setText(data.getEmail());
                        nav_header_name.setText(data.getName());
                        userLat = Double.parseDouble(data.getLatitude().toString());
                        userLng = Double.parseDouble(data.getLongitude().toString());
                        SharedPreferences sp = getSharedPreferences("UID",Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        sp.edit().putString("user",json).apply();
                        loadHomeFragmentFirstTime();
                        progressBarUnset();
                    }
                    else
                    {
                        progressBarUnset();
                        Log.d(TAG,"Error User Data Not Fetched");
                    }
                }

            });
            return true;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    //DETAILS of Service Providers
    public class DownloadDetailsServiceProvider extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSet();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            myDB.collection("service_providers").document(params[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        ServiceProvider_Data data = new ServiceProvider_Data();
                        data.setName(documentSnapshot.getData().get("name").toString());
                        data.setEmail(documentSnapshot.getData().get("email").toString());
                        data.setMobile(documentSnapshot.getData().get("mobile").toString());
                        data.setService(documentSnapshot.getData().get("service_type").toString());
                        data.setAddress1(documentSnapshot.getData().get("address1").toString());
                        data.setAddress2(documentSnapshot.getData().get("address2").toString());
                        data.setLocality(documentSnapshot.getData().get("locality").toString());
                        data.setPincode(documentSnapshot.getData().get("pincode").toString());
                        data.setState(documentSnapshot.getData().get("state").toString());
                        data.setLatitude(documentSnapshot.getData().get("lat").toString());
                        data.setLongitude(documentSnapshot.getData().get("lng").toString());
                        data.setRating(documentSnapshot.getData().get("rating").toString());
                        data.setFull_Address(documentSnapshot.getData().get("fulladdress").toString());
                        data.setPrice(documentSnapshot.getData().get("price").toString());
                        data.setNo_Of_Orders(documentSnapshot.getData().get("no_of_orders").toString());
                        bundle.putParcelable("service_provider_data",data);
                        Log.d(TAG,"Data reterived");
                        getSupportActionBar().setTitle("Welcome, "+data.getName());
                        nav_header_email.setText(data.getEmail());
                        nav_header_name.setText(data.getName());
                        userLat = Double.parseDouble(data.getLatitude().toString());
                        userLng = Double.parseDouble(data.getLongitude().toString());
                        SharedPreferences sp = getSharedPreferences("UID",Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        sp.edit().putString("service",json).apply();
                        loadHomeFragmentFirstTime();
                        progressBarUnset();
                    }
                    else
                    {
                        Log.d(TAG,"Error User Data Not Fetched");
                        progressBarUnset();
                    }
                }

            });
            return true;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //First Fragment
    private void loadHomeFragmentFirstTime()
    {

        Fragment fragment = new Fragment_main();
        bundle.putDouble("userLat",userLat);
        bundle.putDouble("userLng",userLng);
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    void progressBarSet()
    {
        progressDialog.setMessage("Loading!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void progressBarUnset()
    {
        progressDialog.dismiss();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
}
