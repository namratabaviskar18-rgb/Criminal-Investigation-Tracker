package com.complaintmonitor;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class PostComplaintActivity extends AppCompatActivity {

    EditText etSubject, etDescription;
    Button btnSubmit;
    ProgressBar progressBar;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_complaint);

        SharedPreferences prefs = getSharedPreferences("ComplaintApp", MODE_PRIVATE);
        userId = prefs.getInt("UserID", 0);

        etSubject = findViewById(R.id.etSubject);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        btnSubmit.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (subject.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill subject and description", Toast.LENGTH_SHORT).show();
                return;
            }
            new PostTask().execute(String.valueOf(userId), subject, desc);
        });
    }

    private class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... p) {
            try {
                JSONObject json = new JSONObject();
                json.put("UserID", Integer.parseInt(p[0]));
                json.put("Subject", p[1]);
                json.put("Description", p[2]);
                return ApiClient.post("complaint/post", json.toString());
            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            try {
                JSONObject obj = new JSONObject(result);
                Toast.makeText(PostComplaintActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                if (obj.getBoolean("success")) {
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(PostComplaintActivity.this, "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        }
    }
}
