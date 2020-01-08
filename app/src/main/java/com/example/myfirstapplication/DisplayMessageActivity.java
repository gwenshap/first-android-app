package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class DisplayMessageActivity extends AppCompatActivity {

    public static final String TAG = "MyTag";

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String message = "";

        // Capture the layout's TextView and set the string as its text
        final TextView textView = findViewById(R.id.textView);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                message = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        } else {
            // Handle other intents, such as being started from the home screen
            message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        }

        if (message == null) {
            textView.setText("No message sent - either no message or a type that we don't handle like an image or video");
            return;
        }

        // Instantiate the RequestQueue.
        String url ="https://8apenlf7fe.execute-api.us-east-1.amazonaws.com/default/infuDroidBackend";
        queue = Volley.newRequestQueue(this);

        /*
           {
              "TableName": "DROID_DATA",
              "Item": {
                "ID" : "001",
                "TXT" : "Hello World"
              }
            }
         */

        JSONObject payload = new JSONObject();
        try {
            payload.put("TableName","DROID_DATA")
                    .put("Item", new JSONObject().put("ID", UUID.randomUUID())
                    .put("TXT",message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(url,
                payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        textView.setText("Response is: "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });

        request.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(request);

        textView.setText("Message: " + message + " was sent");
    }


    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
