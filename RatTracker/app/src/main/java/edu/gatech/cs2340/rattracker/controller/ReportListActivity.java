package edu.gatech.cs2340.rattracker.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.src.main.java.com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * Created by Spencer on 10/9/2017.
 */

public class ReportListActivity {

    Query query = FirebaseDatabase.getInstance().getReference()
            .child("reports")
            .limitToLast(25);

    FirebaseRecyclerOptions<RatReport> options = //documentation used this but it's not in the API
            new FirebaseRecyclerOptions.Builder<RatReport>()
            .setQuery(query, RatReport.class).
            build();

    FirebaseRecyclerAdapter adapter = new FireBaseRecyclerAdapter<RatReport, ReportHolder>(options) {
        @Override
        public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.key, parent, false);

            return new ReportHolder(view);
        }

        @Override
        protected void onBindViewHolder(ReportHolder holder, int position, RatReport model) {

        }
    }
}
