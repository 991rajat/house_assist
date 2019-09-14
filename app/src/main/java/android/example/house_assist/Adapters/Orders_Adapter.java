package android.example.house_assist.Adapters;

import android.content.Context;
import android.example.house_assist.Models.Orders;
import android.example.house_assist.Models.ServiceProvider_Data;
import android.example.house_assist.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Orders_Adapter extends RecyclerView.Adapter<Orders_Adapter.My_Orders_View_Holder>  {

    ArrayList<Orders> list;
    Context context;
    public Orders_Adapter(ArrayList<Orders> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public My_Orders_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_row, parent,false);
        My_Orders_View_Holder my_orders_view_holder = new My_Orders_View_Holder(view);
        return my_orders_view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull My_Orders_View_Holder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.details.setText(list.get(position).getMobile());
        holder.date.setText(list.get(position).getDate());
        holder.service.setText(list.get(position).getType());
        holder.price.setText(list.get(position).getPrice());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class My_Orders_View_Holder extends RecyclerView.ViewHolder{

        TextView name,details,service,price,date;
        public My_Orders_View_Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.order_current_row_name);
            details = itemView.findViewById(R.id.order_current_row_details);
            service = itemView.findViewById(R.id.order_current_row_service);
            price = itemView.findViewById(R.id.order_current_row_price);
            date = itemView.findViewById(R.id.order_current_row_date);
        }
    }
}
