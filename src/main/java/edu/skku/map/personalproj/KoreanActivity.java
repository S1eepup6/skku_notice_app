package edu.skku.map.personalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KoreanActivity extends AppCompatActivity {

    private TextView content ;
    private String URL = "https://www.skku.edu/skku/campus/skk_comm/notice01.do";

    protected void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);

        outstate.putString("url", URL);
        outstate.putString("cont", content.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korean);

        content = (TextView)findViewById(R.id.content_kor);
        content.setMovementMethod(new ScrollingMovementMethod());

        boolean get_web = true;
        if(savedInstanceState != null)
        {
            URL = savedInstanceState.getString("url");
            content.setText(savedInstanceState.getString("cont"));
            if(!content.getText().toString().equals("Content"))
                get_web = false;
        }
        else {
            Intent intent = getIntent();
            URL = intent.getStringExtra("URL");
        }

        if(get_web) {
            Log.d("asdf", URL);
            content.setMovementMethod(new ScrollingMovementMethod());

            getWebsite();
        }

        TextView change_lang = (TextView)findViewById(R.id.title_kor);
        change_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(KoreanActivity.this, EngActivity.class);
                intent.putExtra("Kor_content", content.getText().toString());
                intent.putExtra("baseURL", URL);
                startActivity(intent);
            }
        });

    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect(URL).get();
                    String title = doc.title();
                    Elements links = doc.select("pre[class='pre']");

                    builder.append(title).append("\n");

                    for(Element link : links)
                    {
                        builder.append("\n").append(link.text());
                    }
                }catch (IOException E){
                    ;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        content.setText(builder.toString());
                    }
                });
            }
        }).start();
    }
}
