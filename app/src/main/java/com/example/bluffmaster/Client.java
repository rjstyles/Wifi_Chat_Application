package com.example.bluffmaster;

/**
 * Created by rajeev on 13/3/17.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Client extends AppCompatActivity {

    EditText message;
    ListView message_list;
    Thread m_objThreadClient;
    Socket clientSocket;
    String IP, u_name;
    ArrayList<com.example.bluffmaster.Message> messages = new ArrayList<com.example.bluffmaster.Message>();
    CustomAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        message = (EditText) findViewById(R.id.enter_message);
        message_list = (ListView) findViewById(R.id.message_list);
        arrayAdapter = new CustomAdapter(this, messages);
        message_list.setAdapter(arrayAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            u_name = extras.getString("Name");
            IP = extras.getString("IP");
        }

        connectToServer();
        addMessage();
    }

    public void connectToServer() {
        m_objThreadClient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(IP, 8080);
                }
                catch (Exception e) {
                    Message msg3 = Message.obtain();
                    msg3.obj = e.getMessage();
                    mHandler.sendMessage(msg3);
                }
            }
        });
        m_objThreadClient.start();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            com.example.bluffmaster.Message recvMessage = new com.example.bluffmaster.Message(msg.obj.toString(), false, timeStamp);

            messages.add(recvMessage);
            arrayAdapter.notifyDataSetChanged();
        }
    };

    public void sendClientMessage(View view) {
        try {
            String msg = message.getText().toString().trim();
            if(msg.length() > 0) {
                String timeStamp = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                com.example.bluffmaster.Message clientMessage = new com.example.bluffmaster.Message(msg, true, timeStamp);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject(msg);

                messages.add(clientMessage);
                arrayAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
                message.setText("");
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
    }

    public void addMessage() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    String msg = null;
                    try {
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                        msg = (String) ois.readObject();
                    } catch (Exception e) { }

                    if(msg != null) {
                        Message new_msg = Message.obtain();
                        new_msg.obj = msg;
                        mHandler.sendMessage(new_msg);
                        scrollMyListViewToBottom();
                    }
                    try {
                        synchronized (this) {
                            wait(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    public void clearText(View view) {
        message.setText("");
    }

    private void scrollMyListViewToBottom() {
        message_list.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                message_list.setSelection(arrayAdapter.getCount() - 1);
            }
        });
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
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}