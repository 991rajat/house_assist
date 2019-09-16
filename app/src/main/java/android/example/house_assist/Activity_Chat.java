package android.example.house_assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.example.house_assist.Adapters.Adapter_Messages;
import android.example.house_assist.Adapters.Fragment_Orders_Adapter;
import android.example.house_assist.Models.Messages;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_Chat extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ImageView send;
    private LinearLayoutManager linearLayoutManager;
    private EditText message;
    private FirebaseFirestore myDB;
    private String from,to;
    private ListenerRegistration listenerRegistration;
    private Toolbar toolbar;
    public ArrayList<Messages> arrayList;
    Adapter_Messages adapter_messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chat);
        recyclerView = findViewById(R.id.chat_recyclerview);
        send = findViewById(R.id.chat_send);
        message = findViewById(R.id.chat_edit);
        myDB = FirebaseFirestore.getInstance();
        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        arrayList = new ArrayList<>();
        toolbar = findViewById(R.id.chat_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(to);
        getSupportActionBar().setSubtitle("last seen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter_messages = new Adapter_Messages(Activity_Chat.this,arrayList);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("message",message.getText().toString());
                map.put("timestamp", FieldValue.serverTimestamp());
                map.put("from",from);
                map.put("to",to);
                myDB.collection("chat").document(from).collection(to).add(map);
                myDB.collection("chat").document(to).collection(from).add(map);
                myDB.collection("chat").document(to).collection(from).document("message").set(map);
                myDB.collection("chat").document(from).collection(to).document("message").set(map);
                message.setText("");
            }
        });


        myDB.collection("chat").document(from).collection(to).orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult())
                    {
                        Messages message = new Messages();
                        if(snapshot.getId().toString().equals("message")) {
                            Log.d("TAG",snapshot.getData().get("message").toString());
                            continue;
                        }
                            message.setMessage(snapshot.getData().get("message").toString());
//                            message.setTime(snapshot.getData().get("timestamp").toString());
                            message.setFrom(snapshot.getData().get("from").toString());
                            message.setTo(snapshot.getData().get("to").toString());
                            arrayList.add(message);

                    }
                    recyclerView.setHasFixedSize(true);
                    linearLayoutManager = new LinearLayoutManager(Activity_Chat.this);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapter_messages = new Adapter_Messages(Activity_Chat.this,arrayList);
                    recyclerView.setAdapter(adapter_messages);
                    Log.d("TAG","why2");

                }
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration= myDB.collection("chat").document(from).collection(to).document("message").addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    Messages message = new Messages();
                    message.setMessage(documentSnapshot.getData().get("message").toString());
                    //message.setTime(documentSnapshot.getData().get("timestamp").toString());
                    message.setFrom(documentSnapshot.getData().get("from").toString());
                    message.setTo(documentSnapshot.getData().get("to").toString());
                    Log.d("TAG","why1");
                    if(!arrayList.contains(message))
                        arrayList.add(message);

                    adapter_messages.notifyDataSetChanged();
                }
            }
        });



    }
//


    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
}
