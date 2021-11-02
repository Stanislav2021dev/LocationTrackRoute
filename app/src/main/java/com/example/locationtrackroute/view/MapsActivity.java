package com.example.locationtrackroute.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.locationtrackroute.R;
import com.example.locationtrackroute.databinding.ActivityMapsBinding;
import com.example.locationtrackroute.presenter.RoutePresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static boolean firstCreate = true;
    private HashMap<String, LatLng> locationList;
    private TreeMap<String,LatLng> locationSorted;
   // private ArrayList<LatLng> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button showCalendar=findViewById(R.id.but);

        if (!firstCreate){
            getCoordinatesList();
        }
        firstCreate=false;

        showCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this,CalendarActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng kyiv = new LatLng(50.45, 30.55);
        mMap.addMarker(new MarkerOptions().position(kyiv).title("Marker in Kyiv"));
        if (locationSorted!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kyiv));
          //  mMap.moveCamera(CameraUpdateFactory.newLatLng(locationList.get()));
        }

        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kyiv));
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        PolylineOptions polylineOptions = new PolylineOptions();

        if (locationSorted!=null) {

            for (String key : locationSorted.keySet()) {

                Log.v("Data", "Time " +  key + "  Location " + (locationSorted.get(key)) );
               // mMap.addMarker(new MarkerOptions().position(locationSorted.get(key)));
                polylineOptions.add(locationSorted.get(key));

            }
        }
        mMap.addPolyline(polylineOptions);
    }

    public void getCoordinatesList(){
        Bundle bundle = getIntent().getExtras();
       // locationList= bundle.getParcelableArrayList("LocationList");

        locationList = (HashMap<String, LatLng>)  getIntent().getSerializableExtra("LocationList");
        locationSorted=new TreeMap<>(locationList);
            Log.v("Data","size" + locationList.size());
    }
}
