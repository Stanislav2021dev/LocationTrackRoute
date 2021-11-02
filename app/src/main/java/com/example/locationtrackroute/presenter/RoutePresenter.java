package com.example.locationtrackroute.presenter;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.locationtrackroute.view.App;
import com.example.locationtrackroute.view.CalendarActivity;
import com.example.locationtrackroute.view.LogInInterface;
import com.example.locationtrackroute.view.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import moxy.MvpPresenter;

public class RoutePresenter extends MvpPresenter<LogInInterface> {

    private final HashMap<String,LatLng> locationList = new HashMap<>();


    public void start(String date,String timeStart, String timeEnd) {

        timeStart= timeStart.replace(":","")+"00";
        timeEnd=timeEnd.replace(":","")+"00";
        Log.v("Data", "startPresenter" + "date " + date
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

                        if (task.isSuccessful()) {
                            Log.v("Data", "Success");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.v("Data", document.getId() + " => " + document.getData());
                             //   Log.v("Data", "Time " + document.getData().get("time"));

                                locationList.put(document.getData().get("time").toString(),
                                        toLatLng(document.getData().get("location").toString()));
                            }

                        }
                        else {
                            Log.v("Data", "Error getting documents: ", task.getException());
                        }
                        Intent intent = new Intent(App.getContext(), MapsActivity.class);
                        intent.putExtra("LocationList",locationList);
                        App.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


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


