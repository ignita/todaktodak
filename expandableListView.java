package com.cs.todaktodak;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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
    String JsonURL = "https://rawgit.com/the1994/todaktodak/master/pet.json";
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;
    String[][] hospitalLocation;
    String[] arrProv = new String[]{"남구", "북구", "사상구", "사하구", "서구", "연제구", "중구", "영도구"};


    public ThreeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        // Volley, JSON 받아오기
        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        final ExpandableListView list = (ExpandableListView) view.findViewById(R.id.list_item);


        // Creating the JsonArrayRequest class called arrayreq, passing the required parameters
        //JsonURL is the URL to be fetched from
        JsonArrayRequest arrayreq = new JsonArrayRequest(JsonURL,
                // The second parameter Listener overrides the method onResponse() and passes
                //JSONArray as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Retrieves first JSON object in outer array
                            JSONObject hospitalObj = response.getJSONObject(0);
                            // Retrieves "petHospital" from the JSON object
                            JSONArray hospitalArry = hospitalObj.getJSONArray("petHospital");
                            // Iterates through the JSON Array getting objects and adding them
                            //to the list view until there are no more objects in hospitalArray

                            String[] petHospital = new String[hospitalArry.length()];

                            String[] hospitalName = new String[hospitalArry.length()];

                            int nam = 0;
                            int buk = 0;
                            int sang = 0;
                            int ha = 0;
                            int seo = 0;
                            int yeonje = 0;
                            int jung = 0;
                            int yeongdo = 0;

                            hospitalLocation = new String[8][];
                            for (int i = 0; i < hospitalArry.length(); i++) {
                                //gets each JSON object within the JSON array
                                JSONObject jsonObject = hospitalArry.getJSONObject(i);

                                // "name", "location", "phone"이라는 이름 받아오고
                                // 객체로 만든다
                                String name = jsonObject.getString("name");
                                String location = jsonObject.getString("location");
                                String phone = jsonObject.getString("phone");

                                // 각각 합쳐서 hospital String에 저장
                                //  String hospital = "Number " + (i + 1) + "\n" + "Hospital Name: " + name + "\n" +
                                //          "location: " + location + "\n" + "phone: " + phone;

                                // 합친 String을 배열에 넣는다.
                                // petHospital[i] = hospital;

                                // 병원 이름
                                hospitalName[i] = name;
                                // Log.v("name: ", hospitalName[i]);

                                // Log.v("test", petHospital[i]);


                                if (location.contains("남구")) {
                                    hospitalLocation[0][nam] = name;
                                    nam++;
                                } else if (location.contains("북구")) {
                                    hospitalLocation[1][buk] = name;
                                    buk++;
                                } else if (location.contains("사상구")) {
                                    hospitalLocation[2][sang] = name;
                                    sang++;
                                } else if (location.contains("사하구")) {
                                    hospitalLocation[3][ha] = name;
                                    ha++;
                                } else if (location.contains("서구")) {
                                    hospitalLocation[4][seo] = name;
                                    seo++;
                                } else if (location.contains("연제구")) {
                                    hospitalLocation[5][yeonje] = name;
                                    yeonje++;
                                } else if (location.contains("중구")) {
                                    hospitalLocation[6][jung] = name;
                                    jung++;
                                } else {
                                    hospitalLocation[7][yeongdo] = hospitalName[i];
                                    yeongdo++;
                                }


                            }

                            List<Map<String, String>> provData = new ArrayList<>();
                            List<List<Map<String, String>>> cityData = new ArrayList<>();

                            for (int i = 0; i < arrProv.length; i++) {
                                Map<String, String> prov = new HashMap<>();
                                prov.put("district", arrProv[i]);
                                provData.add(prov);

                                List<Map<String, String>> cityes = new ArrayList<>();
                                for (int j = 0; j < hospitalLocation[i].length; j++) {
                                    Map<String, String> city = new HashMap<>();
                                    city.put("city", hospitalLocation[i][j]);
                                    cityes.add(city);
                                }//데이터
                                cityData.add(cityes);
                            }

                            ExpandableListAdapter adapter = new SimpleExpandableListAdapter(getContext(),
                                    provData, android.R.layout.simple_expandable_list_item_1, new String[]{"district"}, new int[]{android.R.id.text1},
                                    cityData, android.R.layout.simple_expandable_list_item_1, new String[]{"city"}, new int[]{android.R.id.text1});

                            list.setAdapter(adapter);


                            // listView에 표시하기
                            // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, hospitalName);
                            // list.setAdapter(adapter);
                        }
                        // JSON 에러
                        catch (JSONException e) {
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
