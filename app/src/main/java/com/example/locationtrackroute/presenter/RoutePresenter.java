package com.example.locationtrackroute.presenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.locationtrackroute.view.MapInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.TreeMap;

import moxy.MvpPresenter;

public class RoutePresenter extends MvpPresenter<MapInterface> {

    private HashMap<String,LatLng> locationList;
    private TreeMap<String,LatLng> locationSorted;

    public void start(String date,String timeStart, String timeEnd) {
        Log.v("GetCoordinates", "startPresenter" + "date " + date
        +" time " + timeStart + " " + timeEnd);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(userId+date)
                .whereGreaterThanOrEqualTo("time",timeStart)
                .whereLessThanOrEqualTo("time", timeEnd)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        locationList = new HashMap<>();
                        if (task.isSuccessful()) {
                            Log.v("Data", "Success");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                locationList.put(document.getData().get("time").toString(),
                                        toLatLng(document.getData().get("location").toString()));
                            }

                        }
                        else {
                            Log.v("Data", "Error getting documents: ", task.getException());
                        }

                            locationSorted=new TreeMap<>(locationList);
                            getViewState().buildRoute(locationSorted);

                        if (locationList.keySet().size()==0) {
                            getViewState().makeToast("No Activities this day");
                        }
                    }
                });
    }

    public static com.google.android.gms.maps.model.LatLng toLatLng(String coordinates){
        String[] latlong = coordinates.split(",");
        double latitude = Double.parseDouble(latlong[0] .replace("{latitude=",""));
        String longitudeStr = latlong[1].replace(" longitude=","");
        double longitude = Double.parseDouble(longitudeStr.replace("}",""));
        return new LatLng(latitude, longitude);
    }
}


