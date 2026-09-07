package com.complaintmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            new LoginTask().execute(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject json = new JSONObject();
                json.put("Username", params[0]);
                json.put("Password", params[1]);
                return ApiClient.post("auth/login", json.toString());
            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.getBoolean("success")) {
                    JSONObject user = obj.getJSONObject("user");

                    // Save user session
                    SharedPreferences prefs = getSharedPreferences("ComplaintApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("UserID", user.getInt("userID"));
                    editor.putString("FullName", user.getString("fullName"));
                    editor.putString("Username", user.getString("username"));
                    editor.putString("Role", user.getString("role"));
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Server error. Check connection.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
