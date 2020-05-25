package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.R;
import com.example.myapplication.signup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    FirebaseAuth auth;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        view.findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.templogout).setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cameraButton:
                    startActivity(CameraActivity.class);
                    break;
                case R.id.templogout:
                    startActivity(LoginActivity.class);
                    getActivity().finish();
                    break;
            }
        }
    };


    private void startActivity(Class c){
        Intent intent=new Intent(getActivity(),c);
        startActivity(intent);
    }
}



