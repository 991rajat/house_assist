package android.example.house_assist;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Account extends Fragment {
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private String UID;
    private static String TAG = "TAG";
    private Context contextid;
    private Bundle bundle;
    private ImageView imageView;
    private CustomerUser_Data userData;
    private ServiceProvider_Data serviceProviderData;
    private String type;
    private TextView name,details,myOrders,manageAddress,rateUs,help,signOut,allusers;
    private View alluserline;

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public Fragment_Account(String emails,Context context) {
        UID = emails;
        contextid = context;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = getArguments();
        type = bundle.getString("type");

        if(type.equals("user")) {
            userData = bundle.getParcelable("customer_data");
        }
        else if(type.equals("guest")){
            userData = bundle.getParcelable("customer_data");
        }
        else if(type.equals("service"))
        {
            serviceProviderData = bundle.getParcelable("service_provider_data");
        }else if(type.equals("admin"))
        {
            userData = bundle.getParcelable("customer_data");
        }
        return inflater.inflate(R.layout.fragment_fragment__account, container, false);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.fragment_account_name);
        details = view.findViewById(R.id.fragment_account_details);
        allusers = view.findViewById(R.id.fragment_account_allusers);
        alluserline = view.findViewById(R.id.fragment_account_allusersline);
        signOut = view.findViewById(R.id.fragment_account_signout);
        manageAddress = view.findViewById(R.id.fragment_account_address);
        myOrders = view.findViewById(R.id.fragment_account_myorders);
        imageView = view.findViewById(R.id.fragment_account_edit);

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        if(type.equals("guest")||type.equals("user"))
            UpdateUICustomer();
        else if(type.equals("service"))
            UpdateUIService();
        else if(type.equals("admin")) {
            alluserline.setVisibility(View.VISIBLE);
            allusers.setVisibility(View.VISIBLE);
            UpdateUICustomer();
        }
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut();
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        allusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(contextid,Activity_All_ServiceUser.class));
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        manageAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextid, Activity_Edit_Profile.class);
                if (type.equals("admin") || type.equals("user") || type.equals("guest")) {
                    intent.putExtra("type", "normal");
                    intent.putExtra("user_data", userData);
                } else if (type.equals("service")){
                    intent.putExtra("type", "service");
                    intent.putExtra("user_data",serviceProviderData);
                }
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextid, Activity_Previous_Orders.class);
                if (type.equals("admin") || type.equals("user") || type.equals("guest")) {
                    intent.putExtra("type", "normal");
                    intent.putExtra("user_data", userData);
                } else if (type.equals("service")){
                    intent.putExtra("type", "service");
                    intent.putExtra("user_data",serviceProviderData);
                }
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextid, Activity_Edit_Profile.class);
                if (type.equals("admin") || type.equals("user") || type.equals("guest")) {
                    intent.putExtra("type", "normal");
                    intent.putExtra("user_data", userData);
                } else if (type.equals("service")){
                    intent.putExtra("type", "service");
                    intent.putExtra("user_data",serviceProviderData);
                }
                startActivity(intent);
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void UpdateUIService() {
        Log.d(TAG,"tp");
        name.setText(serviceProviderData.getName());
        details.setText("+91" + serviceProviderData.getMobile() + "\n" + serviceProviderData.getEmail());
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void SignOut() {
        SharedPreferences sp = contextid.getSharedPreferences("UID",Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(contextid,Activity_Start.class));
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private void UpdateUICustomer() {
        //Log.d(TAG,userData.getEmail());
        name.setText(userData.getName());
        details.setText("+91" + userData.getMobile() + "\n" + userData.getEmail());

    }

}
