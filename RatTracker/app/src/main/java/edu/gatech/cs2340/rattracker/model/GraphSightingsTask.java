package edu.gatech.cs2340.rattracker.model;

/**
 * Created by Kyle Suter on 11/13/2017.
 */

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Inner class that is an async task that loads rat sighting data before being displayed
 */
public class GraphSightingsTask extends AsyncTask<Void, Void, Map<String, RatReport>> {
    private final Map<String, RatReport> asyncMap = new HashMap<>();

    private final Query databaseRef = FirebaseDatabase.getInstance()
            .getReference()
            .child("reports");

    @Override
    protected Map<String, RatReport> doInBackground(Void... voids) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ratSnapshot: dataSnapshot.getChildren()) {
                    RatReport ratReport = ratSnapshot.getValue(RatReport.class);
                    if (ratReport != null) {
                        asyncMap.put(ratSnapshot.getKey(), ratReport);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return asyncMap;
    }
}