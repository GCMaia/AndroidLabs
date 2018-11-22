package com.example.gabri.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
//*************
public class MessageFragment extends android.app.Fragment {
    private TextView messageContent;
    private TextView messageID;
    private Button deleteButton;
    private Bundle runningBundle;
    private boolean runningOnPhone;
    private Context parent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runningBundle = this.getArguments();
        runningOnPhone = runningBundle.getBoolean("phone");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment,container,false);
        messageContent = result.findViewById(R.id.messageHere);
        messageID = result.findViewById(R.id.idMessageDetails);
        deleteButton = result.findViewById(R.id.buttonMessageDetails);
        String ID = Long.toString(runningBundle.getLong("ID"));
        messageID.setText(ID);
        String msg = runningBundle.getString("message");
        messageContent.setText(msg);
        runningEnv();
        return result;
    }

    public void runningEnv(){
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (runningOnPhone){
                    Intent intent = new Intent(parent,ChatWindow.class);
                    intent.putExtra("delete",runningBundle.getLong("ID"));
                    MessageDetails messageDetails = (MessageDetails)parent;
                    messageDetails.setResult(-1,intent);
                    messageDetails.finish();
                }else{
                    ChatWindow chatWindow = (ChatWindow) parent;
                    chatWindow.deleteMessage(runningBundle.getLong("ID"));
                    getFragmentManager().beginTransaction().remove(MessageFragment.this).commit();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        runningBundle = this.getArguments();
        runningOnPhone = runningBundle.getBoolean("phone");
        parent = context;
    }
}
