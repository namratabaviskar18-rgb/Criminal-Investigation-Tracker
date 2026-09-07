package com.complaintmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnPostComplaint, btnMyComplaints, btnLogout;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        prefs = getSharedPreferences("ComplaintApp", MODE_PRIVATE);
        String fullName = prefs.getString("FullName", "User");
        String role = prefs.getString("Role", "User");

        tvWelcome = findViewById(R.id.tvWelcome);
        btnPostComplaint = findViewById(R.id.btnPostComplaint);
        btnMyComplaints = findViewById(R.id.btnMyComplaints);
        btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + fullName + " (" + role + ")");

        btnPostComplaint.setOnClickListener(v -> {
            startActivity(new Intent(this, PostComplaintActivity.class));
        });

        btnMyComplaints.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewComplaintsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
