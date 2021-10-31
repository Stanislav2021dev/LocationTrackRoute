package com.example.locationtrackroute.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtrackroute.R;
import com.example.locationtrackroute.databinding.ActivityCalendarBinding;
import com.example.locationtrackroute.presenter.LogInPresenter;
import com.example.locationtrackroute.presenter.RoutePresenter;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class CalendarActivity extends MvpAppCompatActivity  implements LogInInterface {

    @InjectPresenter
    public RoutePresenter routePresenter;
    private String date;
    private String minTime="00:00";
    private String maxTime="23:59";
    private ActivityCalendarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);

        date=new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        showTimeRange();

        binding.rangeSeekbar.setNotifyWhileDragging(true);
        binding.rangeSeekbar.setRangeValues(0L,60*24-1L);
        binding.rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                minTime=String.valueOf(convertMinutesToHHMM(minValue));
                maxTime = String.valueOf(convertMinutesToHHMM(maxValue));
                Log.v("Date",minTime+ "   " + maxTime);
                showTimeRange();
            }
        });

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = String.format("%4d%02d%02d",year,month+1,dayOfMonth);
            Log.v("Date",date);
        });

        binding.start.setOnClickListener(v -> {
            routePresenter.start(date,minTime,maxTime);
        });
    }

    public void showTimeRange(){
        String time = "From "+ minTime + " to " + maxTime;
        binding.tvSelectTimeInfo.setText(time);
    }

    @SuppressLint("DefaultLocale")
    public String convertMinutesToHHMM(Long timeInMinutes){
        long hours = TimeUnit.MINUTES.toHours(timeInMinutes);
        long minutes = timeInMinutes - TimeUnit.HOURS.toMinutes(hours);
        return String.format("%02d:%02d",  hours, minutes);
    }

    @Override
    public void makeToast(String toast) {

    }

    @Override
    public void onSuccessAuth() {

    }
}
