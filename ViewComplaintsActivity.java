package com.complaintmonitor;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class ViewComplaintsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> complaintList = new ArrayList<>();
    int userId;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaints);

        SharedPreferences prefs = getSharedPreferences("ComplaintApp", MODE_PRIVATE);
        userId = prefs.getInt("UserID", 0);
        role = prefs.getString("Role", "User");

        listView = findViewById(R.id.listView);
        new LoadComplaintsTask().execute();
    }

    private class LoadComplaintsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if ("Admin".equals(role)) {
                return ApiClient.get("complaint/all");
            } else {
                return ApiClient.get("complaint/my/" + userId);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.getBoolean("success")) {
                    JSONArray arr = obj.getJSONArray("data");
                    complaintList.clear();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        String item = "ID: " + c.getInt("complaintID") + "\n" +
                                "Subject: " + c.getString("subject") + "\n" +
                                "Status: " + c.getString("status") + "\n" +
                                "Description: " + c.getString("description") + "\n";

                        if (!c.isNull("adminReply") && !c.getString("adminReply").isEmpty()) {
                            item += "Admin Reply: " + c.getString("adminReply") + "\n";
                        } else {
                            item += "Admin Reply: (Waiting for reply)\n";
                        }
                        item += "-----------------------------";
                        complaintList.add(item);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ViewComplaintsActivity.this,
                            android.R.layout.simple_list_item_1,
                            complaintList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewComplaintsActivity.this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(ViewComplaintsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
