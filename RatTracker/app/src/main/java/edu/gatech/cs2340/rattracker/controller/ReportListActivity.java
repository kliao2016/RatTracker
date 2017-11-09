package edu.gatech.cs2340.rattracker.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by Spencer on 10/9/2017.
 */

public class ReportListActivity extends AppCompatActivity {

    private static final int NUMQUERIES = 25;

    Query query = FirebaseDatabase.getInstance().getReference()
            .child("reports")
            .limitToLast(NUMQUERIES);

    FirebaseRecyclerOptions<RatReport> options =
            new FirebaseRecyclerOptions.Builder<RatReport>()
                    .setQuery(query, RatReport.class).build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        FloatingActionButton reportListFab = (FloatingActionButton) findViewById(R.id.report_list_fab);
        reportListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addReportIntent = new Intent(ReportListActivity.this, AddReport.class);
                startActivity(addReportIntent);
            }
        });

        this.getWindow().setTitle("Reports");

        RecyclerView recyclerReportView = findViewById(R.id.report_recyclerview);
        recyclerReportView.setAdapter(adapter);
    }

    FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<RatReport, ReportViewHolder>(options) {
        @Override
        public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.report_list_content, parent, false);

            return new ReportViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(ReportViewHolder holder, int position, RatReport model) {
            holder.report = (RatReport) adapter.getItem(position);

            holder.leftText.setText("" + holder.report.getDateCreated());
            holder.rightText.setText("" + holder.report.getBorough());


        }
    };

    public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RatReport report;
        private TextView leftText;
        private TextView rightText;
        private View view;

        public ReportViewHolder(View view) {
            super(view);
            this.view = view;
            leftText = (TextView) view.findViewById(R.id.dataOneText);
            rightText = (TextView) view.findViewById(R.id.dataTwoText);
            leftText.setPadding(20, 20, 20, 20);
            rightText.setPadding(20, 20, 20, 20);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, ReportDetailsActivity.class);
            intent.putExtra("RatReport", report);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}