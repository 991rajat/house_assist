package android.example.house_assist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.example.house_assist.Adapters.Fragment_Orders_Adapter;
import android.example.house_assist.Adapters.ServiceProviderListAdpter;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_Orders extends Fragment {

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private Bundle bundle;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layout;
    private Context context;
    private FirebaseFirestore myDB;
    private ArrayList<ServiceProvider_Data> list;
    private ArrayList<CustomerUser_Data> customerUser_data;
    private String UID,type;
    private CustomerUser_Data user_data;
    private ServiceProvider_Data serviceProviderData;
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public Fragment_Orders() {
        super();
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = getArguments();
        context = inflater.getContext();
        return inflater.inflate(R.layout.fragment_fragment__orders, container, false);
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_orders_recyclerview);
        layout = view.findViewById(R.id.fragment_orders_nodata);
        progressBar = view.findViewById(R.id.fragment_orders_progress_bar);
        myDB = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences("UID",Context.MODE_PRIVATE);
        UID = preferences.getString("email","NA");
        type = preferences.getString("flag","NA");
        //Admin and Customers
        if(type.equals("user")||type.equals("admin")) {
            MyTask myTask = new MyTask();
            myTask.execute();
        }
        //Service Providers
        else if(type.equals("service"))
        {
            ServiceTask serviceTask = new ServiceTask();
            serviceTask.execute();
        }
        //GUEST CAN ONLY WATCH
        else
        {
            layout.setVisibility(View.VISIBLE);
            Toast.makeText(context,"Guest Can't Make orders",Toast.LENGTH_LONG);
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    // Get Current Orders Of Customer
    public class MyTask extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(String... strings) {
            myDB.collection("service_providers").whereEqualTo("booked",UID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(DocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots)
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
                        //data.setPrice(queryDocumentSnapshot.getData().get("price").toString());
                        data.setNo_Of_Orders(queryDocumentSnapshot.getData().get("no_of_orders").toString());
                        list.add(data);
                    }
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    Fragment_Orders_Adapter fragment_orders_adapter = new Fragment_Orders_Adapter(list,context);
                    recyclerView.setAdapter(fragment_orders_adapter);
                    if(list.size()==0)
                    {
                        layout.setVisibility(View.VISIBLE);
                    }
                    unsetProgressBar();
                }
            });
            return null;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //Get Current Service Provider Order
    public class ServiceTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            myDB.collection("service_providers").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(!task.getResult().getData().get("booked").toString().equals("no")) {
                            myDB.collection("users").document(task.getResult().getData().get("booked").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot queryDocumentSnapshot = task1.getResult();

                                        Log.d("TAGh",task1.getResult().getId());
                                        CustomerUser_Data data = new CustomerUser_Data();
                                        data.setName(queryDocumentSnapshot.getData().get("name").toString());
                                        data.setEmail(queryDocumentSnapshot.getData().get("email").toString());
                                        data.setMobile(queryDocumentSnapshot.getData().get("mobile").toString());
                                        data.setAddress1(queryDocumentSnapshot.getData().get("address1").toString());
                                        data.setAddress2(queryDocumentSnapshot.getData().get("address2").toString());
                                        data.setLocality(queryDocumentSnapshot.getData().get("locality").toString());
                                        data.setPincode(queryDocumentSnapshot.getData().get("pincode").toString());
                                        data.setState(queryDocumentSnapshot.getData().get("state").toString());
                                        recyclerView.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                        LayoutInflater inflater = getLayoutInflater();
                                        View dialogView = inflater.inflate(R.layout.alertdialog_custom_view,null);

                                        // Set the custom layout as alert dialog view
                                        builder.setView(dialogView);
                                        TextView name = dialogView.findViewById(R.id.alertdilog_name);
                                        TextView mobile = dialogView.findViewById(R.id.alertdilog_mobile);
                                        TextView address1 = dialogView.findViewById(R.id.alertdilog_address1);
                                        TextView address2 = dialogView.findViewById(R.id.alertdilog_address2);
                                        name.setText("Name "+data.getName());
                                        mobile.setText("Mobile " +data.getMobile());
                                        address1.setText("Address "+data.getAddress1());
                                        address2.setText(data.getAddress2()+" "+data.getPincode());
                                        final AlertDialog dialog = builder.create();
                                        dialog.show();


                                    }
                                }
                            });
                        }else
                        {
                            //NO BOOKINGS
                            layout.setVisibility(View.VISIBLE);
                        }
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
