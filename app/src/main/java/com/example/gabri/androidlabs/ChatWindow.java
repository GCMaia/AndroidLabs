package com.example.gabri.androidlabs;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class ChatWindow extends Activity {
    final ArrayList<String> conversation = new ArrayList<>();
    public static final String ACTIVITY_NAME = "ChatWindow";

    private ChatDatabaseHelper chatDatabaseHelper;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        final ListView chatListView = findViewById(R.id.listView);
        final EditText chatEdit = findViewById(R.id.chatText);
        final Button button = findViewById(R.id.chatButton);


        final ChatAdapter messageAdapter = new ChatAdapter(this);
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
            conversation.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
        }
        cursor.close();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversation.add(chatEdit.getText().toString());
                writeToDB(chatEdit.getText().toString());
                ChatAdapter messageAdapter = new ChatAdapter(ChatWindow.this);
                messageAdapter.notifyDataSetChanged();
                chatEdit.setText("");
            }
        });


    }


    private void writeToDB(String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, message);
        long id = chatDatabaseHelper.getWritableDatabase().insert(ChatDatabaseHelper.TABLE_NAME, null, contentValues);
        cursor = chatDatabaseHelper.getWritableDatabase().query(ChatDatabaseHelper.TABLE_NAME,
                new String[]{ChatDatabaseHelper.KEY_MESSAGE}, ChatDatabaseHelper.KEY_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }


    class ChatAdapter extends ArrayAdapter<String> {


        private ChatAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return conversation.size();
        }

        public String getItem(int position) {
            return conversation.get(position);

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if (position % 2 == 0) {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            } else {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }

            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }

        public long getItemId(int position) {
            return position;
        }
    }


    @Override
    protected void onDestroy() {
        chatDatabaseHelper.close();
        super.onDestroy();
    }

    class ChatDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_TAG_HELPER = "ChatDatabaseHelper";
        private static final String DATABASE_NAME = "Messages.db";
        private static final int VERSION_NUM =3;
        static final String KEY_ID = "_id";
        static final String KEY_MESSAGE = "message";
        static final String TABLE_NAME = "CONVERSATION";


        ChatDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(DATABASE_TAG_HELPER, "Calling onCreate");

            db.execSQL( "CREATE TABLE " + TABLE_NAME +
                    " ( " +  KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_MESSAGE + " text);" );
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(DATABASE_TAG_HELPER, "oldVersion=" + oldVersion + " newVersion=" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.i(DATABASE_TAG_HELPER, "oldVersion=" + oldVersion + " newVersion=" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }


    }
}

