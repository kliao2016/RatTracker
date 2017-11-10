package edu.gatech.cs2340.rattracker.model;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import edu.gatech.cs2340.rattracker.R;

/**
 * Created by Kevin Liao on 11/9/17.
 */

public class LoadSightingsTask extends
        AsyncTask<Map<String, RatReport>, Void, Void> {

    private final int REPORTS = 300;

    private final Query DATABASE = FirebaseDatabase.getInstance()
            .getReference()
            .child("reports")
            .limitToLast(REPORTS);

    @Override
    protected void onPreExecute() {
        progress = findViewById(R.id.rat_map_progress_bar);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Map<String, RatReport>[] maps) {
        final Map<String, RatReport> asyncMap = maps[0];
        if (asyncMap != null) {
            DATABASE.addValueEventListener(new ValueEventListener() {
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<String, RatReport> stringRatReportMap) {
        progress.setVisibility(View.GONE);
    }
}
