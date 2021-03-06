package com.example.myapplication.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;

import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {

    private Context context;
    Spinner spinner;	//스피너
    Button getBtn;		//날씨 가져오는 버튼
    Button recommend;
    TextView text0;		//날씨 뿌려주는 텍스트창
    TextView text1;		//날씨 뿌려주는 텍스트창
    TextView text2;		//날씨 뿌려주는 텍스트창
    TextView text3;		//날씨 뿌려주는 텍스트창
    TextView text4;		//날씨 뿌려주는 텍스트창
    String sCategory;	//동네
    String sTm;			//발표시각
    String [] sHour;	//예보시간(총 15개정도 받아옴 3일*5번)
    String [] sDay;		//날짜(몇번째날??)
    String [] sTemp;	//현재온도
    String [] sWdKor;	//풍향
    String [] sReh;		//습도
    String [] sWfKor;	//날씨

    int data=0;	//이건 파싱해서 array로 넣을때 번지

    boolean bCategory;	//여긴 저장을 위한 플래그들
    boolean bTm;
    boolean bHour;
    boolean bDay;
    boolean bTemp;
    boolean bWdKor;
    boolean bReh;
    boolean bItem;
    boolean bWfKor;

    boolean tCategory;	//이건 text로 뿌리기위한 플래그
    boolean tTm;
    boolean tItem;

    Handler handler;	//핸들러


    //동네 코드정보 정말이지 너무많다;; 나중에 db배우면 전국을 다 넣어보자
    String dongcode[]={"5011063000","5011061000","5011025000","5011025300", "5011025600", "5011025900",
            "5011031000", "5011032000", "5011033000", "5011051000","5011052000","5011053000","5011054000","5011055000","5011056000","5011057000","5011058000", "5011059000",
            "5011060000","5011062000", "5011064000","5011065000","5011066000","5011067000","5011068000","5011069000"};
    //동네 이름
    String donglist[]={"아라동","삼양동", "한림읍", "애월읍", "구좌읍", "조천읍", "한경면", "추자면", "우도면", "일도1동", "일도2동", "이도1동", "이동2동", "삼도1동", "삼도2동", "용담1동", "용담2동", "건입동","화북동", "봉개동", "오라동", "연동", "노형동", "외도동", "이호동", "도두동" };
    String dong;	//최종적으로 가져다 붙일 동네코드가 저장되는 변수


    public WeatherFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        handler=new Handler();	//스레드&핸들러처리
        spinner=(Spinner)view.findViewById(R.id.spinner);



        bCategory=bTm=bHour=bTemp=bWdKor=bReh=bDay=bWfKor=tCategory=tTm=tItem=false;	//부울상수는 false로 초기화해주자

        sHour=new String[30];	//예보시간(사실 15개밖에 안들어오지만 넉넉하게 20개로 잡아놓음)
        sDay=new String[30];	//날짜
        sTemp=new String[30];	//현재온도
        sWdKor=new String[30];	//풍향
        sReh=new String[30];	//습도
        sWfKor=new String[30];	//날씨

        spinner = (Spinner) view.findViewById(R.id.spinner);		//스피너 객체생성
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {	//이부분은 스피너에 나타나는 내용


            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	//선택시
                dong=dongcode[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {	//미선택시
                dong=dongcode[0];

            }
        });
        // 어댑터 객체 생성
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, donglist);	//어댑터를 통해 스피너에 donglist 넣어줌
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//dropdown형식

        // 어댑터 설정
        spinner.setAdapter(adapter);



        text0=(TextView) view.findViewById(R.id.textView0);	//텍스트 객체생성
        text0.setPadding(10,10,10,10);
        text1=(TextView) view.findViewById(R.id.textView1);	//텍스트 객체생성
        text1.setPadding(10,10,10,10);
//        text2=(TextView) view.findViewById(R.id.textView2);	//텍스트 객체생성
        text3=(TextView) view.findViewById(R.id.textView3);	//텍스트 객체생성
        text3.setPadding(10,10,10,10);
        text4=(TextView) view.findViewById(R.id.textView4);	//텍스트 객체생성
        text4.setPadding(10,10,10,10);
        getBtn=(Button) view.findViewById(R.id.getBtn);
        recommend=(Button)view.findViewById(R.id.recommend);
        recommend.setVisibility(View.GONE);
        recommend.setOnClickListener(onClickListener);//버튼 객체생성
        getBtn.setOnClickListener(new OnClickListener() {	//버튼을 눌러보자
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                text0.setText("");	//일단 중복해서 누를경우 대비해서 내용 지워줌
                text1.setText("");
//                text2.setText("");
                text3.setText("");
                text4.setText("");
                network_thread thread=new network_thread();		//스레드생성(UI 스레드사용시 system 뻗는다)
                thread.start();	//스레드 시작
                recommend.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecommendFragment recommendFragment=new RecommendFragment();
            Bundle bundle=new Bundle();
            switch (v.getId()) {
                case R.id.recommend:
                    bundle.putString("currentTemp",sTemp[0]);
                    recommendFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, recommendFragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("temp",sTemp[0]);
        startActivity(intent);
    }


    class network_thread extends Thread{	//기상청 연결을 위한 스레드
        /**
         * 기상청을 연결하는 스레드
         * 이곳에서 풀파서를 이용하여 기상청에서 정보를 받아와 각각의 array변수에 넣어줌
         * @author Ans
         */
        public void run(){

            try{


                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();	//이곳이 풀파서를 사용하게 하는곳
                factory.setNamespaceAware(true);									//이름에 공백도 인식
                XmlPullParser xpp=factory.newPullParser();							//풀파서 xpp라는 객체 생성

                String weatherUrl="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+dong;	//이곳이 기상청URL
                URL url=new URL(weatherUrl);		//URL객체생성
                InputStream is=url.openStream();	//연결할 url을 inputstream에 넣어 연결을 하게된다.
                xpp.setInput(is,"UTF-8");			//이렇게 하면 연결이 된다. 포맷형식은 utf-8로

                int eventType=xpp.getEventType();	//풀파서에서 태그정보를 가져온다.

                while(eventType!=XmlPullParser.END_DOCUMENT){	//문서의 끝이 아닐때

                    switch(eventType){
                        case XmlPullParser.START_TAG:	//'<'시작태그를 만났을때

                            if(xpp.getName().equals("category")){	//태그안의 이름이 카테고리일떄 (이건 동네이름이 나온다)
                                bCategory=true;

                            } if(xpp.getName().equals("tm")){		//발표시각정보
                            bTm=true;

                        } if(xpp.getName().equals("hour")){		//예보시간
                            bHour=true;

                        } if(xpp.getName().equals("day")){		//예보날(오늘 내일 모레)
                            bDay=true;

                        } if(xpp.getName().equals("temp")){		//예보시간기준 현재온도
                            bTemp=true;

                        } if(xpp.getName().equals("wdKor")){	//풍향정보
                            bWdKor=true;

                        } if(xpp.getName().equals("reh")){		//습도정보
                            bReh=true;

                        } if(xpp.getName().equals("wfKor")){	//날씨정보(맑음, 구름조금, 구름많음, 흐림, 비, 눈/비, 눈)
                            bWfKor=true;

                        }

                            break;

                        case XmlPullParser.TEXT:	//텍스트를 만났을때
                            //앞서 시작태그에서 얻을정보를 만나면 플래그를 true로 했는데 여기서 플래그를 보고
                            //변수에 정보를 넣어준 후엔 플래그를 false로~
                            if(bCategory){				//동네이름
                                sCategory=xpp.getText();
                                bCategory=false;
                            } if(bTm){					//발표시각
                            sTm=xpp.getText();
                            bTm=false;
                        }  if(bHour){				//예보시각
                            sHour[data]=xpp.getText();
                            bHour=false;
                        }  if(bDay){				//예보날짜
                            sDay[data]=xpp.getText();
                            bDay=false;
                        }  if(bTemp){				//현재온도
                            sTemp[data]=xpp.getText();
                            bTemp=false;
                        }  if(bWdKor){				//풍향
                            sWdKor[data]=xpp.getText();
                            bWdKor=false;
                        }  if(bReh){				//습도
                            sReh[data]=xpp.getText();
                            bReh=false;
                        } if(bWfKor){				//날씨
                            sWfKor[data]=xpp.getText();
                            bWfKor=false;
                        }
                            break;

                        case XmlPullParser.END_TAG:		//'>' 엔드태그를 만나면 (이부분이 중요)

                            if(xpp.getName().equals("item")){	//태그가 끝나느 시점의 태그이름이 item이면(이건 거의 문서의 끝
                                tItem=true;						//따라서 이때 모든 정보를 화면에 뿌려주면 된다.
                                view_text();					//뿌려주는 곳~
                            } if(xpp.getName().equals("tm")){	//이건 발표시각정보니까 1번만나오므로 바로 뿌려주자
                            tTm=true;
                            view_text();
                        } if(xpp.getName().equals("category")){	//이것도 동네정보라 바로 뿌려주면 됨
                            tCategory=true;
                            view_text();
                        } if(xpp.getName().equals("data")){	//data태그는 예보시각기준 예보정보가 하나씩이다.
                            data++;							//즉 data태그 == 예보 개수 그러므로 이때 array를 증가해주자
                        }
                            break;
                    }
                    eventType=xpp.next();	//이건 다음 이벤트로~
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }

        /**
         * 이 부분이 뿌려주는곳
         * 뿌리는건 핸들러가~
         * @author Ans
         */
        private void view_text(){

            handler.post(new Runnable() {	//기본 핸들러니깐 handler.post하면됨

                @Override
                public void run() {

                    if(tCategory){	//동네이름 들어왔다
//                        text.setText(text1.getText()+"지역:"+sCategory+"\n");
                        text0.setText(text0.getText()+sCategory+"\n");              //지역
                        tCategory=false;
                    }if(tTm){		//발표시각 들어왔다
//                        text.setText(text1.getText()+"발표시각:"+sTm+"\n\n");
                        StringBuilder sb = new StringBuilder(sTm); //객체 생성

                        //문자열 끝에 추가
                        sb.append(" 발표");

                        sb.insert(4, ".");//4번째 문자 뒤에 .를 삽입
                        sb.insert(7, ".");
                        sb.insert(10, " ");
                        sb.insert(13, ":");

                        text0.setText(text0.getText()+sb.toString()+"\n\n");
                        tTm=false;
                    } if(tItem){		//문서를 다 읽었다
//                        text0.setText(text0.getText()+sTemp[0].substring(0,2)+"º\n\n");	//현재(+3시간) 온도

                        //글자 크기
                        text0.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        text3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        text4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);


                        int flag = 0;
                        for(int i=0;i<data;i++){	//array로 되어있으니 for문으로
                            if(sDay[i]!=null){		//이건 null integer 에러 예방을 위해(String은 null이 가능하지만intger는 안되니깐)
                                if(Integer.parseInt(sDay[i])==0){	//발표시각이 0이면 오늘
//                                  text.setText(text.getText()+"날짜:"+"오늘"+"\n");
                                    text1.setText(text1.getText()+"오늘"+" ");
                                }else if(Integer.parseInt(sDay[i])==1){	//1이면 내일
//                                    text.setText(text.getText()+"날짜:"+"내일"+"\n");
                                    text1.setText(text1.getText()+"내일"+" ");
                                }else if(Integer.parseInt(sDay[i])==2){	//2이면 모레
//                                    text.setText(text.getText()+"날짜:"+"모레"+"\n");
                                    text1.setText(text1.getText()+"모레"+" ");
                                }
                            }

//                            if(!sTmn[i].equals("-999.0") && !sTmx[i].equals("-999.0")){ // 최저, 최고기온 값이 있을 때
////                                flag++; // 초기값 0. 최저, 최고기온 값이 있을 때 증가
////                                if (flag == 1){ // 한번만 출력
//                                text0.setText(text0.getText()+sTmn[i].substring(0,2)+"º / ");
//                                text0.setText(text0.getText()+sTmx[i].substring(0,2)+"º\n\n");
////                                }
//                            }

                            text1.setText(text1.getText()+sHour[i]+"시\n\n\n");			        //예보시간
                            text3.setText(text3.getText()+sWfKor[i]+"\n\n\n");			        //날씨
                            text4.setText(text4.getText()+sTemp[i].substring(0,2)+"º\n\n\n");	//온도

                            switch (sWfKor[i]){
                                case "맑음":
//                                    text2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sun_1, 0, 0, 0);
                                    break;
                                case "구름많음":
//                                    text2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sun, 0, 0, 0);
                                    break;
                                case "흐림":
//                                    text2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cloud_black_24dp, 0, 0, 0);
                                    break;
                                case "비":
//                                    text2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rain, 0, 0, 0);
//
                                    break;
                                default :
                                    break;
                            }

//                            text.setText(text.getText()+"예보시간:"+sHour[i]+"시\n");			//예보시간
//                            text.setText(text.getText()+"현재시간온도:"+sTemp[i]+"도"+"\n");	//온도
//                            text.setText(text.getText()+"풍향:"+sWdKor[i]+"풍"+"\n");			//풍향
//                            text.setText(text.getText()+"습도:"+sReh[i]+"%"+"\n");			//습도
//                            text.setText(text.getText()+"날씨:"+sWfKor[i]+"\n\n\n");			//날씨
                            //날씨
                        }
                        tItem=false;
                        data=0;		//다음에 날씨를 더가져오게 되면 처음부터 저장해야겠지?

                    }


                }
            });
        }
    }



}
