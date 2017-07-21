package com.cs.todaktodak;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class ThreeFragment extends Fragment{

    // Json Data URL
    String JsonURL = "https://rawgit.com/the1994/todaktodak/master/pet.json";
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;
    View dialogView, dialogView_title;


    public ThreeFragment() { }

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

        final ListView list = (ListView) view.findViewById(R.id.list_item);

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
                            final JSONArray hospitalArry = hospitalObj.getJSONArray("petHospital");
                            // Iterates through the JSON Array getting objects and adding them
                            //to the list view until there are no more objects in hospitalArray

                            String[] petHospital = new String[hospitalArry.length()];

                            final String[] hospitalName = new String[hospitalArry.length()];
                            final String[] hospitalLocation = new String[hospitalArry.length()];
                            final String[] hospitalPhone = new String[hospitalArry.length()];


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
                                hospitalLocation[i] = location;
                                hospitalPhone[i] = phone;
                                // Log.v("name: ", hospitalName[i]);

                                //Log.v("test", petHospital[i]);
                            }
                            // listView에 표시하기
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, hospitalName);
                            list.setAdapter(adapter);


                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    String shospitalName = hospitalName[i];
                                    String shospitalLocation = hospitalLocation[i];
                                    final String shospitalPhone = hospitalPhone[i];

                                    final List<String> values = new ArrayList<String>();
                                    values.add("병원명 : " + shospitalName);
                                    values.add("주소 : " + shospitalLocation);
                                    if (shospitalPhone.equals("null")) {
                                    }
                                    else {
                                        values.add("전화번호 : " + hospitalPhone[i]);
                                    }
//                                    final String[] mid = {hospitalName[i]};
//                                    ListView list1 = (ListView) view.findViewById(R.id.list_info);
//                                    final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mid);
//                                    list1.setAdapter(adapter1);

                                    final CharSequence[] value = values.toArray(new String[values.size()]);
                                    //ImageButton img_phone = (ImageButton) view.findViewById(R.id.phone);

                                    dialogView = (View) View.inflate(getActivity(), R.layout.custom_dialog, null);
                                    dialogView_title = (View) View.inflate(getActivity(), R.layout.custom_dialog_title,null);
                                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                                    dlg.setTitle("병원정보");
                                    dlg.setItems(value, null);
                                    dlg.setCustomTitle(dialogView_title);
                                    dlg.setView(dialogView);
                                    dlg.show();
                                    //final AlertDialog dialog = dlg.create();
                                    ImageButton img_phone = (ImageButton) dialogView.findViewById(R.id.phone);
                                    ImageButton img_map = (ImageButton) dialogView.findViewById(R.id.map);

                                    img_phone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Uri uri = Uri.parse("tel:"+shospitalPhone);
                                            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(intent);
                                        }
                                    });
                                    img_map.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(getActivity(), "지도보기", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
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
