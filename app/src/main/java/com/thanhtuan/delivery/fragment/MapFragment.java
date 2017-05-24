package com.thanhtuan.delivery.fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.api.ApiHelper;
import com.thanhtuan.delivery.api.VolleySingleton;
import com.thanhtuan.delivery.interface_delivery.Interface_Location;
import com.thanhtuan.delivery.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.orientation;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    MapView mMapView;
    private GoogleMap googleMap;
    double longitudeCurrent, latitudeCurrent, longitudeSale, latitudeSale;
    Location location;
    private List<Polyline> polylines;
    LatLng start;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        polylines = new ArrayList<>();

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

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                getCurrentLocation();

                LatLng start = new LatLng(latitudeCurrent, longitudeCurrent);
                /*googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));*/
                getLocationSale(new Interface_Location() {
                    @Override
                    public void onLocation(LatLng end) {
                        googleMap.addMarker(new MarkerOptions().position(end).title("Điểm cuối").snippet("Giao hàng cho khách"));
                    }
                });

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(start).zoom(18).bearing(orientation).tilt(75).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        route();
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
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        longitudeCurrent = location.getLongitude();
        latitudeCurrent = location.getLatitude();
    }

    private void getLocationSale(final Interface_Location interface_location) {Gson gson = new Gson();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("MyPre",MODE_PRIVATE);
        String json = mPrefs.getString("SaleItem", "");
        Item item = gson.fromJson(json, Item.class);
        String address = item.getAddress();
        try {
            address = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String key = "AIzaSyCueeDritXwUW37E3jH897o9iBHyIMpseE";
        String API_MAP = ApiHelper.URL_MAP + ApiHelper.DOMAIN_MAP + "address=" + address + "&key=" + key;
        Log.e("MAP", API_MAP);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_MAP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("status").equals("OK")) {
                                JSONArray results = response.getJSONArray("results");
                                JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                latitudeSale = location.getDouble("lat");
                                longitudeSale = location.getDouble("lng");
                                LatLng end = new LatLng(latitudeSale,longitudeSale);

                                interface_location.onLocation(end);
                            }else {
                                Toast.makeText(getActivity(), "Không tìm thấy địa chỉ!", Toast.LENGTH_SHORT).show();
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

    public void route()
    {
        getCurrentLocation();
        start = new LatLng(latitudeCurrent,longitudeCurrent);
        getLocationSale(new Interface_Location() {
            @Override
            public void onLocation(LatLng end) {
                getEnd(end);
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
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        int i = 0;
        //In case of more than 5 alternative routes
        int colorIndex = i % COLORS.length;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(COLORS[colorIndex]));
        polyOptions.width(25);
        polyOptions.addAll(route.get(i).getPoints());
        Polyline polyline = googleMap.addPolyline(polyOptions);
        polylines.add(polyline);

        Toast.makeText(getActivity(),"Thông tin: " +"Quãng đường: "+ route.get(i).getDistanceValue()/1000
                + "km - Thời gian: "+ route.get(i).getDurationValue()/60 + " phút",Toast.LENGTH_LONG).show();
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
}
