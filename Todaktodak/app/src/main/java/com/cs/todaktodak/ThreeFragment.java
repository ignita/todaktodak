package com.cs.todaktodak;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class ThreeFragment extends Fragment {

    // Json Data URL
    public static String JsonURL = "https://raw.githubusercontent.com/the1994/todaktodak/master/hospitals.json";

    ArrayList<ArrayList<String>> hospitalAddress = null;
    final String[] arrProv = new String[]{"남구", "북구", "사상구", "사하구", "서구", "연제구", "영도구", "중구"};

    ConnectionClass connectionClass;

    ArrayList<PetHospital> petHospitals = new ArrayList<PetHospital>();

    List<Map<String, String>> provData = new ArrayList<>();
    List<List<Map<String, String>>> cityData = new ArrayList<>();

    ExpandableListView list;


    View dialogView, dialogView_title;


    public ThreeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        connectionClass = new ConnectionClass();
        connectionClass.execute(JsonURL);

        list = (ExpandableListView) view.findViewById(R.id.list_item);

        ExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(),
                provData, android.R.layout.simple_expandable_list_item_1, new String[]{"district"}, new int[]{android.R.id.text1},
                cityData, android.R.layout.simple_expandable_list_item_1, new String[]{"city"}, new int[]{android.R.id.text1});

        // listView에 표시하기
        list.setAdapter(adapter);

        // ExpandableListView는 setOnChildClickListener로 자식 항목 선택
//        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//
//                int getInfo = petHospitals.get(i).getNum();
//                int num = i;
//                int sum = 0;
//
//                while (num > 0) {
//                    sum += hospitalAddress.get(num - 1).size();
//                    num--;
//                }
//                Log.i("sum", Integer.toString(sum));
//
//                String shospitalName = petHospitals.get(getInfo).getName();
//                String shospitalLocation = hospitalLocation[sum + i1];
//                final String shospitalPhone = hospitalPhone[sum + i1];
//
//                final List<String> values = new ArrayList<String>();
//                values.add("병원명 : " + shospitalName);
//                values.add("주소 : " + shospitalLocation);
//                if (shospitalPhone.equals("null")) {
//                } else {
//                    values.add("전화번호 : " + hospitalPhone[sum + i1]);
//                }
//
//
//                final CharSequence[] value = values.toArray(new String[values.size()]);
//
//                dialogView = (View) View.inflate(getActivity(), R.layout.custom_dialog, null);
//                dialogView_title = (View) View.inflate(getActivity(), R.layout.custom_dialog_title, null);
//                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
//                dlg.setTitle("병원정보");
//                dlg.setItems(value, null);
//                dlg.setCustomTitle(dialogView_title);
//                dlg.setView(dialogView);
//                dlg.show();
//                //final AlertDialog dialog = dlg.create();
//
//                ImageButton img_phone = (ImageButton) dialogView.findViewById(R.id.phone);
//                ImageButton img_map = (ImageButton) dialogView.findViewById(R.id.map);
//
//                img_phone.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Uri uri = Uri.parse("tel:" + shospitalPhone);
//                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
//                        startActivity(intent);
//                    }
//                });
//                img_map.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(getActivity(), "지도보기", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//        });

        return view;
    }

    // JSON 받아오기
    private class ConnectionClass extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);

                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();

                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("petHospital");
                int num = 0;

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject ho = ja.getJSONObject(i);

                    String name = ho.getString("name");
                    String address = ho.getString("location");
                    String phone = ho.getString("phone");

                    final String[] hospitalName = new String[ja.length()];
                    final String[] hospitalLocation = new String[ja.length()];
                    final String[] hospitalPhone = new String[ja.length()];

                    num++;

                    // 콤마 이후는 제외
                    StringTokenizer tokens = new StringTokenizer(address);

                    String searchAddress = tokens.nextToken("(");

                    petHospitals.add(i, new PetHospital(name, searchAddress, phone, num));
                }
                listadder();
                notify();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void listadder() {

        hospitalAddress = new ArrayList<ArrayList<String>>();
        ArrayList<String> nam = new ArrayList<>();
        ArrayList<String> buk = new ArrayList<>();
        ArrayList<String> sang = new ArrayList<>();
        ArrayList<String> ha = new ArrayList<>();
        ArrayList<String> seo = new ArrayList<>();
        ArrayList<String> yeonje = new ArrayList<>();
        ArrayList<String> jung = new ArrayList<>();
        ArrayList<String> yeongdo = new ArrayList<>();


        for (int i = 0; i < petHospitals.size(); i++) {

            String name = petHospitals.get(i).getName();
            String address = petHospitals.get(i).getAddress();
            String phone = petHospitals.get(i).getAddress();

            if (address.contains("남구")) {
                nam.add(name);
            } else if (address.contains("북구")) {
                buk.add(name);
            } else if (address.contains("사상구")) {
                sang.add(name);
            } else if (address.contains("사하구")) {
                ha.add(name);
            } else if (address.contains("서구")) {
                seo.add(name);
            } else if (address.contains("연제구")) {
                yeonje.add(name);
            } else if (address.contains("영도구")) {
                yeongdo.add(name);
            } else if (address.contains("중구")) {
                jung.add(name);
            }
        }

        hospitalAddress.add(nam);
        hospitalAddress.add(buk);
        hospitalAddress.add(sang);
        hospitalAddress.add(ha);
        hospitalAddress.add(seo);
        hospitalAddress.add(yeonje);
        hospitalAddress.add(yeongdo);
        hospitalAddress.add(jung);

        for (int i = 0; i < arrProv.length; i++) {
            Map<String, String> prov = new HashMap<>();
            prov.put("district", arrProv[i]);
            provData.add(prov);

            List<Map<String, String>> cityes = new ArrayList<>();
            for (int j = 0; j < hospitalAddress.get(i).size(); j++) {
                Map<String, String> city = new HashMap<>();
                city.put("city", hospitalAddress.get(i).get(j));
                cityes.add(city);
            }
            cityData.add(cityes);
        }




    }
}

