package com.example.locationtrackroute.view;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.TreeMap;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

public interface MapInterface extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void buildRoute (TreeMap<String, LatLng> locationList);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void makeToast (String message);
}
