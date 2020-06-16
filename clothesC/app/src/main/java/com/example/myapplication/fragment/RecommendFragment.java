package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.ShirtAdapter;
import com.example.myapplication.Adaptor.Temp20_22Adapter;
import com.example.myapplication.Clothes.ClothesItem;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class RecommendFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private double cTemp;
    private ArrayList<ClothesItem> itemList;
    private StorageReference storageRef;
    private ShirtAdapter shirtAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean topScrolled;
    private boolean updating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recommend_clothes, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        itemList=new ArrayList<>();
        final RecyclerView recyclerView = view.findViewById(R.id.recommendRecycler);

        if (getArguments() != null) {
            String param1 = getArguments().getString("currentTemp");
            cTemp=Double.parseDouble(param1);
            Log.d("TAG","ddd "+cTemp);
            if(19<cTemp&&cTemp<23){
                shirtAdapter=new ShirtAdapter(getActivity(),itemList);
                recyclerView.setAdapter(shirtAdapter);
            }
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
//                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    shirtUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        shirtUpdate(false);




        return view;
    }
    private void shirtUpdate(final boolean clear) {
        updating = true;
        Date date = itemList.size() == 0 || clear ? new Date() : itemList.get(itemList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("shirt");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                itemList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.get("publisher").toString();
                                String kind=document.get("lowerkind").toString();
                                if (user.getUid().equals(id)) {
                                    if(kind.equals("셔츠")||kind.equals("블라우스")){
                                        itemList.add(new ClothesItem(
                                                (ArrayList<String>) document.getData().get("contents"),
                                                (ArrayList<String>) document.getData().get("formats"),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getData().get("kind").toString(),
                                                document.getData().get("lowerkind").toString(),
                                                document.getId()));
                                        shirtAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        updating = false;
                    }
                });
    }

}
