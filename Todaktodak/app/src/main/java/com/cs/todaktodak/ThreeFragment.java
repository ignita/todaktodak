package com.cs.todaktodak;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class ThreeFragment extends Fragment {

    // Json Data URL
    String JsonURL = "https://raw.githubusercontent.com/the1994/todaktodak/master/petHospitals.json";
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;

    ArrayList<ArrayList<String>> hospitalAddress = null;
    final String[] arrProv = new String[]{"남구", "북구", "사상구", "사하구", "서구", "연제구", "영도구", "중구"};

    ArrayList<PetHospital> petHospitals = new ArrayList<PetHospital>();
    ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

    List<Map<String, String>> provData = new ArrayList<>();
    List<List<Map<String, String>>> cityData = new ArrayList<>();

    // JSON에 lat, lon이 없을 때
//    // 전체 주소(검색용x)
//    ArrayList<String> originAddress = new ArrayList<String>();
//    // 검색용 주소
//    ArrayList<String> searchAddress = new ArrayList<String>();

    ExpandableListView list;
    MainActivity mainActivity;


    public ThreeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("googlemap", "ThreeFragmentOnCreatView");
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        // Volley, JSON 받아오기
        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.list_item);

        // Creating the JsonArrayRequest class called arrayreq, passing the required parameters
        //JsonURL is the URL to be fetched from
        JsonObjectRequest arrayreq = new JsonObjectRequest(JsonURL,
                // The second parameter Listener overrides the method onResponse() and passes
                //JSONArray as a parameter
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            // Retrieves first JSON object in outer array
//                            JSONObject hospitalObj = response.getJSONObject(0);
//                            // Retrieves "petHospital" from the JSON object
                            final JSONArray hospitalArry = response.getJSONArray("petHospital");
                            // Iterates through the JSON Array getting objects and adding them
                            //to the list view until there are no more objects in hospitalArray

                            // String[] petHospital = new String[hospitalArry.length()];
                            final String[] hospitalName = new String[hospitalArry.length()];
                            final String[] hospitalLocation = new String[hospitalArry.length()];
                            final String[] hospitalPhone = new String[hospitalArry.length()];

                            hospitalAddress = new ArrayList<ArrayList<String>>();
                            ArrayList<String> nam = new ArrayList<>();
                            ArrayList<String> buk = new ArrayList<>();
                            ArrayList<String> sang = new ArrayList<>();
                            ArrayList<String> ha = new ArrayList<>();
                            ArrayList<String> seo = new ArrayList<>();
                            ArrayList<String> yeonje = new ArrayList<>();
                            ArrayList<String> jung = new ArrayList<>();
                            ArrayList<String> yeongdo = new ArrayList<>();


                            int num = 0;
                            for (int i = 0; i < hospitalArry.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = hospitalArry.getJSONObject(i);

                                // "name", "location", "phone"이라는 이름 받아오고
                                // 객체로 만든다
                                String name = jsonObject.getString("name");
                                String location = jsonObject.getString("location");
                                String phone = jsonObject.getString("phone");
                                String time = jsonObject.getString("time");
                                String night = jsonObject.getString("night");
                                String web = jsonObject.getString("homepage");
                                double lat = Double.parseDouble(jsonObject.getString("latitude"));
                                double lon = Double.parseDouble(jsonObject.getString("longitude"));

                                latLngs.add(new LatLng(lat, lon));

                                petHospitals.add(i, new PetHospital(name, location, phone, num, time, night, web, latLngs.get(i)));

                                num++;

                                // JSON에 lat, long이 없을 때
                                // 콤마 이후는 제외
//                                StringTokenizer tokens = new StringTokenizer(location);
//
//                                searchAddress.add(i, tokens.nextToken("("));
//
//                                petHospitals.add(i, new PetHospital(name, searchAddress.get(i), phone, num, time, night, homepage));
//                                originAddress.add(i, location);


                                if (location.contains("남구")) {
                                    nam.add(name);
                                } else if (location.contains("북구")) {
                                    buk.add(name);
                                } else if (location.contains("사상구")) {
                                    sang.add(name);
                                } else if (location.contains("사하구")) {
                                    ha.add(name);
                                } else if (location.contains("서구")) {
                                    seo.add(name);
                                } else if (location.contains("연제구")) {
                                    yeonje.add(name);
                                } else if (location.contains("영도구")) {
                                    yeongdo.add(name);
                                } else if (location.contains("중구")) {
                                    jung.add(name);
                                }
                                // 병원 이름
                                hospitalName[i] = name;
                                hospitalLocation[i] = location;
                                hospitalPhone[i] = phone;
                            }

                            hospitalAddress.add(nam);
                            hospitalAddress.add(buk);
                            hospitalAddress.add(sang);
                            hospitalAddress.add(ha);
                            hospitalAddress.add(seo);
                            hospitalAddress.add(yeonje);
                            hospitalAddress.add(yeongdo);
                            hospitalAddress.add(jung);

                            List<Map<String, String>> provData = new ArrayList<>();
                            List<List<Map<String, String>>> cityData = new ArrayList<>();

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

                            ExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(),
                                    provData, android.R.layout.simple_expandable_list_item_1, new String[]{"district"}, new int[]{android.R.id.text1},
                                    cityData, android.R.layout.simple_expandable_list_item_1, new String[]{"city"}, new int[]{android.R.id.text1});

                            // listView에 표시하기
                            list.setAdapter(adapter);

                            // ExpandableListView는 setOnChildClickListener로 자식 항목 선택
                            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                @Override
                                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                                    final View customView = (View) View.inflate(getActivity(), R.layout.custom_dialog_material_listview, null);

                                    TextView tvAddress = (TextView) customView.findViewById(R.id.tv_address);
                                    TextView tvPhone = (TextView) customView.findViewById(R.id.tv_phone);
                                    TextView tvTime = (TextView) customView.findViewById(R.id.tv_time);
                                    TextView tvWeb = (TextView) customView.findViewById(R.id.tv_web);

                                    int num = i;
                                    int sum = 0;

                                    while (num > 0) {
                                        sum += hospitalAddress.get(num - 1).size();
                                        num--;
                                    }

                                    // 병원 인덱스
                                    final int order = petHospitals.get(sum + i1).getNum();

                                    String name = petHospitals.get(order).getName();
                                    final String address = petHospitals.get(order).getAddress();
                                    final String phone = petHospitals.get(order).getPhone();
                                    String time = petHospitals.get(order).getTime();
                                    String night = petHospitals.get(order).getNight();
                                    String web = petHospitals.get(order).getHomepage();

                                    tvAddress.setText(address);

                                    if (phone.equals("null")) {
                                        tvPhone.setText("정보없음");
                                    } else tvPhone.setText(phone);

                                    tvTime.setText(time);
                                    if (web.equals("null")) {
                                        tvWeb.setText("-");
                                    } else tvWeb.setText(web);

                                    int headerColor = 0;
                                    switch (night) {
                                        case "T":
                                            headerColor = R.color.colorNightTime;
                                            break;
                                        case "F":
                                            headerColor = R.color.colorPrimary;
                                            break;
                                    }

                                    MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(getActivity());
                                    builder.setTitle(name)
                                            .setCustomView(customView)
                                            .setStyle(Style.HEADER_WITH_TITLE)
                                            .setHeaderColor(headerColor)
                                            .setCancelable(true)
                                            .withDialogAnimation(true)
                                            .withDarkerOverlay(true);

                                    final Dialog dialog = builder.show();

                                    ImageButton img_phone = (ImageButton) customView.findViewById(R.id.btnPhone);
                                    ImageButton img_map = (ImageButton) customView.findViewById(R.id.btnMap);

                                    img_phone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Uri uri = Uri.parse("tel:" + phone);
                                            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    });
                                    img_map.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            mainActivity = (MainActivity) getActivity();
                                            mainActivity.viewPager.setCurrentItem(0);

                                            ((MainActivity) getActivity()).putAddress(latLngs.get(order));
//                                            ((MainActivity) getActivity()).putAddress(searchAddress.get(order));
                                        }
                                    });
                                    return false;
                                }
                            });


                        }
                        // JSON 에러
                        catch (
                                JSONException e)

                        {
                            // 에러 발생하면, 로그에 출력
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        // Adds the JSON array request "arrayreq" to the request queue
        requestQueue.add(arrayreq);

        return view;
    }
}