package edu.skku.map.personalproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private String uname;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.username);
        Button login_btn = (Button)findViewById(R.id.login_button);

        if(savedInstanceState != null)
        {
            username.setText(savedInstanceState.getString("uname"));
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                Intent go_menu = new Intent(MainActivity.this, MenuActivity.class);
                go_menu.putExtra("Username", uname);
                startActivity(go_menu);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outstate)
    {
        super.onSaveInstanceState(outstate);

        outstate.putString("uname", username.getText().toString());
    }
}
