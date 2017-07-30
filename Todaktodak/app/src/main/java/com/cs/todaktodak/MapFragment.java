package com.cs.todaktodak;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final double SEOUL_LAT = 37.5449546;
    private static final double SEOUL_LNG = 126.9647997;
    private static final LatLng Seoul = new LatLng(SEOUL_LAT, SEOUL_LNG);


    private GoogleApiClient mGoogleApiClient = null;
    GoogleMap mGoogleMap = null;
    private MapView mapView = null;
    private Marker currentMarker = null;

    private static final LatLng DEFAULT_LOCATION = new LatLng(35.154046, 129.032505);
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 3000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 3000; // 1초

    boolean askPermissionOnceAgain = false;
    MainActivity mainActivity = null;

    public static String JsonURL = "https://raw.githubusercontent.com/the1994/todaktodak/master/hospitals.json";

    int hospitalNum = 0;

    ArrayList<PetHospital> petHospitals = new ArrayList<PetHospital>();
    ArrayList<String> originalAddress = new ArrayList<String>();
    ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

    ConnectionClass connectionClass;

    BitmapDescriptor icon;

    String t;


    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView1");
        View layout = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);
        return layout;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mapView.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
//
//    @Override
//    public void onStop() {
//        Log.d(TAG, "onStop");
//        super.onStop();
//        mapView.onStop();
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.d(TAG, "onResume");

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        }

        t = ((MainActivity) getActivity()).getAddress();
        if (t != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationFromAddress(getContext(), t), 15));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        Log.d(TAG, "onPause");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }

//    @Override
//    public void onDestroy() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.unregisterConnectionCallbacks(this);
//            mGoogleApiClient.unregisterConnectionFailedListener(this);
//
//            if (mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi
//                        .removeLocationUpdates(mGoogleApiClient, this);
//                mGoogleApiClient.disconnect();
//            }
//        }
//
//        super.onDestroy();
//        mapView.onLowMemory();
//    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        Log.i(TAG, "setCurrentLocation");
        if (currentMarker != null) currentMarker.remove();
        if (location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            return;
        }
        this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 12));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        mGoogleMap = map;
        setCurrentLocation(null, "위치정보 가져올 수 없음", "위치 퍼미션과 GPS 활성 요부 확인하세요");

        mGoogleMap.getUiSettings().setCompassEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
            } else {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        } else {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }

        connectionClass = new ConnectionClass();
        connectionClass.execute(JsonURL);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i(TAG, "onRequestPremissionResut");
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionAccepted) {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                checkPermissions();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(UPDATE_INTERVAL_MS);
//        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                mGoogleMap.getUiSettings().setCompassEnabled(true);
                //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        } else {
            Log.d(TAG, "onConnected : call FusedLocationApi");
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);
        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 여부 확인하세요");
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        //Log.d(TAG, "checkPermissions");
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());
        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet);
    }

    public String getCurrentAddress(Location location) {
        // 지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        Log.d(TAG, "showDialogForPermission");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {
        Log.d(TAG, "showDialogForPermissionSetting");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                askPermissionOnceAgain = true;
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getActivity().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //finish();
            }
        });
        builder.create().show();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        Toast.makeText(getActivity(), "잠시만", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                mGoogleMap.setMyLocationEnabled(true);
                            }
                        } else mGoogleMap.setMyLocationEnabled(true);
                        return;
                    }
                } else {
                    setCurrentLocation(null, "위치정보 가져올 수 없음", "위치 퍼미션과 GPS 활성 요부 확인하세요");
                }
                break;
        }
    }

    // 주소를 위도 경도로
    public LatLng getLocationFromAddress(Context context, String inputtedAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng resLatLng = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(inputtedAddress, 5);
            if (address == null) {
                return null;
            }

            if (address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return resLatLng;
    }

    // JSON 받아오기
    private class ConnectionClass extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            Log.i(TAG, "doInBackground");

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
            Log.i(TAG, "onPostExecute");
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("petHospital");
                hospitalNum = ja.length();
                int num = ja.length();

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject ho = ja.getJSONObject(i);

                    String name = ho.getString("name");
                    String address = ho.getString("location");
                    String phone = ho.getString("phone");
                    String time = ho.getString("time");
                    String night = ho.getString("night");
                    String web = ho.getString("homepage");

                    // 콤마 이후는 제외
                    StringTokenizer tokens = new StringTokenizer(address);
                    String searchAddress = tokens.nextToken("(");

                    petHospitals.add(i, new PetHospital(name, searchAddress, phone, num, time, night, web));
                    originalAddress.add(i, address);
                    //latLngs.add(i, getLocationFromAddress(getActivity(), searchAddress));
                }
                markeradder();
                notify();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 마커 추가하기
    public synchronized void markeradder() {
        Log.i(TAG, "markeradder()");
        Toast.makeText(getActivity(), "잠시만 기다려주세요!", Toast.LENGTH_LONG).show();
        // 클러스터 매니저를 생성
        ClusterManager<MarkerItem> mClusterManager = new ClusterManager<>(getActivity(), mGoogleMap);
        mGoogleMap.setOnCameraChangeListener(mClusterManager);

        String name;
        String address;
        String night;

        for (int i = 0; i < petHospitals.size(); i++) {
            name = petHospitals.get(i).getName();
            address = petHospitals.get(i).getAddress();
            night = petHospitals.get(i).getNight();

            // 마커 이미지
            switch (night) {
                case "T":
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.footmarker2);
                    break;
                case "F":
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.footmarker1);
                    break;
            }

            List<Address> addr;

            LatLng latLng = getLocationFromAddress(getActivity(), address);
            try {
                mClusterManager.addItem(new MarkerItem(icon, latLng, address, name, String.valueOf(i)));
                mClusterManager.setRenderer(new OwnRendring(getActivity(), mGoogleMap, mClusterManager));
            } catch (Exception e) {
            }
        }

        // InfoWindow 클릭 시 Dialog 출력
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                View customView = (View) View.inflate(getActivity(), R.layout.custom_dialog_material, null);

                TextView tvAddress = (TextView) customView.findViewById(R.id.tv_address);
                TextView tvPhone = (TextView) customView.findViewById(R.id.tv_phone);
                TextView tvTime = (TextView) customView.findViewById(R.id.tv_time);
                TextView tvWeb = (TextView) customView.findViewById(R.id.tv_web);

                int getNum = Integer.parseInt(marker.getSnippet());

                String shospitalName = petHospitals.get(getNum).getName();
                String shospitalLocation = petHospitals.get(getNum).getAddress();
                final String shospitalPhone = petHospitals.get(getNum).getPhone();
                String night = petHospitals.get(getNum).getNight();
                String time = petHospitals.get(getNum).getTime();
                String web = petHospitals.get(getNum).getHomepage();

                tvAddress.setText(originalAddress.get(getNum));

                if (shospitalPhone.equals("null")) {
                    tvPhone.setText("정보없음");
                } else tvPhone.setText(shospitalPhone);

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
                new MaterialStyledDialog.Builder(getActivity())
                        .setTitle(shospitalName)
                        .setCustomView(customView)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setHeaderColor(headerColor)
                        .setCancelable(true)
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .setPositiveText("전화걸기")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Uri uri = Uri.parse("tel:" + shospitalPhone);
                                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            View v;
            @Override
            public View getInfoWindow(Marker marker) {
                // Getting view from the layout file info_window_layout
                if(marker.getTitle() != null) {
                     v = (View) View.inflate(getActivity(), R.layout.custom_infowindow, null);

                    TextView tvName = (TextView) v.findViewById(R.id.tv_name);
                    // setting hospital Name
                    tvName.setText(marker.getTitle());
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    // 클러스터 렌더링(마커 아이콘, 타이틀, 스니펫 등 지정)
    public class OwnRendring extends DefaultClusterRenderer<MarkerItem> {

        public OwnRendring(Context context, GoogleMap map,
                           ClusterManager<MarkerItem> clusterManager) {
            super(context, map, clusterManager);
        }

        protected void onBeforeClusterItemRendered(MarkerItem item, MarkerOptions markerOptions) {
            markerOptions.icon(item.getIcon());
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            markerOptions.infoWindowAnchor(1f, 0f);
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

}