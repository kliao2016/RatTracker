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
 * Controller class associated with ReportList view.
 */
public class ReportListActivity extends AppCompatActivity {

    private static final int NUMQUERIES = 25;

    private final Query query = FirebaseDatabase.getInstance().getReference()
            .child("reports")
            .limitToLast(NUMQUERIES);

    private final FirebaseRecyclerOptions<RatReport> options =
            new FirebaseRecyclerOptions.Builder<RatReport>()
                    .setQuery(query, RatReport.class).build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        FloatingActionButton reportListFab = findViewById(R.id.report_list_fab);
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

    private final FirebaseRecyclerAdapter adapter
            = new FirebaseRecyclerAdapter<RatReport, ReportViewHolder>(options) {
        @Override
        public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.report_list_content, parent, false);

            return new ReportViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(ReportViewHolder holder, int position, RatReport model) {
            holder.report = (RatReport) adapter.getItem(position);

            holder.leftText.setText(holder.report.getDateCreated());
            holder.rightText.setText(holder.report.getBorough());
        }
    };

    public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RatReport report;
        private final TextView leftText;
        private final TextView rightText;
        private final int pad = 20;

        /**
         * Constructor for the ReportViewHolder.
         * @param view the layout to be used for the ReportViewHolder
         */
        public ReportViewHolder(View view) {
            super(view);
            leftText = view.findViewById(R.id.dataOneText);
            rightText = view.findViewById(R.id.dataTwoText);
            leftText.setPadding(pad, pad, pad, pad);
            rightText.setPadding(pad, pad, pad, pad);
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