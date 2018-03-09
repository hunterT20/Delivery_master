package com.thanhtuan.delivery.ui.detail;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.AppConst;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;
import com.thanhtuan.delivery.data.model.ItemChuaGiao;
import com.thanhtuan.delivery.data.model.RoutePoint;
import com.thanhtuan.delivery.data.model.Steps;
import com.thanhtuan.delivery.data.model.map.Distance;
import com.thanhtuan.delivery.data.model.map.Duration;
import com.thanhtuan.delivery.data.model.map.Leg;
import com.thanhtuan.delivery.data.model.map.LocationMap;
import com.thanhtuan.delivery.data.model.map.Map;
import com.thanhtuan.delivery.data.model.map.Point;
import com.thanhtuan.delivery.data.model.map.RouteMap;
import com.thanhtuan.delivery.data.model.map.StepMap;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.interface_delivery.Interface_Location;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.mapView)    MapView mMapView;
    @BindView(R.id.Direction)    SwipeSelector swipeSelector;
    @BindView(R.id.txtvTime)    TextView txtvTime;
    @BindView(R.id.btnDirection)    Button btnDirection;
    @BindView(R.id.LnLTotal)    CardView cardView;

    private GoogleMap googleMap;
    private List<Polyline> polylines;
    private LatLng start;

    public static final String TAG = MapsFragment.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient client;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mMapView.onCreate(savedInstanceState);

        requestLocationPermissions();

        if (isPlayServicesAvailable()) {
            setUpLocationClientIfNeeded();
            buildLocationRequest();
        }

        return view;
    }

    /**
     * Kiểm tra quyền LocationMap
     */
    private void requestLocationPermissions() {
        if (getActivity() == null) return;
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

    /**
     * Kiểm tra Google play service có sẵn sàng chưa
     * @return true/false
     */
    private boolean isPlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())
                == ConnectionResult.SUCCESS;
    }

    /**
     * Kiểm tra GPS đã bật chưa
     * @return true/false
     */
    private boolean isGpsOn() {
        assert getActivity() != null;
        LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Khởi tạo LocationMap Client
     */
    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (client == null) {
            client = LocationServices.getFusedLocationProviderClient(getActivity());
        }
    }

    /**
     * Cấu hình chất lượng request của Map
     */
    private void buildLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    /**
     * callback trả về Last LocationMap
     */
    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mLastLocation = locationResult.getLastLocation();
        }
    };

    protected void startLocationUpdates() {
        if (getActivity() == null) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.requestLocationUpdates(mLocationRequest, callback, null);
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
        if (getActivity() == null) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                if (!isGpsOn()) {
                    Toast.makeText(getActivity(), "Bạn chưa mở GPS! Mở GPS để xác định địa điểm chính xác!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLastLocation = location;
                startLocationUpdates();

                initData();
                initGoogleMap();
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

    private void initData() {
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
                Log.e(TAG, "onRoutingFailure: " + e.getMessage());
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

            getLocationSale(routePoint -> {
                int current = SharePreferenceUtil.getValueDirection(getActivity());
                initSwipeItem(routePoint, current);

                if (current == -1) {
                /*set textview tổng quãng đường và thời gian cần đi*/
                    if (getActivity() == null) return;
                    SharePreferenceUtil.setValueDistance(getActivity(), routePoint.getTotalDistance());
                    SharePreferenceUtil.setValueTime(getActivity(), routePoint.getTotalDuration());

                    txtvTime.setText(
                            "Quãng đường: " + routePoint.getTotalDistance() +
                                    "- Thời gian: " + routePoint.getTotalDuration()
                    );
                /*set color cho cả đoạn đường*/
                    getPolyline("#FFFF7700", routePoint.getOverviewPolyline());
                /*event khi click vào button Direction*/
                    btnDirection.setOnClickListener(v -> {
                    /*set visibility cho view direction*/
                        cardView.setVisibility(View.GONE);
                        swipeSelector.setVisibility(View.VISIBLE);

                    /*set color cho đoạn đường đầu tiên đươc load lên*/
                        getPolyline("#BABABA", routePoint.getOverviewPolyline());
                        getPolyline("#FFFF7700", routePoint.getStepsArrayList().get(0).getPolyline());
                        updateCamera(routePoint.getStepsArrayList().get(0).getStartLocation());
                    });
                } else {
                /*set visibility cho view direction*/
                    cardView.setVisibility(View.GONE);
                    swipeSelector.setVisibility(View.VISIBLE);

                /*set color cho đoạn đường đầu tiên đươc load lên*/
                    getPolyline("#BABABA", routePoint.getOverviewPolyline());
                    getPolyline("#FFFF7700", routePoint.getStepsArrayList().get(current).getPolyline());
                    updateCamera(routePoint.getStepsArrayList().get(current).getStartLocation());
                }
            });
        }

        @Override
        public void onRoutingCancelled() {
            Log.i("MAP", "Routing was cancelled.");
        }
    };

    private void getLocationSale(final Interface_Location interface_location) {
        if (getActivity() == null) return;
        ItemChuaGiao itemChuaGiao = SharePreferenceUtil.getValueSaleItem(getActivity());

        assert mLastLocation != null;
        LocationMap origin = new LocationMap(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Observable<Map> setupMap = ApiUtils.getAPIMap().setupMap(
          origin.getOrigin(),itemChuaGiao.getAddress(),"vi",AppConst.KEY_MAP
        );
        Disposable disposableMap =
                setupMap.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Map>() {
                            @Override
                            public void onNext(Map map) {
                                if (map.getStatus().equals("OK")) {
                                    RouteMap routes = map.getRoutes().get(0);

                                    Leg legs = routes.getLegs().get(0);
                                    Point overviewPolyline = routes.getOverviewPolyline();
                                    Distance distance = legs.getDistance();
                                    Duration duration = legs.getDuration();
                                    LocationMap endLocation = legs.getEndLocation();
                                    List<StepMap> stepMapList = legs.getSteps();

                                    LatLng end = new LatLng(endLocation.getLat(), endLocation.getLng());

                                    RoutePoint routePoint = new RoutePoint();
                                    routePoint.setTotalDistance(distance.getText());
                                    routePoint.setTotalDuration(duration.getText());
                                    routePoint.setOverviewPolyline(PolyUtil.decode(overviewPolyline.getPoints()));
                                    routePoint.setLatLng(end);

                                    ArrayList<Steps> stepsArrayList = new ArrayList<>();
                                    for (int i = 0; i < stepMapList.size(); i++) {
                                        LatLng latlngStart = new LatLng(stepMapList.get(i).getStartLocation().getLat(),
                                                stepMapList.get(i).getStartLocation().getLng());
                                        LatLng latLng_end = new LatLng(stepMapList.get(i).getEndLocation().getLat(),
                                                stepMapList.get(i).getEndLocation().getLng());

                                        String LINE = stepMapList.get(i).getPolyline().getPoints();
                                        List<LatLng> decodedPath = PolyUtil.decode(LINE);


                                        Steps steps = new Steps();
                                        steps.setDistance(stepMapList.get(i).getDistance().getText());
                                        steps.setDuration(stepMapList.get(i).getDuration().getText());
                                        steps.setHtmlInstructions(Jsoup.parse(stepMapList.get(i).getHtmlInstructions()).text());
                                        steps.setPolyline(decodedPath);
                                        steps.setStartLocation(latlngStart);
                                        steps.setEndLocation(latLng_end);

                                        stepsArrayList.add(steps);
                                    }

                                    routePoint.setStepsArrayList(stepsArrayList);

                                    interface_location.onLocation(routePoint);
                                } else {
                                    if (getActivity() != null) {
                                        txtvTime.setText("Không Tìm thấy địa chỉ!");
                                        SharePreferenceUtil.setValueDistance(getActivity(), "");
                                        SharePreferenceUtil.setValueTime(getActivity(), "");
                                        btnDirection.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        disposable.add(disposableMap);
    }

    public void route() {
        if (mLastLocation == null) return;
        start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        getLocationSale(route_point -> getEnd(route_point.getLatLng()));
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

    private void getPolyline(String color, List<LatLng> overviewPolyline) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.parseColor(color));
        polyOptions.width(22);
        polyOptions.addAll(overviewPolyline);

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

    private void initSwipeItem(final RoutePoint routePoint, int current) {
        /*danh sách các direction*/
        SwipeItem[] swipeItems = new SwipeItem[routePoint.getStepsArrayList().size()];
        for (int i = 0; i < routePoint.getStepsArrayList().size(); i++) {
            Steps step = routePoint.getStepsArrayList().get(i);
            swipeItems[i] = new SwipeItem(i, step.getDistance() + " - " + step.getDuration(), step.getHtmlInstructions());
        }
        /*Khởi tạo swipe Direction*/
        swipeSelector.setItems(
                swipeItems
        );

        if (current != -1) {
            swipeSelector.selectItemAt(current);
        }

        /*set Item selected của swipe*/
        swipeSelector.setOnItemSelectedListener(item -> {
            int current1 = (int) item.value;
            removePoly();

            getPolyline("#BABABA", routePoint.getOverviewPolyline());
            getPolyline("#FFFF7700", routePoint.getStepsArrayList().get(current1).getPolyline());
            updateCamera(routePoint.getStepsArrayList().get(current1).getStartLocation());

            SharePreferenceUtil.setValueDirection(getActivity(), current1);
        });
    }

    private void initGoogleMap() {
        mMapView.getMapAsync(mMap -> {
            if (getActivity() == null) return;

            googleMap = mMap;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);

            if (mLastLocation != null) {
                start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }

            getLocationSale(
                    route_point ->
                            googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(route_point.getLatLng())
                                            .title("Điểm cuối")
                                            .snippet("Giao hàng cho khách")
                            )
            );

            CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(15).tilt(45).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            route();
        });
    }
}
