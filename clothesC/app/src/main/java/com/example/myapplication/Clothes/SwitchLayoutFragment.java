package com.example.myapplication.Clothes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.EtcAdapter;
import com.example.myapplication.Adaptor.OuterAdapter;
import com.example.myapplication.Adaptor.PantAdapter;
import com.example.myapplication.Adaptor.ShirtAdapter;
import com.example.myapplication.Adaptor.ShoesAdapter;
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

import static com.example.myapplication.Util.isEtcUrl;
import static com.example.myapplication.Util.isItemUrl;
import static com.example.myapplication.Util.isPantUrl;
import static com.example.myapplication.Util.isShirtUrl;
import static com.example.myapplication.Util.isShoesUrl;
import static com.example.myapplication.Util.storageUrlToName;

public class SwitchLayoutFragment extends Fragment {
    private static final String TAG = "SwitchLayoutFragment";

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageRef;
    private ShirtAdapter shirtAdapter;
    private OuterAdapter outerAdapter;
    private PantAdapter pantAdapter;
    private ShoesAdapter shoesAdapter;
    private EtcAdapter etcAdapter;
    private ArrayList<ClothesItem> itemList;
    private boolean updating;
    private boolean topScrolled;
    private int successCount;
    private String type;

    public SwitchLayoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_switch_layout, container, false);


        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        itemList=new ArrayList<>();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        type= getArguments().getString("type");
        if(type.equals("shirt")){
            shirtAdapter=new ShirtAdapter(getActivity(),itemList);
            shirtAdapter.setOnPostListener(onPostListener);
            recyclerView.setAdapter(shirtAdapter);
        }
        else if(type.equals("outer")){
            outerAdapter=new OuterAdapter(getActivity(),itemList);
            outerAdapter.setOnPostListener(onPostListener);
            recyclerView.setAdapter(outerAdapter);
        }
        else if(type.equals("pant")){
            pantAdapter=new PantAdapter(getActivity(),itemList);
            pantAdapter.setOnPostListener(onPostListener);
            recyclerView.setAdapter(pantAdapter);
        }
        else if(type.equals("shoes")){
            shoesAdapter=new ShoesAdapter(getActivity(),itemList);
            shoesAdapter.setOnPostListener(onPostListener);
            recyclerView.setAdapter(shoesAdapter);
        }
        else if(type.equals("etc")){
            etcAdapter=new EtcAdapter(getActivity(), itemList);
            etcAdapter.setOnPostListener(onPostListener);
            recyclerView.setAdapter(etcAdapter);
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
    }


    private void initItemsData() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu,menu);
    }

    private void postsUpdate(final boolean clear) {
        updating = true;
        Date date = itemList.size() == 0 || clear ? new Date() : itemList.get(itemList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection(type);
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                itemList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String id = document.get("publisher").toString();
                                if(user.getUid().equals(id)){
                                    itemList.add(new ClothesItem(

                                            (ArrayList<String>) document.getData().get("contents"),
                                            (ArrayList<String>) document.getData().get("formats"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getData().get("kind").toString(),
                                            document.getId()));
                                }

                            }
                            if(type.equals("shirt")){
                                shirtAdapter.notifyDataSetChanged();
                            }
                            else if(type.equals("outer")){
                                outerAdapter.notifyDataSetChanged();
                            }
                            else if(type.equals("pant")){
                                pantAdapter.notifyDataSetChanged();
                            }
                            else if(type.equals("shoes")){
                                shoesAdapter.notifyDataSetChanged();
                            }
                            else if(type.equals("etc")){
                                etcAdapter.notifyDataSetChanged();
                            }
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
            final String id = itemList.get(position).getId();

            ArrayList<String> contentList = itemList.get(position).getContents();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if(type.equals("shirt")){
                    if(isShirtUrl(contents)){ //내용이 url인가? (즉 이미지인가 동영상인가)
                        deleteItem(id, contents);
                    }
                }
                else if(type.equals("outer")){
                    if(isItemUrl(contents)){
                     deleteItem(id,contents);
                    }
                }
                else if(type.equals("pant")){
                    if(isPantUrl(contents)){
                        deleteItem(id,contents);
                    }
                }
                else if(type.equals("shoes")){
                    if(isShoesUrl(contents)){
                        deleteItem(id,contents);
                    }
                }
                else {
                    if(isEtcUrl(contents)){
                        deleteItem(id,contents);
                    }
                }

            }
            storageDeleteUpdate(id);
        }

        @Override
        public void onModify(int position) {
            goWriteActivity(SubmitActivity.class, itemList.get(position));
        }

    };

    private void deleteItem(final String id, String contents) {
        successCount++;
        StorageReference desertRef = storageRef.child(type + id + "/" + storageUrlToName(contents));
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                            showToast(ShowPostActivity.this, "삭제 했습니다.");
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

    private void storageDeleteUpdate(String id) {
        if (successCount == 0) {
            firebaseFirestore.collection(type).document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            boolean clear=true;
                            postsUpdate(clear);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }


    private void goWriteActivity(Class c, ClothesItem clothesItem) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("item", clothesItem);
        startActivity(intent);
    }

}
