package com.thanhtuan.delivery.fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.interface_delivery.Interface_Location;
import com.thanhtuan.delivery.model.Item;
import com.thanhtuan.delivery.model.Route_point;
import com.thanhtuan.delivery.model.Steps;
import com.thanhtuan.delivery.sharePreference.MyShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @BindView(R.id.mapView) MapView mMapView;
    @BindView(R.id.Direction)    SwipeSelector swipeSelector;
    @BindView(R.id.txtvTime)    TextView txtvTime;
    @BindView(R.id.btnDirection)    Button btnDirection;
    @BindView(R.id.LnLTotal)    LinearLayout linearLayout;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private GoogleMap googleMap;
    private double longitudeCurrent, latitudeCurrent;
    private List<Polyline> polylines;
    private LatLng start;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this,view);

        mMapView.onCreate(savedInstanceState);
        polylines = new ArrayList<>();
        SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
        int status = mPrefs.getInt(MyShare.VALUE_STATUS,0);
        if(status ==0 || status ==3){
            value_current(-1);
        }

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if(getActivity() == null) return;
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }
                }
                googleMap.setMyLocationEnabled(true);
                getCurrentLocation();

                final LatLng start = new LatLng(latitudeCurrent, longitudeCurrent);
                getLocationSale(new Interface_Location() {
                    @Override
                    public void onLocation(Route_point route_point) {
                        googleMap.addMarker(new MarkerOptions().position(route_point.getLatLng()).title("Điểm cuối").snippet("Giao hàng cho khách"));
                    }
                });

                CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(15).tilt(45).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                route();
            }
        });
        return view;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitudeCurrent = location.getLongitude();
            latitudeCurrent = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void getCurrentLocation() {
        if(getActivity() == null){
            return;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location locationCurrent = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (locationCurrent != null){
            longitudeCurrent = locationCurrent.getLongitude();
            latitudeCurrent = locationCurrent.getLatitude();
        }
    }

    private void getLocationSale(final Interface_Location interface_location) {
        Gson gson = new Gson();
        String PARAM1 = "origin=";
        String PARAM2 = "&destination=";
        String PARAM3 = "&language=";
        String PARAM4 = "&key=";

        if(getActivity() == null){
            return;
        }
        SharedPreferences mPrefs = getActivity().getSharedPreferences("MyPre",MODE_PRIVATE);
        String json = mPrefs.getString("SaleItem", "");
        Item item = gson.fromJson(json, Item.class);
        String address = item.getAddress();
        try {
            address = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getCurrentLocation();


        String key = "AIzaSyCueeDritXwUW37E3jH897o9iBHyIMpseE";
        String API_MAP = ApiHelper.URL_MAP + ApiHelper.DOMAIN_MAP + PARAM1 + latitudeCurrent + "," + longitudeCurrent +
                PARAM2 + address + PARAM3 + "vi" + PARAM4 + key;
        Log.e("MAP", API_MAP);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_MAP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("status").equals("OK")) {
                                JSONObject routes = response.getJSONArray("routes").getJSONObject(0);

                                JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
                                JSONObject overview_polyline = routes.getJSONObject("overview_polyline");
                                JSONObject distance = legs.getJSONObject("distance");
                                JSONObject duration = legs.getJSONObject("duration");
                                JSONObject end_location = legs.getJSONObject("end_location");
                                JSONArray step_list = legs.getJSONArray("steps");

                                LatLng end = new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng"));

                                Route_point route_point = new Route_point();
                                route_point.setTotal_distance(distance.getString("text"));
                                route_point.setTotal_duration(duration.getString("text"));
                                route_point.setOverview_polyline(PolyUtil.decode(overview_polyline.getString("points")));
                                route_point.setLatLng(end);

                                ArrayList<Steps> stepsArrayList = new ArrayList<>();
                                for (int i = 0; i < step_list.length(); i++){
                                    LatLng latLng_start = new LatLng(step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lat"),
                                            step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lng"));
                                    LatLng latLng_end = new LatLng(step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lat"),
                                            step_list.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));

                                    String LINE = step_list.getJSONObject(i).getJSONObject("polyline").getString("points");
                                    List<LatLng> decodedPath = PolyUtil.decode(LINE);


                                    Steps steps = new Steps();
                                    steps.setDistance(step_list.getJSONObject(i).getJSONObject("distance").getString("text"));
                                    steps.setDuration(step_list.getJSONObject(i).getJSONObject("duration").getString("text"));
                                    steps.setHtml_instructions(Jsoup.parse(step_list.getJSONObject(i).getString("html_instructions")).text());
                                    steps.setPolyline(decodedPath);
                                    steps.setStart_location(latLng_start);
                                    steps.setEnd_location(latLng_end);

                                    stepsArrayList.add(steps);
                                }

                                route_point.setStepsArrayList(stepsArrayList);

                                interface_location.onLocation(route_point);
                            }else {
                                if (getActivity() != null){
                                    txtvTime.setText("Không Tìm thấy địa chỉ!");
                                    btnDirection.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " + error.getMessage());
            }
        });

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
    }

    public void route(){
        getCurrentLocation();
        start = new LatLng(latitudeCurrent,longitudeCurrent);
        getLocationSale(new Interface_Location() {
            @Override
            public void onLocation(Route_point route_point) {
                getEnd(route_point.getLatLng());
            }
        });
    }

    private void getEnd(LatLng end){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        if(getActivity() == null) return;
        if(e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        removePoly();

        getLocationSale(new Interface_Location() {
            @Override
            public void onLocation(final Route_point route_point) {
                SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
                int current = mPrefs.getInt(MyShare.VALUE_DIRECTION, -1);

                init_SwipeItem(route_point, current);

                Log.e("current", current + "");

                if (current == -1){
                    /*set textview tổng quãng đường và thời gian cần đi*/
                    txtvTime.setText("Quãng đường: " + route_point.getTotal_distance() + "- Thời gian: " + route_point.getTotal_duration());
                    /*set color cho cả đoạn đường*/
                    getPolyline("#FFFF7700",route_point.getOverview_polyline());
                    /*event khi click vào button Direction*/
                    btnDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*set visibility cho view direction*/
                            linearLayout.setVisibility(View.GONE);
                            swipeSelector.setVisibility(View.VISIBLE);

                            /*set color cho đoạn đường đầu tiên đươc load lên*/
                            getPolyline("#BABABA",route_point.getOverview_polyline());
                            getPolyline("#FFFF7700",route_point.getStepsArrayList().get(0).getPolyline());
                            updateCamera(route_point.getStepsArrayList().get(0).getStart_location());
                        }
                    });
                }else {
                    /*set visibility cho view direction*/
                    linearLayout.setVisibility(View.GONE);
                    swipeSelector.setVisibility(View.VISIBLE);

                    /*set color cho đoạn đường đầu tiên đươc load lên*/
                    getPolyline("#BABABA",route_point.getOverview_polyline());
                    getPolyline("#FFFF7700",route_point.getStepsArrayList().get(current).getPolyline());
                    updateCamera(route_point.getStepsArrayList().get(current).getStart_location());
                }
            }
        });
    }

    @Override
    public void onRoutingCancelled() {
        Log.i("MAP", "Routing was cancelled.");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateCamera(LatLng latLng) {
        CameraPosition pos = CameraPosition.builder().target(latLng).zoom(18f).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private void getPolyline(String color, List<LatLng> overview_polyline){
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.parseColor(color));
        polyOptions.width(22);
        polyOptions.addAll(overview_polyline);

        Polyline polyline = googleMap.addPolyline(polyOptions);
        polyline.setGeodesic(true);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polylines.add(polyline);
    }

    private void removePoly(){
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
    }

    private void init_SwipeItem(final Route_point route_point, int current){
        /*danh sách các direction*/
        SwipeItem[] swipeItems = new SwipeItem[route_point.getStepsArrayList().size()];
        for (int i = 0; i < route_point.getStepsArrayList().size(); i++){
            Steps steps = route_point.getStepsArrayList().get(i);
            swipeItems[i] = new SwipeItem(i,steps.getDistance() + " - " + steps.getDuration(),steps.getHtml_instructions());
        }
        /*Khởi tạo swipe Direction*/
        swipeSelector.setItems(
                swipeItems
        );

        if (current != -1){
            swipeSelector.selectItemAt(current);
        }

        /*set Item selected của swipe*/
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                int current = (int) item.value;
                removePoly();

                getPolyline("#BABABA",route_point.getOverview_polyline());
                getPolyline("#FFFF7700",route_point.getStepsArrayList().get(current).getPolyline());
                updateCamera(route_point.getStepsArrayList().get(current).getStart_location());

                value_current(current);
            }
        });
    }

    public void value_current(int current){
        /*Biến Share giữ vị trí đang chỉ đường*/
        SharedPreferences mPrefs = getActivity().getSharedPreferences(MyShare.NAME,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt(MyShare.VALUE_DIRECTION, current);
        prefsEditor.apply();
    }
}