package com.example.myapplication.fragment;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class WeatherApiFragment extends Fragment {
    private static final String TAG = "WeatherAPIFragment";

    private static final String type1 = "VilageFcstInfoService/getUltraSrtFcst?";
    private static final String type2 = "VilageFcstInfoService/getVilageFcst?";
    private static final String TAG_ID = "id";
    private static final String TAG_CAT = "category";
    private static final String TAG_DATE = "fcstDate";
    private static final String TAG_TIME = "fcstTime";
    private static final String TAG_VAL = "fcstValue";

    Button mButton;
    TextView mTextView;
    ProgressDialog pd;

    public WeatherApiFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_api, container, false);
        // Menu Setting
        setHasOptionsMenu(true);
        mTextView = (TextView) rootView.findViewById(R.id.weatherTextview);

        myAsyncTask mat = new myAsyncTask(); //myAsyncTask 객체 생성
        mat.execute(type1); //myAsyncTask 실행하기

        dAsyncTask mat2 = new dAsyncTask(); //myAsyncTask 객체 생성
        mat2.execute(type2); //myAsyncTask 실행하기

        return rootView;
    }


    protected class myAsyncTask extends AsyncTask<String, Void, Boolean> {
        String forecastJsonStr = null;
        ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();

        protected void saveNshow(ArrayList<HashMap<String, String>> mArrayList, int n, String category, String fcstDate, String fcstTime, String fcstValue) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(TAG_ID, String.valueOf(n));
            hashMap.put(TAG_CAT, category);
            hashMap.put(TAG_DATE, fcstDate);
            hashMap.put(TAG_TIME, fcstTime);
            hashMap.put(TAG_VAL, fcstValue);

            mArrayList.add(hashMap);

            HashMap<String, String> outputHashMap = mArrayList.get(n);
            String id = outputHashMap.get("id");
            String cat = outputHashMap.get("category");
            String date = outputHashMap.get("fcstDate");
            String time = outputHashMap.get("fcstTime");
            String val = outputHashMap.get("fcstValue");

            String s = String.format(getResources().getString(R.string.textview_message), time, cat, val);
            mTextView.append(s);
            Log.d("mArrayList :", String.valueOf(mArrayList.get(n)));
        }

        @Override
        protected Boolean doInBackground(String... types) {
            for(String type : types) {
                HttpURLConnection urlConnection = null; //HttpUrlConnection
                BufferedReader reader = null;

                try {

                            // 날시 데이터 URL 에 사용될 옵션
                            String nx = "53";
                            String ny = "38";
                            String dataType = "JSON";
                            String key = "8Gnkss2kI%2F%2BVSjVNBRpY9QcW53gpQ953TJoA2E%2B1260r6hNjBYOBotBXFuhE3XXfy2Hzx%2BSV9JdIsoEriCzApQ%3D%3D";
                            String baseDate = "20200613";

                            String baseTime = "1906";//현재시간
                            int hour = Integer.valueOf(baseTime.substring(0, 2));
                            int min = Integer.valueOf(baseTime.substring(2, 4));
//                            min = 0;
                            if (min < 30) {
                                hour = hour - 1;
                                min = min + 30;
                            } //19:00 / 1906 -> 1830
                            else {
                                min = 30;
                            } //19:30 / 1945 -> 1930

                            baseTime = hour + "" + min;

                            int numOfRows = 40;
                            int pageNo = 1;


                            jsonParsing(type, urlConnection, reader, nx, ny, baseDate, baseTime, dataType, numOfRows, pageNo, key);


                    try {

                        Log.d("Json Result =>", forecastJsonStr);
                        JSONObject JsonObj = new JSONObject(forecastJsonStr);
                        Log.d("JSON", forecastJsonStr);
                        JSONObject parse_response = JsonObj.getJSONObject("response");
                        JSONObject parse_body = parse_response.getJSONObject("body");
                        JSONObject parse_items = parse_body.getJSONObject("items");
                        //JSONArray parse_item = (JSONArray) parse_items.get("item");
                        JSONArray parse_item = parse_items.getJSONArray("item");


                        currentWeatherParsing(parse_item);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {

                    forecastJsonStr = null;

                } finally {

                    if (urlConnection != null) {
                        urlConnection.disconnect(); //HttpURLConnection 연결 끊기
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }

                }
            }
            return null;
        }

        private void jsonParsing(String type, HttpURLConnection urlConnection, BufferedReader reader, String nx, String ny, String baseDate, String baseTime, String dataType, int numOfRows, int pageNo, String key) throws IOException {

            //새 URL 객체
            //UriBuilder 를 이용해 URL 만들기
            final String FORECAST_BASE_URL =
                    "http://apis.data.go.kr/1360000/"+ type;
            final String serviceKey_PARAM = "serviceKey";
            final String dataType_PARAM = "dataType";
            final String numOfRows_PARAM = "numOfRows";
            final String pageNo_PARAM = "pageNo";
            final String base_date_PARAM = "base_date";
            final String base_time_PARAM = "base_time";
            final String nx_PARAM = "nx";
            final String ny_PARAM = "ny";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(serviceKey_PARAM, key)
                    .appendQueryParameter(dataType_PARAM, dataType)
                    .appendQueryParameter(numOfRows_PARAM, String.valueOf(numOfRows))
                    .appendQueryParameter(pageNo_PARAM, String.valueOf(pageNo))
                    .appendQueryParameter(base_date_PARAM, baseDate)
                    .appendQueryParameter(base_time_PARAM, baseTime)
                    .appendQueryParameter(nx_PARAM, nx)
                    .appendQueryParameter(ny_PARAM, ny)
                    .build();


            String link = builtUri.toString(); //String 쓰레기값 제거
            link = link.replace("%25", "%");
            Log.d("URL", link);
            URL url = new URL(link);
            Log.d("URL", String.valueOf(url));
            //새 URLConnection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //InputStream 을 사용해 데이터 읽어들이기
            InputStream inputStream = urlConnection.getInputStream();

            //StringBuffer 에 데이터 저장
            StringBuffer buffer = new StringBuffer(); // 새로운 StringBuffer 생성
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // 불러온 데이터가 비어있음.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString(); //로드한 데이터 문자열 변수에 저장.
        }

        private void currentWeatherParsing(JSONArray parse_item) throws JSONException {
            int n = -1;
            JSONObject obj = parse_item.getJSONObject(0);
            String bt = obj.getString("baseTime"); //1906
            int hour = Integer.valueOf(bt.substring(0, 2));
            int min = Integer.valueOf(bt.substring(2, 4));

            String fcst_time = "";
            hour = hour + 1; min = min - 30;
//            if(hour<10) {
//                hour='0'+hour;
//            }
//            if(min<10) {
//                min='0'+min;
//            }
            String str = String.format("%02d%02d", hour, min);
            fcst_time = str;


            Log.d("예보보보보보보보보보보보보보보ㅗ보보보봅시각", fcst_time); //1960

            for ( int i = 0 ; i < parse_item.length(); i++ ) {
                JSONObject fObj = null;


                String category = null;
                String fcstDate= null;
                String fcstTime= null;
                String fcstValue= null;

                fObj = parse_item.getJSONObject(i);

                category = fObj.getString("category");
                fcstDate = fObj.getString("fcstDate");
                fcstTime = fObj.getString("fcstTime");
                fcstValue = fObj.getString("fcstValue");


                if(fcst_time.equals(fcstTime)) {
                    switch( category ){
                        case "T1H" : // 현재 시간 날씨
                            n++;
                            saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                            break;
                        case "SKY" : // 하늘 상태
                            n++;
                            saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                            break;
                        case "PTY" : // 강수 형태
                            n++;
                            saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                            break;
                        default :
                            break;
                    }
                }
            }
        }



        protected void onPostExecute(boolean t){
            super.onPostExecute(t);
//            dAsyncTask mat2 = new dAsyncTask(); //myAsyncTask 객체 생성
//            mat2.execute(type2); //myAsyncTask 실행하기
        }

    }

    protected class dAsyncTask extends AsyncTask<String, Void, Void>{
        String forecastJsonStr = null;
        ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();

        protected void saveNshow(ArrayList<HashMap<String, String>> mArrayList, int n, String category, String fcstDate, String fcstTime, String fcstValue) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(TAG_ID, String.valueOf(n));
            hashMap.put(TAG_CAT, category);
            hashMap.put(TAG_DATE, fcstDate);
            hashMap.put(TAG_TIME, fcstTime);
            hashMap.put(TAG_VAL, fcstValue);

            mArrayList.add(hashMap);

            HashMap<String, String> outputHashMap = mArrayList.get(n);
            String id = outputHashMap.get("id");
            String cat = outputHashMap.get("category");
            String date = outputHashMap.get("fcstDate");
            String time = outputHashMap.get("fcstTime");
            String val = outputHashMap.get("fcstValue");
//            String s = String.format(getResources().getString(R.string.textview_message), id, cat, date, time, val);
            String s = String.format(getResources().getString(R.string.textview_message), time, cat, val);
            mTextView.append(s);
            // {fcstTime=fcstTime0300, fcstValue=fcstValue22, id=id4, category=categoryT3H, fcstDate=fcstDate20200612}
            Log.d("mArrayList :", String.valueOf(mArrayList.get(n)));
        }


        @Override
        protected Void doInBackground(String... types) {
            for(String type : types) {
                HttpURLConnection urlConnection = null; //HttpUrlConnection
                BufferedReader reader = null;

//                if(min < 30) { hour = hour-1; min = min + 30; } //19:00 / 1906 -> 1830
//                else { min = 30;} //19:30 / 1945 -> 1930
                try {

                    String nx = "53";
                    String ny = "38";
                    String dataType = "JSON";
                    String key = "8Gnkss2kI%2F%2BVSjVNBRpY9QcW53gpQ953TJoA2E%2B1260r6hNjBYOBotBXFuhE3XXfy2Hzx%2BSV9JdIsoEriCzApQ%3D%3D";
                    String baseDate = "20200613";
                    String baseTime = "2300";
                    int numOfRows = 77;
                    int pageNo = 1;
                    jsonParsing(type, urlConnection, reader, nx, ny, baseDate, baseTime, dataType, numOfRows, pageNo, key);

                    try {

                        Log.d("Json Result =>", forecastJsonStr);
                        JSONObject JsonObj = new JSONObject(forecastJsonStr);
                        Log.d("JSON", forecastJsonStr);
                        JSONObject parse_response = JsonObj.getJSONObject("response");
                        JSONObject parse_body = parse_response.getJSONObject("body");
                        JSONObject parse_items = parse_body.getJSONObject("items");
                        //JSONArray parse_item = (JSONArray) parse_items.get("item");
                        JSONArray parse_item = parse_items.getJSONArray("item");

                        dayWeatherParsing(parse_item);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {

                    forecastJsonStr = null;

                } finally {

                    if (urlConnection != null) {
                        urlConnection.disconnect(); //HttpURLConnection 연결 끊기
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }

                }
            }
            return null;
        }

        private void jsonParsing(String type, HttpURLConnection urlConnection, BufferedReader reader, String nx, String ny, String baseDate, String baseTime, String dataType, int numOfRows, int pageNo, String key) throws IOException {

            //새 URL 객체
            //UriBuilder 를 이용해 URL 만들기
            final String FORECAST_BASE_URL =
                    "http://apis.data.go.kr/1360000/"+ type;
            final String serviceKey_PARAM = "serviceKey";
            final String dataType_PARAM = "dataType";
            final String numOfRows_PARAM = "numOfRows";
            final String pageNo_PARAM = "pageNo";
            final String base_date_PARAM = "base_date";
            final String base_time_PARAM = "base_time";
            final String nx_PARAM = "nx";
            final String ny_PARAM = "ny";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(serviceKey_PARAM, key)
                    .appendQueryParameter(dataType_PARAM, dataType)
                    .appendQueryParameter(numOfRows_PARAM, String.valueOf(numOfRows))
                    .appendQueryParameter(pageNo_PARAM, String.valueOf(pageNo))
                    .appendQueryParameter(base_date_PARAM, baseDate)
                    .appendQueryParameter(base_time_PARAM, baseTime)
                    .appendQueryParameter(nx_PARAM, nx)
                    .appendQueryParameter(ny_PARAM, ny)
                    .build();


            String link = builtUri.toString(); //String 쓰레기값 제거
            link = link.replace("%25", "%");
            Log.d("URL", link);
            URL url = new URL(link);
            Log.d("URL", String.valueOf(url));
            //새 URLConnection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //InputStream 을 사용해 데이터 읽어들이기
            InputStream inputStream = urlConnection.getInputStream();

            //StringBuffer 에 데이터 저장
            StringBuffer buffer = new StringBuffer(); // 새로운 StringBuffer 생성
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // 불러온 데이터가 비어있음.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString(); //로드한 데이터 문자열 변수에 저장.
        }

        private void dayWeatherParsing(JSONArray parse_item) throws JSONException {
            int n = -1;
            for (int i = 0; i < parse_item.length(); i++) {
                //JSONObject lObj = (JSONObject) parse_item.get(i);
                JSONObject lObj = parse_item.getJSONObject(i);
                //                    String status = lObj.optString("status");
                String fcstDate = null;
                String fcstTime = null;
                String category = null;
                String fcstValue = null;

                category = lObj.getString("category");
                fcstDate = lObj.getString("fcstDate");
                fcstTime = lObj.getString("fcstTime");
                fcstValue = lObj.getString("fcstValue");

                String item = null;


                switch (fcstTime) {
                    case "0000":
                        break;
                    case "0300":
                        break;
                    case "0600":
                        if ("TMN".equals(category)) { // 최저 기온
                            n++;
                            saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                        }
                    case "1500":
                        if ("TMX".equals(category)) { // 최고 기온
                            n++;
                            saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                        }

                    default:
                        switch (category) {
                            case "T3H": // 3시간 간격 기온
                                n++;
                                saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                                break;

                            case "SKY": // 하늘 상태
                                n++;
                                saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                                break;
                            case "PTY": // 강수 형태

                                n++;
                                saveNshow(mArrayList, n, category, fcstDate, fcstTime, fcstValue);
                                break;

                            default:
                                break;
                        }
                        break;
                }

            }

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 정의한 Menu 리소스를 여기서 Inflate 합니다.
        inflater.inflate(R.menu.weatherapifragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴 항목 클릭을 여기서 처리합니다..
        int id = item.getItemId(); // 클릭된 항목 id 값 얻기

//얻은 id 값에 따라 클릭 처리
        if (id == R.id.action_refresh) { //id값이 action_refresh 이면.
            String type1 = "VilageFcstInfoService/getUltraSrtFcst?";
            String type2 = "VilageFcstInfoService/getVilageFcst?";
            new myAsyncTask().execute(type1, type2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
