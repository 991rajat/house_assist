package android.example.house_assist;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.house_assist.Adapters.Adapter_Chat_Users;
import android.example.house_assist.Adapters.Fragment_Orders_Adapter;
import android.example.house_assist.Models.ChatUsers;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_Message extends Fragment {


    private Bundle bundle;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layout;
    private Context context;
    private FirebaseFirestore myDB;
    private ArrayList<ChatUsers> arrayList;
    private String UID,type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = inflater.getContext();
        return inflater.inflate(R.layout.fragment_fragment__message, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_message_recyclerview);
        layout = view.findViewById(R.id.fragment_message_nodata);
        progressBar = view.findViewById(R.id.fragment_message_progress_bar);
        myDB = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences("UID",Context.MODE_PRIVATE);
        UID = preferences.getString("name","NA");
        Chats chats = new Chats();
        chats.execute(UID);
    }

    public class Chats extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected Void doInBackground(String... strings) {
            myDB.collection("chat").document(strings[0]).collection("chat_with").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for (DocumentSnapshot snapshot : task.getResult())
                        {
                            ChatUsers chatUsers = new ChatUsers();
                            chatUsers.setName(snapshot.getData().get("name").toString());
                            Log.d("TAG",chatUsers.getName());
                            arrayList.add(chatUsers);
                        }
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        Adapter_Chat_Users chat_users = new Adapter_Chat_Users(context,arrayList);
                        recyclerView.setAdapter(chat_users);
                        if(arrayList.size()==0)
                        {
                            layout.setVisibility(View.VISIBLE);
                        }
                        unsetProgressBar();
                    }
                }
            });
            return null;
        }
    }
    void setProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    void unsetProgressBar()
    {
        progressBar.setVisibility(View.GONE);
    }
}
