package com.example.bluffmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText user_name, server_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_name = (EditText) findViewById(R.id.user_name);
        server_name =  (EditText) findViewById(R.id.server_name);;
    }

    public void startChatServer(View view) {
        Intent i = new Intent(this, Server.class);
        String u_name = user_name.getText().toString();
        String server = server_name.getText().toString();
        i.putExtra("Name", u_name);
        i.putExtra("ServerName", server);

        startActivity(i);
    }

    public void connectToChatServer(View view) {

        Intent i = new Intent(this, FindServers.class);
        String u_name = user_name.getText().toString();
        i.putExtra("Name", u_name);
        startActivity(i);
    }

    public void clearText(View view) {
        user_name.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
