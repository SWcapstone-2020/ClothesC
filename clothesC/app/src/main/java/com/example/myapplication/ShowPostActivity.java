package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ShowPostActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("posts")

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    ArrayList<PostInfo> postList=new ArrayList<>();;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                postList.add(new PostInfo(document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime())
                                ));
                            }
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ShowPostActivity.this));

                            RecyclerView.Adapter mAdapter = new PostAdapter(ShowPostActivity.this, postList);;
                            recyclerView.setAdapter(mAdapter);
                        } else {

                        }
                    }

                });
        findViewById(R.id.writePost).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writePost:
                    startActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
