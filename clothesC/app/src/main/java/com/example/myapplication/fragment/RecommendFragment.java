package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.OuterAdapter;
import com.example.myapplication.Adaptor.PantAdapter;
import com.example.myapplication.Adaptor.ShirtAdapter;
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

import static com.example.myapplication.Util.showToast;

public class RecommendFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private double cTemp;
    private ArrayList<ClothesItem> itemList;
    private StorageReference storageRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean topScrolled;
    private boolean updating;
    private ShirtAdapter shirtAdapter;
    private OuterAdapter outerAdapter;
    private PantAdapter pantAdapter;

    private ArrayList<String> tempClothes=new ArrayList<>();

    private ArrayAdapter<CharSequence> hightKind;

    private String clotheskind="outer";

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

        Spinner spinner = (Spinner) view.findViewById(R.id.hightKind);
        spinner.setPrompt("옷 종류를 선택하세요");
        hightKind=ArrayAdapter.createFromResource(getActivity(),R.array.recommendKind, android.R.layout.simple_spinner_item);
        hightKind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(hightKind);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clotheskind = hightKind.getItem(position).toString();
                if (clotheskind.equals("아우터")) {
                    clotheskind = "outer";
                    itemList.clear();
                    outerAdapter = new OuterAdapter(getActivity(), itemList);
                    recyclerView.setAdapter(outerAdapter);
                    loadClothes(false);

                } else if (clotheskind.equals("상의")) {
                    clotheskind = "shirt";
                    itemList.clear();
                    shirtAdapter = new ShirtAdapter(getActivity(), itemList);
                    recyclerView.setAdapter(shirtAdapter);
                    loadClothes(false);
                } else {
                    clotheskind = "pant";
                    itemList.clear();
                    pantAdapter=new PantAdapter(getActivity(), itemList);
                    recyclerView.setAdapter(pantAdapter);
                    loadClothes(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast(getActivity(), "종류를 선택해주세요.");
            }
        });


        if (getArguments() != null) {
            String param1 = getArguments().getString("currentTemp");
            cTemp=Double.parseDouble(param1);
            if(20<=cTemp&&cTemp<=22){
                tempClothes.add("셔츠");
                tempClothes.add("블라우스");
                tempClothes.add("긴바지");
            }
            else if(23<=cTemp&&cTemp<=27){
                tempClothes.add("반팔");
                tempClothes.add("짧은바지");
            }
            else if(28<=cTemp){
                tempClothes.add("민소매");
                tempClothes.add("짧은바지");
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
                    loadClothes(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        loadClothes(false);

        return view;
    }

    private void loadClothes(final boolean clear) {
        updating = true;
        Date date = itemList.size() == 0 || clear ? new Date() : itemList.get(itemList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection(clotheskind);
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                itemList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.get("publisher").toString();
                                String kind_L=document.get("lowerkind").toString();
                                if (user.getUid().equals(id)) {
                                    for(int i=0; i<tempClothes.size(); i++){
                                        if((tempClothes.get(i)).equals(kind_L)){
                                            itemList.add(new ClothesItem(
                                                    (ArrayList<String>) document.getData().get("contents"),
                                                    (ArrayList<String>) document.getData().get("formats"),
                                                    document.getData().get("publisher").toString(),
                                                    new Date(document.getDate("createdAt").getTime()),
                                                    document.getData().get("kind").toString(),
                                                    document.getData().get("lowerkind").toString(),
                                                    document.getId()));
                                            Log.d("TAG","dddd123"+itemList);
                                        }
                                    }
                                }
                            }

                            if(clotheskind.equals("shirt")){
                                shirtAdapter.notifyDataSetChanged();
                            }
                            else if(clotheskind.equals("outer")){
                                outerAdapter.notifyDataSetChanged();
                            }
                            else if(clotheskind.equals("pant")){
                                pantAdapter.notifyDataSetChanged();
                            }
                        }
                        updating = false;
                    }
                });
    }

}
