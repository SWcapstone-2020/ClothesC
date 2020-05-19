package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.PostAdapter;
import com.example.myapplication.signup.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ShowPostActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(firebaseUser==null){
            startActivity(LoginActivity.class);
        }
        else{

            DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document=task.getResult();
                        if(document != null){
                            if(document.exists()){
//                                Log.d("show","Document data:"+document.getData());
                            }
                            else{
                                startActivity(LoginActivity.class);
                            }
                        }
                    }
                    else{
//                        Log.d("show","get failed with"+task.getException());
                    }
                }
            });
        }
        recyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.writePost).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowPostActivity.this));


    }
    @Override
    protected void onResume(){
        super.onResume();

        if(firebaseUser!=null){
            CollectionReference collectionReference=firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
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
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()
                                    ));
                                }


                                RecyclerView.Adapter mAdapter = new PostAdapter(ShowPostActivity.this, postList);;
                                recyclerView.setAdapter(mAdapter);
                            } else {

                            }
                        }

                    });
        }




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
