package com.iridescent07.smart_citizens_feedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadedFeedback extends AppCompatActivity {

    private ListView listView;
    private TextView text;
    private FirebaseAuth firebaseAuth;

    private ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_feedback);

        text = findViewById(R.id.text);
        listView = findViewById(R.id.listView);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("Post Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUser = firebaseAuth.getCurrentUser().getUid();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostDetails postDetails = snapshot.getValue(PostDetails.class);

                    if(postDetails.getUserID().equals(currentUser)) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("caption", postDetails.getCaption());
                        hashMap.put("latitude", postDetails.getLatitude());
                        hashMap.put("longitude", postDetails.getLongitude());
                        hashMap.put("severity", postDetails.getSeverity());
                        hashMap.put("status", postDetails.getDone());
                        hashMap.put("type", postDetails.getType());
                        arrayList.add(hashMap);

                        for(Map.Entry<String,Object> entry : hashMap.entrySet()){
                            System.out.println(entry.getKey()+":"+entry.getValue());
                            text.setText(text.getText()+ "\n" +entry.getKey()+"  "+entry.getValue());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(UploadedFeedback.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(arrayAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }
}
