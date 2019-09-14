package android.example.house_assist.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.house_assist.Activity_Service_Booked;
import android.example.house_assist.Models.CustomerUser_Data;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.example.house_assist.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceProviderListAdpter extends RecyclerView.Adapter<ServiceProviderListAdpter.MyViewHolder>{

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    ArrayList<ServiceProvider_Data> list;
    Context context;
    public ServiceProviderListAdpter(ArrayList<ServiceProvider_Data> list, Context context)
    {
        this.list = list;
        this.context = context;

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_provider_list_row, parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.name.setText(list.get(position).getName());
            holder.service.setText("Service : "+list.get(position).getService());
            holder.distance.setText(list.get(position).getDistance()+"Kms");
            holder.rating.setText(list.get(position).getRating().toString());
            SharedPreferences sp = context.getSharedPreferences("UID",Context.MODE_PRIVATE);
            if(sp.getString("flag","NA").equals("user")||sp.getString("flag","NA").equals("admin")||sp.getString("flag","NA").equals("guest")){
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Activity_Service_Booked.class);
                    intent.putExtra("service",list.get(position));
                    context.startActivity(intent);
                }
            });}
            else{
                Toast.makeText(context,"Service Provider Can't See.",Toast.LENGTH_LONG).show();
            }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public int getItemCount() {
        return list.size();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name,service,distance;
        private LinearLayout layout;
        private Button rating;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.service_provider_list_row_name);
            service = itemView.findViewById(R.id.service_provider_list_row_service);
            distance = itemView.findViewById(R.id.service_provider_list_row_distance);
            rating = itemView.findViewById(R.id.service_provider_list_row_rating);
            layout = itemView.findViewById(R.id.service_provider_list_row_layout);
        }
    }
}
