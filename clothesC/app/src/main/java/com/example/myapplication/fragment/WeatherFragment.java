package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.WeahterActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    FirebaseAuth auth;

    /*
    TextView curLocation, curDate, curTemp, curWeather,w1,w2,w3,w4;
    ImageView weatherIcon, icon1,icon2,icon3,icon4;

    private Timer mTimer;
    private LocationManager locationManager;

    double lat = 37.566535;
    double lon = 126.977969;

    //다음 액티비티로 넘어갈때 넘겨줄 값
    String getTime,weather_str,location;
    int temp,temp_max,temp_min;
    int weather_id;
    int[] weather_id2 = new int[4];
    int[] weather_temp = new int[4];
    String[] weather_date = new String[4];

*/

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        view.findViewById(R.id.weatherButton).setOnClickListener(onClickListener);
/*
        curLocation = (TextView)view.findViewById(R.id.location);
        curDate = (TextView) view.findViewById(R.id.currentDate);
        curTemp = (TextView) view.findViewById(R.id.temp);
        curWeather = (TextView)view. findViewById(R.id.weather);
        weatherIcon = (ImageView)view. findViewById(R.id.weatherIcon);
        icon1 = (ImageView)view. findViewById(R.id.icon1);
        icon2 = (ImageView)view. findViewById(R.id.icon2);
        icon3 = (ImageView) view.findViewById(R.id.icon3);
        icon4 = (ImageView) view.findViewById(R.id.icon4);
        w1 = (TextView)view.findViewById(R.id.w1);
        w2 = (TextView)view.findViewById(R.id.w2);
        w3 = (TextView)view.findViewById(R.id.w3);
        w4 = (TextView)view.findViewById(R.id.w4);

        try {
            Location lo = getLocation();
            //lat=lo.getLatitude();
            //lon=lo.getLongitude();
            //WeatherFragment.MainTimerTask timerTask = new MainTimerTask();
            MainTimerTask timerTask = new MainTimerTask();
            mTimer = new Timer();
            mTimer.schedule(timerTask, 500, 1000);

            new GetWeather().start();
            new GetWeather5day3time().start();
        }catch (NullPointerException e) {
            //Toast.makeText(this,"위치정보를 받아오지 못했습니다",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
           // Toast.makeText(this,"에러다:"+e,Toast.LENGTH_LONG).show();
        }
*/
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.weatherButton:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(WeahterActivity.class);
                    break;
            }
        }
    };


    private void startActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }


}






