package android.example.house_assist.Adapters;

import android.content.Context;
import android.example.house_assist.Activity_All_ServiceUser;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.example.house_assist.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Admin_Recylcer_View_Adapter extends RecyclerView.Adapter<Admin_Recylcer_View_Adapter.MyViewHolder> {

    ArrayList<ServiceProvider_Data> list;
    Context context;


    public Admin_Recylcer_View_Adapter(ArrayList<ServiceProvider_Data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Admin_Recylcer_View_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_row_list, parent,false);
        MyViewHolder view_holder = new MyViewHolder(view);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String name = list.get(position).getName();
        final String details = "+91"+list.get(position).getMobile()+"\n"+list.get(position).getEmail();
        final String service = "Service : "+list.get(position).getService();
        holder.name.setText(name);
        holder.details.setText(details);
        holder.service.setText(service);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateItem(list.get(position),"ok");
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateItem(list.get(position),"not_ok");
            }
        });

    }

    //Removed Data from Recycler View
    private void UpdateItem(ServiceProvider_Data serviceProvider_data,String verfication) {
        String deleteemail = serviceProvider_data.getEmail();

        int currentIndex = list.indexOf(serviceProvider_data);
        list.remove(currentIndex);
        notifyItemRemoved(currentIndex);
        notifyItemChanged(currentIndex,list.size());
        Activity_All_ServiceUser activity_all_serviceUser = new Activity_All_ServiceUser();
        activity_all_serviceUser.UpdateDB(verfication,deleteemail);


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,details,service;
        Button accept,reject;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.all_users_row_name);
            details = itemView.findViewById(R.id.all_users_row_details);
            service = itemView.findViewById(R.id.all_users_row_service);
            accept = itemView.findViewById(R.id.all_users_row_accept);
            reject = itemView.findViewById(R.id.all_users_row_reject);

        }
    }
}
