package com.thanhtuan.delivery.view.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.AppConst;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.interface_delivery.Interface_Location;
import com.thanhtuan.delivery.model.Route_point;
import com.thanhtuan.delivery.model.Steps;
import com.thanhtuan.delivery.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.Direction)
    SwipeSelector swipeSelector;
    @BindView(R.id.txtvTime)
    TextView txtvTime;
    @BindView(R.id.btnDirection)
    Button btnDirection;
    @BindView(R.id.LnLTotal)
    CardView cardView;

    private GoogleMap googleMap;
    private List<Polyline> polylines;
    private LatLng start;

    public static final String TAG = MapsFragment.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient client;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this,view);
        mMapView.onCreate(savedInstanceState);

        requestLocationPermissions();

        if (isPlayServicesAvailable()) {
            setUpLocationClientIfNeeded();
            buildLocationRequest();
        }

        return view;
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length <= 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissions();
                }
                break;
            default:
                break;
        }
    }

    private boolean isPlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())
                == ConnectionResult.SUCCESS;
    }

    private boolean isGpsOn() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (client == null){
            client = LocationServices.getFusedLocationProviderClient(getActivity());
        }
    }

    private void buildLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    private LocationCallback callback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mLastLocation = locationResult.getLastLocation();
        }
    };

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.requestLocationUpdates(mLocationRequest,callback,null);
    }

    protected void stopLocationUpdates() {
        client.removeLocationUpdates(callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null
                && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (!isGpsOn()){
                        Toast.makeText(getActivity(), "Bạn chưa mở GPS! Mở GPS để xác định địa điểm chính xác!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mLastLocation = location;
                    startLocationUpdates();

                    initData();
                    initGoogleMap();
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initData(){
        polylines = new ArrayList<>();

        int status = SharePreferenceUtil.getValueStatus(getActivity());
        if (status == AppConst.DANG_CHO_GIAO_HANG) {
            SharePreferenceUtil.setValueDirection(getActivity(), -1);
        }

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RoutingListener listener = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            if (getActivity() == null) return;
            if (e != null) {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            removePoly();

            getLocationSale(new Interface_Location() {
                @Override
                public void onLocation(final Route_point route_point) {
                    int current = SharePreferenceUtil.getValueDirection(getActivity());
                    initSwipeItem(route_point, current);

                    if (current == -1) {
                    /*set textview tổng quãng đường và thời gian cần đi*/
                        if (getActivity() == null) return;
                        SharePreferenceUtil.setValueDistance(getActivity(), route_point.getTotalDistance());
                        SharePreferenceUtil.setValueTime(getActivity(),route_point.getTotalDuration());

                        txtvTime.setText(
                                "Quãng đường: " + route_point.getTotalDistance() +
                                        "- Thời gian: " + route_point.getTotalDuration()
                        );
                    /*set color cho cả đoạn đường*/
                        getPolyline("#FFFF7700", route_point.getOverviewPolyline());
                    /*event khi click vào button Direction*/
                        btnDirection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            /*set visibility cho view direction*/
                                cardView.setVisibility(View.GONE);
                                swipeSelector.setVisibility(View.VISIBLE);

                            /*set color cho đoạn đường đầu tiên đươc load lên*/
                                getPolyline("#BABABA", route_point.getOverviewPolyline());
                                getPolyline("#FFFF7700", route_point.getStepsArrayList().get(0).getPolyline());
                                updateCamera(route_point.getStepsArrayList().get(0).getStartLocation());
                            }
                        });
                    } else {
                    /*set visibility cho view direction*/
                        cardView.setVisibility(View.GONE);
                        swipeSelector.setVisibility(View.VISIBLE);

                    /*set color cho đoạn đường đầu tiên đươc load lên*/
                        getPolyline("#BABABA", route_point.getOverviewPolyline());
                        getPolyline("#FFFF7700", route_point.getStepsArrayList().get(current).getPolyline());
                        updateCamera(route_point.getStepsArrayList().get(current).getStartLocation());
                    }
                }
            });
        }

        @Override
        public void onRoutingCancelled() {
            Log.i("MAP", "Routing was cancelled.");
        }
    };

    private void getLocationSale(final Interface_Location interface_location) {
        if (getActivity() == null || mLastLocation == null) {
            return;
        }

        String URL = ApiHelper.ApiMap(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude());
        JsonRequest.Request(getActivity(), null, URL, null, new Response.Listener<JSONObject>() {
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

                        LatLng end = new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng"));

                        Route_point route_point = new Route_point();
                        route_point.setTotalDistance(distance.getString("text"));
                        route_point.setTotalDuration(duration.getString("text"));
                        route_point.setOverviewPolyline(PolyUtil.decode(overview_polyline.getString("points")));
                        route_point.setLatLng(end);

                        ArrayList<Steps> stepsArrayList = new ArrayList<>();
                        for (int i = 0; i < step_list.length(); i++) {
                            LatLng latLng_start = new LatLng(step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lat"),
                                    step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lng"));
                            LatLng latLng_end = new LatLng(step_list.getJSONObject(i).getJSONObject("start_location").getDouble("lat"),
                                    step_list.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));

                            String LINE = step_list.getJSONObject(i).getJSONObject("polyline").getString("points");
                            List<LatLng> decodedPath = PolyUtil.decode(LINE);


                            Steps steps = new Steps();
                            steps.setDistance(step_list.getJSONObject(i).getJSONObject("distance").getString("text"));
                            steps.setDuration(step_list.getJSONObject(i).getJSONObject("duration").getString("text"));
                            steps.setHtmlInstructions(Jsoup.parse(step_list.getJSONObject(i).getString("html_instructions")).text());
                            steps.setPolyline(decodedPath);
                            steps.setStartLocation(latLng_start);
                            steps.setEndLocation(latLng_end);

                            stepsArrayList.add(steps);
                        }

                        route_point.setStepsArrayList(stepsArrayList);

                        interface_location.onLocation(route_point);
                    } else {
                        if (getActivity() != null) {
                            txtvTime.setText("Không Tìm thấy địa chỉ!");
                            SharePreferenceUtil.setValueDistance(getActivity(),"");
                            SharePreferenceUtil.setValueTime(getActivity(),"");
                            btnDirection.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void route() {
        if (mLastLocation == null) return;
        start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        getLocationSale(new Interface_Location() {
            @Override
            public void onLocation(Route_point route_point) {
                getEnd(route_point.getLatLng());
            }
        });
    }

    private void getEnd(LatLng end) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(listener)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    private void updateCamera(LatLng latLng) {
        CameraPosition pos = CameraPosition.builder().target(latLng).zoom(18f).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private void getPolyline(String color, List<LatLng> overview_polyline) {
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

    private void removePoly() {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
    }

    private void initSwipeItem(final Route_point route_point, int current) {
        /*danh sách các direction*/
        SwipeItem[] swipeItems = new SwipeItem[route_point.getStepsArrayList().size()];
        for (int i = 0; i < route_point.getStepsArrayList().size(); i++) {
            Steps steps = route_point.getStepsArrayList().get(i);
            swipeItems[i] = new SwipeItem(i, steps.getDistance() + " - " + steps.getDuration(), steps.getHtmlInstructions());
        }
        /*Khởi tạo swipe Direction*/
        swipeSelector.setItems(
                swipeItems
        );

        if (current != -1) {
            swipeSelector.selectItemAt(current);
        }

        /*set Item selected của swipe*/
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                int current = (int) item.value;
                removePoly();

                getPolyline("#BABABA", route_point.getOverviewPolyline());
                getPolyline("#FFFF7700", route_point.getStepsArrayList().get(current).getPolyline());
                updateCamera(route_point.getStepsArrayList().get(current).getStartLocation());

                SharePreferenceUtil.setValueDirection(getActivity(), current);
            }
        });
    }

    private void initGoogleMap() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                if (getActivity() == null) return;

                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                if (mLastLocation != null){
                    start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }

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
    }

}
