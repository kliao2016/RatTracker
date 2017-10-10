package edu.gatech.cs2340.rattracker.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

    Query query = FirebaseDatabase.getInstance().getReference()
            .child("reports")
            .limitToLast(25);

    FirebaseRecyclerOptions<RatReport> options =
            new FirebaseRecyclerOptions.Builder<RatReport>()
                    .setQuery(query, RatReport.class).build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

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
        }


        @Override
        public void onClick(View view) {

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