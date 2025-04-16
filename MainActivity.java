package com.dvincisc349.ihatethisshit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private final String SERVER_URL = "http://10.0.2.2:5000/challenges"; // replace <your-ip>

    // Store challenges in an ArrayList for later use
    private List<JSONObject> challenges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        fetchChallenges();
    }

    private void fetchChallenges() {
        new Thread(() -> {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                in.close();

                runOnUiThread(() -> parseAndDisplayChallenges(response.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseAndDisplayChallenges(String json) {
        try {
            JSONArray challengesArray = new JSONArray(json);
            List<String> challengeNames = new ArrayList<>();

            Log.d("JSON Response", json);

            // Store challenges and extract names for the list
            for (int i = 0; i < challengesArray.length(); i++) {
                JSONObject challengeObj = challengesArray.getJSONObject(i);
                challenges.add(challengeObj); // Store the challenge object for later use
                String name = challengeObj.getString("name");
                challengeNames.add(name);
            }

            updateListView(challengeNames);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateListView(List<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();  // This ensures the UI gets updated with new data

        // Set up a click listener for each item in the list
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected challenge data
            JSONObject selectedChallenge = challenges.get(position);

            try {
                // Extract the generations array from the selected challenge
                if (selectedChallenge.has("generations")) {
                    JSONArray generationsArray = selectedChallenge.getJSONArray("generations");

                    // Assuming you want to show goals for the first generation (index 0)
                    JSONObject firstGeneration = generationsArray.getJSONObject(0);

                    if (firstGeneration.has("goals")) {
                        JSONArray goalsArray = firstGeneration.getJSONArray("goals");

                        ArrayList<String> goalTexts = new ArrayList<>();

                        // Loop through the goals array and extract only the 'text'
                        for (int i = 0; i < goalsArray.length(); i++) {
                            JSONObject goal = goalsArray.getJSONObject(i);

                            // Safely retrieve the 'text' value
                            String goalText = goal.optString("text", "Unknown Goal");

                            // Add the goalText to the list
                            goalTexts.add(goalText);
                        }

                        // Pass the goalTexts and other data to the next activity
                        Intent intent = new Intent(MainActivity.this, ChallengeDetailActivity.class);
                        intent.putStringArrayListExtra("goalTexts", goalTexts);
                        intent.putExtra("challengeName", selectedChallenge.getString("name"));
                        intent.putExtra("generationsJson", generationsArray.toString()); // Pass the generations data
                        startActivity(intent);
                    } else {
                        Log.w("JSON Error", "No 'goals' field found in the first generation.");
                    }
                } else {
                    Log.w("JSON Error", "No 'generations' field found in the selected challenge.");
                }
            } catch (JSONException e) {
                Log.e("JSON Error", "Error parsing selected challenge data", e);
            }
        });
    }

}
