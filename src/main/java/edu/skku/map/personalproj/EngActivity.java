package edu.skku.map.personalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class EngActivity extends AppCompatActivity {

    private String clientId = "GaTzsELCOUvuCOA8HlZM";
    private String clientSecret = "xnpKCXzOeG";

    private String response = null;
    private String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
    private String baseURL = "";
    String text;
    String will_encode = "";
    TextView content;

    protected void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);

        outstate.putString("KorContent", will_encode);
        outstate.putString("EngContent", content.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eng);

        content = (TextView) findViewById(R.id.content_eng);
        content.setMovementMethod(new ScrollingMovementMethod());

        boolean call_papago = true;
        if(savedInstanceState != null)
        {
            will_encode = savedInstanceState.getString("KorContent");
            content.setText(savedInstanceState.getString("EngContent"));

            if(!(content.getText().toString().equals("Content")))
                call_papago = false;
        }
        else
        {
            Intent intent = getIntent();
            will_encode = intent.getStringExtra("Kor_content");
            baseURL = intent.getStringExtra("baseURL");
        }

        if(call_papago)
        {
            try {
                text = URLEncoder.encode(will_encode, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.d("asdf", "Encode Failed");
            }

            final Map<String, String> requests = new HashMap<>();
            requests.put("X-Naver-Client-Secret", clientSecret);
            requests.put("X-Naver-Client-Id", clientId);
            requests.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(apiURL);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        String postParams = "source=ko&target=en&text=" + text;
                        try {
                            con.setRequestMethod("POST");
                            for(Map.Entry<String, String> header : requests.entrySet()) {
                                con.setRequestProperty(header.getKey(), header.getValue());
                                Log.d("asdf", header.getKey() + ' ' + header.getValue());
                            }

                            con.setDoOutput(true);
                            Log.d("asdf", con.getOutputStream().toString());
                            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                                wr.write(postParams.getBytes());
                                wr.flush();
                            }

                            int responseCode = con.getResponseCode();
                            Log.d("asdf", Integer.toString(responseCode));
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                response =  readBody(con.getInputStream());
                            } else {
                                response = readBody(con.getErrorStream());
                            }
                        } catch (IOException e) {
                            Log.d("asdf", "API request & response Failed");
                            throw new RuntimeException("API request & response Failed", e);
                        } finally {
                            con.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            content.setText(response);
                        }
                    });
                }
            }).start();

        }

        TextView change_lang = (TextView)findViewById(R.id.title_eng);
        change_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(EngActivity.this, KoreanActivity.class);
                intent.putExtra("URL", baseURL);
                startActivity(intent);
            }
        });
    }

    private static String readBody(InputStream body){
        InputStreamReader inputstream = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(inputstream)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            JSONObject mArray = new JSONObject(responseBody.toString());
            String res = mArray.getJSONObject("message").getJSONObject("result").getString("translatedText");

            return res;
        } catch (IOException | JSONException e) {
            return "Naver API done today, sorry.";
        }
    }
}
