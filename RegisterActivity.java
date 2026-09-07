package com.complaintmonitor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etUsername, etPassword, etEmail, etPhone;
    Button btnRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Name, Username and Password are required", Toast.LENGTH_SHORT).show();
                return;
            }
            new RegisterTask().execute(name, user, pass, email, phone);
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... p) {
            try {
                JSONObject json = new JSONObject();
                json.put("FullName", p[0]);
                json.put("Username", p[1]);
                json.put("Password", p[2]);
                json.put("Email", p[3]);
                json.put("Phone", p[4]);
                return ApiClient.post("auth/register", json.toString());
            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            try {
                JSONObject obj = new JSONObject(result);
                Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                if (obj.getBoolean("success")) {
                    finish(); // go back to login
                }
            } catch (Exception e) {
                Toast.makeText(RegisterActivity.this, "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        }
    }
}
