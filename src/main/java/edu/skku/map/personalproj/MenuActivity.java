package edu.skku.map.personalproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MenuActivity extends AppCompatActivity {

    private String BASIC_SKKU_NOTICE;
    private String SKKU_NOTICE;
    private String username;
    private String key;
    private ListView listview;
    private EditText edit_keyword;
    private ArrayList<AnnounceItem> items;
    private Button search_btn;

    private Boolean init_click;

    private DatabaseReference mPostRef;

    protected void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);

        outstate.putString("keyword", edit_keyword.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mPostRef = FirebaseDatabase.getInstance().getReference();

        edit_keyword = (EditText)findViewById(R.id.edit_keyword);

        if(savedInstanceState != null)
        {
            edit_keyword.setText(savedInstanceState.getString("keyword"));
        }

        init_click = true;

        BASIC_SKKU_NOTICE = "https://www.skku.edu/skku/campus/skk_comm/notice01.do";
        listview = (ListView)findViewById(R.id.listview);
        search_btn = (Button)findViewById(R.id.search_btn);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username").toString();

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                key = edit_keyword.getText().toString();
                SKKU_NOTICE = "https://www.skku.edu/skku/campus/skk_comm/notice01.do?mode=list&srCategoryId1=&srSearchKey=&srSearchVal=" + key;
                getWebsite();

                postFirebaseDatabase();
            }
        });

        getFirebaseDatabase();
    }

    public void postFirebaseDatabase()
    {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        FirebasePost post = new FirebasePost(key);
        postValues = post.toMap();
        childUpdates.put("/" + username + "/", postValues);
        mPostRef.updateChildren(childUpdates);
    }


    public void getFirebaseDatabase()
    {
        final ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                String info = "";
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    info = (String) postSnapshot.getValue();
                }
                edit_keyword.setText(info);

                if(init_click == true)  {
                    search_btn.performClick();
                    init_click = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){ ; }
        };

        mPostRef.child("/" + username + "/").addValueEventListener(postListener);
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                items = new ArrayList<AnnounceItem>();
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect(SKKU_NOTICE).get();
                    Elements links = doc.select("a[class=\"\"]");

                    int pass = 10;
                    for(Element link : links)
                    {
                        if(pass > 0)
                            pass--;
                        else {
                            String Announce_title = link.text();
                            String URL = BASIC_SKKU_NOTICE + link.attr("href").toString();
                            AnnounceItem tmp = new AnnounceItem(Announce_title, URL);
                            items.add(tmp);
                        }
                    }
                }catch (IOException E){
                    ;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter listAdapter = new ListAdapter(MenuActivity.this, items);
                        listview.setAdapter(listAdapter);

                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                Intent intent = new Intent(MenuActivity.this, KoreanActivity.class);

                                TextView tv_url = (TextView) view.findViewById(R.id.hidden_url);
                                String url = tv_url.getText().toString();

                                intent.putExtra("URL", url);
                                intent.putExtra("Keyword", edit_keyword.getText().toString());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
