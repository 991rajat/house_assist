package android.example.house_assist.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.house_assist.Activity_Chat;
import android.example.house_assist.Models.Messages;
import android.example.house_assist.R;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_Messages extends RecyclerView.Adapter<Adapter_Messages.messageholder>{

    Context context;
    ArrayList<Messages> arrayList;
    public Adapter_Messages(Activity_Chat activity_chat, ArrayList<Messages> arrayList) {
        context = activity_chat;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public messageholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_resource_right,parent,false);
            messageholder messageholder = new messageholder(view);
            return messageholder;}
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_resource_left,parent,false);
            messageholder messageholder = new messageholder(view);
            return messageholder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull messageholder holder, int position) {
        holder.text.setText(" "+arrayList.get(position).getMessage()+" ");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class messageholder extends RecyclerView.ViewHolder{

        private LinearLayout layout;
        private TextView text;
        public messageholder(@NonNull View itemView) {
            super(itemView);
            //layout = itemView.findViewById(R.id.chat_layout_row);
            text = itemView.findViewById(R.id.chat_text_row);
        }
    }

    @Override
    public int getItemViewType(int position) {

        SharedPreferences sp = context.getSharedPreferences("UID",Context.MODE_PRIVATE);
        if(arrayList.get(position).getFrom().equals(sp.getString("name","NA")))
        {
                return 0;
        }else
        {
            return 1;
        }
    }
}
