package chh24.uw.edu.phishapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import chh24.uw.edu.phishapp.utils.PushReceiver;
import chh24.uw.edu.phishapp.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private String mJwToken;
    private String mEmail;
    private static final String TAG = "CHAT_FRAG";

    private static final String CHAT_ID = "1";

    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private PushMessageReceiver mPushMessageReciever;
    private String mSendUrl;



    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mEmail = getArguments().getString(getString(R.string.enter_email));
            mJwToken = getArguments().getString((getString(R.string.keys_intent_jwt)));
            Log.e("jwtoken: ", " " + mJwToken);
        }

        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        mMessageOutputTextView = rootLayout.findViewById(R.id.text_chat_message_display);
        mMessageInputEditText = rootLayout.findViewById(R.id.edit_chat_message_input);
        rootLayout.findViewById(R.id.button_chat_send).setOnClickListener(this::handleSendClick);

        return rootLayout;
    }

    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();

        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", CHAT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);

            if(res.has("success")  && res.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");

                //its up to you to decide if you want to send the message to the output here
                //or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {

                String sender = intent.getStringExtra("SENDER");
                String messageText = intent.getStringExtra("MESSAGE");

                mMessageOutputTextView.append(sender + ":" + messageText);
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }


}
