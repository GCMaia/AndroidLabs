package com.example.gabri.androidlabs;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class ChatWindow extends Activity {
    ArrayList<Message> conversation = new ArrayList<>();
    public static final String ACTIVITY_NAME = "ChatWindow";
    private ChatDatabaseHelper chatDatabaseHelper;
    ChatAdapter messageAdapter;
    private Cursor cursor;
    private boolean frameLayoutExists;
    EditText chatEdit;
    ListView chatListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        chatListView = findViewById(R.id.listView);
        chatEdit = findViewById(R.id.chatText);
        final Button button = findViewById(R.id.chatButton);

        if(findViewById(R.id.frameLayout)!=null)
            frameLayoutExists=true;
        else
            frameLayoutExists=false;

        messageAdapter = new ChatAdapter(this);
        chatListView.setAdapter(messageAdapter);

        chatDatabaseHelper = new ChatDatabaseHelper(this);
        cursor = chatDatabaseHelper.getReadableDatabase().query(ChatDatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null);

        Log.i(ACTIVITY_NAME, "Cursor’s  column count =" + cursor.getColumnCount());

        for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
            Log.i(ACTIVITY_NAME, "Cursor's column name: " + cursor.getColumnName(idx));
        }

        while (cursor.moveToNext()) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "SQL ID: " + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID)));
            int id = cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID));
            int message =  cursor.getInt(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
            String txt = cursor.getString(message);

            Message msg = new Message(id, txt);
            conversation.add(msg);
        }
        cursor.close();
        conversation = (ArrayList<Message>) chatDatabaseHelper.getAllMessages();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = chatEdit.getText().toString();
                Message ms = chatDatabaseHelper.insertMessage(msgText);
                if(ms!=null){
                    conversation.add(ms);
                    messageAdapter.notifyDataSetChanged();
                }
                chatEdit.setText(null);
            }
        });


        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (frameLayoutExists) {
                    MessageFragment mf = new MessageFragment();
                    Bundle fragmentArgs = new Bundle();
                    fragmentArgs.putLong("ID", id);
                    fragmentArgs.putString("message", conversation.get(position).getMessage());
                    mf.setArguments(fragmentArgs);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.frameLayout, mf);
                    ft.commit();

                } else {
                    Intent i = new Intent(ChatWindow.this, MessageDetails.class);
                    i.putExtra("ID", id);
                    i.putExtra("message", conversation.get(position).getMessage());
                    startActivityForResult(i, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode ==RESULT_OK){
                Long messageID=data.getLongExtra("delete",-1);
                deleteMessage(messageID);
                Log.i(ACTIVITY_NAME,messageID + " is deleted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        chatDatabaseHelper.close();
        super.onDestroy();

    }

    protected  void deleteMessage(long id){
        SQLiteDatabase db = chatDatabaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+ ChatDatabaseHelper.TABLE_NAME+ " WHERE " +chatDatabaseHelper.KEY_ID +" = " +id);
        conversation=new ArrayList<>();
        conversation =(ArrayList<Message>)getAllMessages();
        messageAdapter.notifyDataSetChanged();

    }



    public List<Message> getAllMessages() {

        final List<Message> messages = new ArrayList<>();
        final SQLiteDatabase db = chatDatabaseHelper.getWritableDatabase();

        final Cursor cursor = db.query(ChatDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

        final int cIdIndex = cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID);
        final int cMessageIndex = cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE);

        Log.i(ACTIVITY_NAME, "Coursor column count: " + cursor.getColumnCount());
        Log.i(ACTIVITY_NAME, "Cursor Col: " + cursor.getColumnName(cIdIndex));
        Log.i(ACTIVITY_NAME, "Cursor Column: " + cursor.getColumnName(cMessageIndex));

        this.cursor = cursor;

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cIdIndex);
            String txt = cursor.getString(cMessageIndex);

            final Message msg = new Message(id, txt);
            messages.add(msg);
            Log.i(ACTIVITY_NAME, "SQL message text: " + txt);
        }
        return messages;
    }

    private class ChatAdapter extends ArrayAdapter<Message> {
        private ChatAdapter(Context context) {
            super(context,0);
        }

        @Override
        public int getCount(){
            return conversation.size();
        }

        @Override
        public Message getItem(int position) {
            return conversation.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result;
            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming,null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing,null);
            TextView message = result.findViewById(R.id.message_text);
            message.setText( getItem(position).getMessage());
            return result;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }

    class ChatDatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "ChatDatabaseHelper";
        private static final String DATABASE_NAME = "Messages.db";
        private static final int VERSION_NUM = 2;
        static final String TABLE_NAME = "MessageList";
        static final String KEY_ID = "_id", KEY_MESSAGE = "message";
        protected Cursor c;


        ChatDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Calling onCreate");
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_MESSAGE + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Calling onUpgrade, oldVersion = " + oldVersion + "newVersion = " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);


        }

        public List<Message> getAllMessages() {
            final List<Message> list = new ArrayList<>();
            final SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
            final int cIdIndex = cursor.getColumnIndex(KEY_ID);
            final int cMessageIndex = cursor.getColumnIndex(KEY_MESSAGE);
            Log.i(TAG, "Cursor's column count = " + cursor.getColumnCount());
            Log.i(TAG, "Cursor column1: " + cursor.getColumnName(cIdIndex));
            Log.i(TAG, "Cursor Column2" + cursor.getColumnName(cMessageIndex));
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.i(TAG, "Column name = " + cursor.getColumnName(i));
            }
            while (cursor.moveToNext()) {
                Log.i(TAG, "SQL MESSAGE: " + getMessageFrom(cursor).getMessage());
                list.add(getMessageFrom(cursor));
            }
            return list;
        }

        public Message insertMessage(String msg) {
            final ContentValues cValue = new ContentValues();
            SQLiteDatabase db = this.getWritableDatabase();
            cValue.put(KEY_MESSAGE, msg);
            long searchId = db.insert(TABLE_NAME, null, cValue);
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "= ?", new String[]{String.valueOf(searchId)});
            return cursor.moveToNext() ? getMessageFrom(cursor) : null;

        }

        private Message getMessageFrom(Cursor cursor) {
            final int columnIdIndex = cursor.getColumnIndex(KEY_ID);
            final int columnMessageIndex = cursor.getColumnIndex(KEY_MESSAGE);

            long id = cursor.getLong(columnIdIndex);
            String txt = cursor.getString(columnMessageIndex);

            final Message msg = new Message(id, txt);
            Log.i(TAG, "SQL message text: " + txt);

            return msg;
        }
    }
}

