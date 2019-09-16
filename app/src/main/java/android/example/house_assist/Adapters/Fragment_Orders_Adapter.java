package android.example.house_assist.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.house_assist.MainActivity;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.example.house_assist.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Fragment_Orders_Adapter extends RecyclerView.Adapter<Fragment_Orders_Adapter.Fragment_Orders_Adapter_View_Holder> {


    ArrayList<ServiceProvider_Data> list;
    Context context;
    public Fragment_Orders_Adapter(ArrayList<ServiceProvider_Data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment_Orders_Adapter_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_current_row, parent,false);
        Fragment_Orders_Adapter_View_Holder myViewHolder = new Fragment_Orders_Adapter_View_Holder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Fragment_Orders_Adapter_View_Holder holder, int position) {
        final String name = list.get(position).getName();
        final String details = "+91"+list.get(position).getMobile()+"\n"+list.get(position).getEmail();
        final String service = "Service : "+list.get(position).getService();
        holder.name.setText(name);
        holder.details.setText(details);
        holder.service.setText(service);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialogRating = new BottomSheetDialog(context);
                View bottomrate = LayoutInflater.from(context).inflate(R.layout.rateus_bottomsheet,null);
                bottomSheetDialogRating.setContentView(bottomrate);
                RatingBar ratingBar = bottomrate.findViewById(R.id.ratingBar);
                Button rating_Button = bottomrate.findViewById(R.id.ratingButton);
                bottomSheetDialogRating.show();

                rating_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TAGh",""+ratingBar.getRating());
                        final double no_of_orders = Double.parseDouble(list.get(position).getNo_Of_Orders());
                        double rate = (Double.parseDouble(list.get(position).getRating())*no_of_orders+ratingBar.getRating())/(no_of_orders+1);
                        double finalRate = (double) Math.round(rate * 100) / 100;;
                        double finalNo_of_orders = no_of_orders+1;
                        Log.d("TAGh",""+ratingBar.getRating());
                        //Toast.makeText(context,""+ratingBar.getRating(),Toast.LENGTH_SHORT).show();
                        FirebaseFirestore.getInstance().collection("service_providers").
                                document(list.get(position).getEmail()).update("booked","no","rating",String.valueOf(finalRate),"no_of_orders",String.valueOf(finalNo_of_orders))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemChanged(position,list.size());
                                    Toast.makeText(context,"Thank You",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                                }
                                bottomSheetDialogRating.hide();
                            }
                        });

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Fragment_Orders_Adapter_View_Holder extends RecyclerView.ViewHolder{

        TextView name,details,service;
        Button button;
        public Fragment_Orders_Adapter_View_Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fragment_order_current_row_name);
            details = itemView.findViewById(R.id.fragment_order_current_row_details);
            service = itemView.findViewById(R.id.fragment_order_current_row_service);
            button = itemView.findViewById(R.id.fragment_order_current_row_done);
        }
    }
}
