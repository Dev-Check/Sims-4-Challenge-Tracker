package com.dvincisc349.ihatethisshit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChallengeDetailActivity extends AppCompatActivity {

    private TextView challengeNameTextView;
    private TextView generationTextView;
    private ListView goalsListView;
    private ImageButton backToMainButton, backToPreviousGenerationButton, forwardToNextGenerationButton;

    private int currentGenerationIndex = 0;
    private JSONArray generationsArray;
    private String challengeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Initialize views
        challengeNameTextView = findViewById(R.id.challenge_name);
        generationTextView = findViewById(R.id.generation);
        goalsListView = findViewById(R.id.goals_list);
        backToMainButton = findViewById(R.id.back_to_main_button);
        backToPreviousGenerationButton = findViewById(R.id.back_to_previous_generation_button);
        forwardToNextGenerationButton = findViewById(R.id.forward_to_next_generation_button);

        // Retrieve data from intent
        Intent intent = getIntent();
        challengeName = intent.getStringExtra("challengeName"); // Retrieve challenge name
        String generationsJson = intent.getStringExtra("generationsJson");
        currentGenerationIndex = intent.getIntExtra("currentGenerationIndex", 0); // Retrieve the current generation index

        try {
            generationsArray = new JSONArray(generationsJson);
            challengeNameTextView.setText(challengeName);
            updateGenerationData();
        } catch (JSONException e) {
            e.printStackTrace();
            generationTextView.setText("Error parsing generations data");
        }

        // Back to Main Menu
        backToMainButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ChallengeDetailActivity.this, MainActivity.class);
            backIntent.putExtra("currentGenerationIndex", currentGenerationIndex);  // Save current generation index
            startActivity(backIntent);
            finish();
        });

        // Back to Previous Generation
        backToPreviousGenerationButton.setOnClickListener(v -> {
            if (currentGenerationIndex > 0) {
                currentGenerationIndex--;
                updateGenerationData();
            }
        });

        // Forward to Next Generation
        forwardToNextGenerationButton.setOnClickListener(v -> {
            if (currentGenerationIndex < generationsArray.length() - 1) {
                currentGenerationIndex++;
                updateGenerationData();
            }
        });
    }

    private void updateGenerationData() {
        try {
            JSONObject currentGeneration = generationsArray.getJSONObject(currentGenerationIndex);
            generationTextView.setText("Generation " + (currentGenerationIndex + 1) + ": \n"
                    + "Aspiration: " + currentGeneration.optString("aspiration", "Unknown Aspiration") + "\n"
                    + "Career: " + currentGeneration.optString("career", "Unknown Career") + "\n"
                    + "Traits: " + currentGeneration.optString("traits", "Unknown Traits"));

            JSONArray goalsArray = currentGeneration.optJSONArray("goals");
            ArrayList<GoalItem> goalItems = new ArrayList<>();

            for (int i = 0; i < goalsArray.length(); i++) {
                JSONObject goalObject = goalsArray.getJSONObject(i);
                String text = goalObject.optString("text", "Unknown Goal");
                boolean completed = goalObject.optBoolean("completed", false);

                // Add the goal item using challengeName, and generation data
                goalItems.add(new GoalItem(text, completed, challengeName, currentGenerationIndex + 1));
            }

            GoalAdapter adapter = new GoalAdapter(this, goalItems);
            goalsListView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            generationTextView.setText("Error displaying generation data");
        }
    }
}
