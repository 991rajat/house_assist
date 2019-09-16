package android.example.house_assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.house_assist.Activity_Chat;
import android.example.house_assist.Models.ChatUsers;
import android.example.house_assist.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_Chat_Users extends  RecyclerView.Adapter<Adapter_Chat_Users.chat_users>{


    Context context;
    ArrayList<ChatUsers> arrayList;
    public Adapter_Chat_Users(Context context, ArrayList<ChatUsers> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public chat_users onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_chat_users,parent,false);
        chat_users chat_users = new chat_users(view);
        return  chat_users;
    }

    @Override
    public void onBindViewHolder(@NonNull chat_users holder, int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Activity_Chat.class);
                SharedPreferences sp = context.getSharedPreferences("UID",Context.MODE_PRIVATE);
                intent.putExtra("from",sp.getString("name","NA"));
                intent.putExtra("to",arrayList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class chat_users extends RecyclerView.ViewHolder{

        private TextView name;
        private LinearLayout layout;
        public chat_users(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_user_name_row);
            layout = itemView.findViewById(R.id.chat_user_layout_row);
        }
    }
}
