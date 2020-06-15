package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adaptor.MyPostAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Post.PostInfo;
import com.example.myapplication.Post.WritePostActivity;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;
import com.example.myapplication.signup.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.isStorageUrl;
import static com.example.myapplication.Util.storageUrlToName;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    ImageView imageView;
    private boolean topScrolled;
    private FirebaseFirestore db;
    private ArrayList<PostInfo> postList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private MyPostAdapter myPostAdapter;
    private StorageReference storageRef;
    private Context context;
    private boolean updating;
    private int successCount;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = (ImageView) view.findViewById(R.id.profileView);
        final TextView textName = (TextView) view.findViewById(R.id.nameEditText);
        final TextView textBirth = (TextView) view.findViewById(R.id.introEditText);

        final RecyclerView recyclerView = view.findViewById(R.id.mypost);

       db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();

        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String name = documentSnapshot.get("name").toString();
                            String intro = documentSnapshot.get("intro").toString();
                            textName.setText(name);
                            textBirth.setText(intro);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) { //성공시 출력
                        }
                    });
        } else {
            startActivity(LoginActivity.class);
        }


        view.findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.profileUpdate).setOnClickListener(onClickListener);

        myPostAdapter=new MyPostAdapter(getActivity(),postList);
        myPostAdapter.setOnPostListener(onPostListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myPostAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if (newState == 1 && firstVisibleItemPosition == 0) {
                    topScrolled = true;
                }
                if (newState == 0 && topScrolled) {
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                if (totalItemCount - 3 <= lastVisibleItemPosition && !updating) {
                    postsUpdate(false);
                }

                if (0 < firstVisibleItemPosition) {
                    topScrolled = false;
                }
            }
        });

        postsUpdate(false);

        return view;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(LoginActivity.class);
                    break;
                case R.id.profileUpdate:
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.onFragmentChanged(1);
                    break;

            }
        }
    };


    private void startActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://clothesc-ver1.appspot.com");
        StorageReference pathReference = storageReference.child("profileImage/" + user.getUid() + "/" + "profile.PNG");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, uri.toString());

                Glide.with(context).load(uri).into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void postsUpdate(final boolean clear) {
        updating = true;
        Date date = postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                postList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.get("publisher").toString();
                                if (user.getUid().equals(id)) {
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            (ArrayList<String>) document.getData().get("formats"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()));
                                }

                            }
                            myPostAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id = postList.get(position).getId();
            ArrayList<String> contentList = postList.get(position).getContents();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) { //내용이 url인가? (즉 이미지인가 동영상인가)
                    successCount++;
                    StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "이미지 삭제...", Toast.LENGTH_LONG).show();
                            successCount--;
                            storageDeleteUpdate(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
//                            showToast(ShowPostActivity.this, "삭제하지 못했습니다");
                        }
                    });

                }
            }
            storageDeleteUpdate(id);
        }

        @Override
        public void onModify(int position) {
            goWriteActivity(WritePostActivity.class, postList.get(position));
        }

    };

    private void storageDeleteUpdate(String id) {
        if (successCount == 0) {
            db.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "게시글이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                            boolean clear = true;
                            postsUpdate(clear);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "게시글 삭제 실패", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void goWriteActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }

}