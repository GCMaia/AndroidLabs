package com.example.gabri.androidlabs;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatWindow extends Activity {
    final ArrayList<String> conversation = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        final ListView chatListView = findViewById(R.id.listView);
        final EditText chatEdit = findViewById(R.id.chatText);
        final Button button = findViewById(R.id.chatButton);



        class ChatAdapter extends ArrayAdapter<String>{

            public ChatAdapter(Context context){
                super(context, 0);
            }

            public  int getCount(){
                return conversation.size();
            }

            public String getItem(int position){
                return conversation.get(position);

            }

            public View getView(int position, View convertView, ViewGroup parent){
                LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
                View result = null;
                if (position%2==0){
                    result = inflater.inflate(R.layout.chat_row_incoming, null);
                } else {
                    result = inflater.inflate(R.layout.chat_row_outgoing, null);
                }

                TextView message = result.findViewById(R.id.message_text);
                message.setText(getItem(position));
                return result;
            }

            public long getItemId(int position){
                return position;
            }
        }

        final ChatAdapter messageAdapter = new ChatAdapter(this);
        chatListView.setAdapter(messageAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversation.add(chatEdit.getText().toString());
                messageAdapter.notifyDataSetChanged();
                chatEdit.setText("");
            }
        });




    }










}

