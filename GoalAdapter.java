package com.dvincisc349.ihatethisshit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GoalAdapter extends ArrayAdapter<GoalItem> {

    private Context context;

    public GoalAdapter(Context context, ArrayList<GoalItem> goals) {
        super(context, 0, goals);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoalItem goalItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goal_list_item, parent, false);
        }

        TextView goalText = convertView.findViewById(R.id.goal_text);
        CheckBox goalCheckbox = convertView.findViewById(R.id.goal_checkbox);

        goalText.setText(goalItem.getText());

        goalCheckbox.setOnCheckedChangeListener(null); // prevent recycling glitch
        goalCheckbox.setChecked(goalItem.isChecked());

        goalCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            goalItem.setChecked(isChecked);
            updateGoalCompletion(goalItem, isChecked);
        });

        return convertView;
    }

    private void updateGoalCompletion(GoalItem goalItem, boolean isChecked) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("challengeName", goalItem.getChallengeName()); // dynamic!
            requestData.put("generationNumber", goalItem.getGenerationNumber()); // dynamic!
            requestData.put("goalText", goalItem.getText());
            requestData.put("completed", isChecked);

            new UpdateGoalTask(context).execute(requestData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to update goal completion", Toast.LENGTH_SHORT).show();
        }
    }

    private static class UpdateGoalTask extends AsyncTask<String, Void, String> {
        private final Context taskContext;

        public UpdateGoalTask(Context context) {
            this.taskContext = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            try {
                URL url = new URL("http://10.0.2.2:5000/update_goal");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(jsonData.getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                return (responseCode == 200) ? "success" : "error";
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String message = result.equals("success") ? "Goal updated successfully" : "Failed to update goal";
            Toast.makeText(taskContext, message, Toast.LENGTH_SHORT).show();
        }
    }
}
