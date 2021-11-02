package com.example.locationtrackroute.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

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
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class MapsActivity extends MvpAppCompatActivity implements OnMapReadyCallback,
        RangeTimePickerDialog.ISelectedTime, MapInterface {

    @InjectPresenter
    public RoutePresenter routePresenter;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Calendar dateAndTime=Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateListener;
    private String currentDate;
    private String chosenDate;
    private final String defaultMinTime="000000";
    private final String defaultMaxTime="235900";
    private String chosenMinTime;
    private String chosenMaxTime;
    private Polyline polyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.v("GetCoordinates","OnCreate");
        currentDate=new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        routePresenter.start(currentDate,defaultMinTime,defaultMaxTime);

        binding.calendarfab.setOnClickListener(v -> setDate(v));
        binding.timefab.setOnClickListener(v -> intitRangeTimeDialog());
        binding.refreshfab.setOnClickListener(v -> updateTrack());

        dateListener= (view, year, monthOfYear, dayOfMonth) -> {
            chosenDate = String.format("%4d%02d%02d",year,monthOfYear+1,dayOfMonth);

            if (chosenMinTime==null || chosenMaxTime ==null) {
                routePresenter.start(chosenDate,defaultMinTime,defaultMaxTime);
            }
            else {
                routePresenter.start(chosenDate,chosenMinTime,chosenMaxTime);
            }

            Log.v("Date","Current date" + chosenDate);
        };
    }

    private void updateTrack() {
        if (chosenDate==null && (chosenMinTime ==null || chosenMaxTime ==null)){
            routePresenter.start(currentDate,defaultMinTime,defaultMaxTime);
        }
        else if (chosenDate==null && (chosenMinTime !=null || chosenMaxTime !=null)){
            routePresenter.start(currentDate,chosenMinTime,chosenMaxTime);
        }
        else if (chosenDate!=null && (chosenMinTime ==null || chosenMaxTime ==null)) {
            routePresenter.start(chosenDate,defaultMinTime,defaultMaxTime);
        }
        else routePresenter.start(chosenDate,chosenMinTime,chosenMaxTime);
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        chosenMinTime=String.valueOf(hourStart)+ minuteStart+"00";
        chosenMaxTime=String.valueOf(hourEnd)+minuteEnd+"00";
        Log.v("Date", "Min " + chosenMinTime+ " Max "+chosenMaxTime);
        if (chosenDate==null){
            routePresenter.start(currentDate,chosenMinTime,chosenMaxTime);
        }
        else {
            routePresenter.start(chosenDate,chosenMinTime,chosenMaxTime);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap = googleMap;
       LatLng kyiv = new LatLng(50.45, 30.55);
       mMap.moveCamera(CameraUpdateFactory.newLatLng(kyiv));
       mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    public void intitRangeTimeDialog(){
        RangeTimePickerDialog rangeTimePickerDialog = new RangeTimePickerDialog();
        rangeTimePickerDialog.newInstance();
        rangeTimePickerDialog.setIs24HourView(true);
        FragmentManager fragmentManager = getFragmentManager();
        rangeTimePickerDialog.show(fragmentManager, "");
    }

    public void setDate(View v) {
        new DatePickerDialog(this, dateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }


    @Override
    public void buildRoute(TreeMap<String, LatLng> locationList) {

        Log.v("Data","size" + locationList.size());
        PolylineOptions polylineOptions = new PolylineOptions();
        if (polyline!=null){
            polyline.remove();
        }

        if (locationList!=null && locationList.keySet().size()!=0) {
            for (String key : locationList.keySet()) {
      //          Log.v("Data", "Time " + key + "  Location " + (locationSorted.get(key)));
                polylineOptions.add(locationList.get(key));
            }

          polyline =  mMap.addPolyline(polylineOptions);
          String firstKey = locationList.firstKey();
          mMap.moveCamera(CameraUpdateFactory.newLatLng(locationList.get(firstKey)));
          mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            Log.v("key", "Key" + firstKey + "coord "+ locationList.get(firstKey));
        }

    }

    @Override
    public void makeToast(String toast) {
        Toast.makeText(MapsActivity.this, toast, Toast.LENGTH_LONG).show();
    }

}
