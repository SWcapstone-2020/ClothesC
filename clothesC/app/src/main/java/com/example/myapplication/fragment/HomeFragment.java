package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.PostAdapter;
import com.example.myapplication.Post.PostInfo;
import com.example.myapplication.Post.WritePostActivity;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.isStorageUrl;
import static com.example.myapplication.Util.showToast;
import static com.example.myapplication.Util.storageUrlToName;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private PostAdapter postAdaptor;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;
    private int successCount;
    private String check;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private String writer;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context=container.getContext(); //Toast창 사용하기 위해 사용


        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        postList = new ArrayList<>();
        postAdaptor = new PostAdapter(getActivity(), postList);
        postAdaptor.setOnPostListener(onPostListener);



        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        view.findViewById(R.id.writePost).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdaptor);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        postsUpdate(false);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onPause(){
        super.onPause();
//        postAdaptor.playerStop();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writePost:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            if(user.getUid().equals(postList.get(position).getPublisher())){
                final String id = postList.get(position).getId();
                ArrayList<String> contentList = postList.get(position).getContents();
                for (int i = 0; i < contentList.size(); i++) {
                    String contents = contentList.get(i);
                    if(isStorageUrl(contents)){ //내용이 url인가? (즉 이미지인가 동영상인가)
                        successCount++;
                        StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                successCount--;
                                storageDeleteUpdate(id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context,"게시글 삭제에 실패했습니다",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
                storageDeleteUpdate(id);
            }
               else{
                Toast.makeText(context,"본인이 작성하신 게시물이 아니라 삭제가 불가능합니다", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onModify(int position) {
            if(user.getUid().equals(postList.get(position).getPublisher())) {
                goWriteActivity(WritePostActivity.class, postList.get(position));
            }
            else{
                Toast.makeText(context,"본인이 작성하신 게시물이 아니라 수정이 불가능합니다", Toast.LENGTH_LONG).show();
            }

        }

    };
    private void storageDeleteUpdate(String id) {
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context,"게시글이 삭제되었습니다.",Toast.LENGTH_LONG).show();
                            boolean clear=true;
                            postsUpdate(clear);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"게시글 삭제 실패",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    private void goWriteActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }

    private void postsUpdate(final boolean clear) {
        updating = true;
        Date date = postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                postList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                postList.add(new PostInfo(
                                        document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getId()));
                                writer=document.getData().get("publisher").toString();
                            }
                            postAdaptor.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }



}