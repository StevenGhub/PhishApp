package chh24.uw.edu.phishapp;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import chh24.uw.edu.phishapp.model.Credentials;
import chh24.uw.edu.phishapp.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnRegisterFragmentInteractionListener mListener;
    private Credentials mCredentials;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button b = (Button) v.findViewById(R.id.doneRegister);
        b.setOnClickListener(v1 -> doneRegister(v));
        return v;
    }

    public interface OnRegisterFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener{
        void onRegisterSuccess(Credentials credentials);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void doneRegister(View theView) {
        if (mListener != null) {

            EditText newEmail = theView.findViewById(R.id.newEmail);
            EditText newPassword = theView.findViewById(R.id.newPassword);
            EditText userName = theView.findViewById(R.id.userName);
            EditText firstName = theView.findViewById(R.id.firstName);
            EditText lastName = theView.findViewById(R.id.lastName);

            Credentials credentials = new Credentials.Builder(
                    newEmail.getText().toString(),
                    newPassword.getText().toString())
                    .addUsername(userName.getText().toString())
                    .addFirstName(firstName.getText().toString())
                    .addLastName(lastName.getText().toString())
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            Log.d("msg"," " + msg);
            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();


            /*Credentials credentials;
            EditText newEmail = theView.findViewById(R.id.newEmail);
            EditText newPassword = theView.findViewById(R.id.newPassword);
            EditText reTypePassword = theView.findViewById(R.id.retypePassword);

            String newEmailS = newEmail.getText().toString();
            String newPasswordS = newPassword.getText().toString();
            String reTypePasswordS = reTypePassword.getText().toString();

            int check = 1;
            for (int i = 0; i < newEmailS.length(); i ++) {
                if (newEmailS.charAt(i) == '@') {
                    check *=0;
                }
            }
            if (check != 0) {
                newEmail.setError("Please enter email.");
            } else if (newPasswordS.length() < 6) {
                newPassword.setError("Please enter more than 6 character.");
            } else if (!newPasswordS.equals(reTypePasswordS)){
                reTypePassword.setError("Please enter the same password.");
            } else {
                credentials = new Credentials.Builder(newEmailS, newPasswordS).build();
                Log.d("pass: ", "all set." + credentials.getEmail());
                mListerner.onRegisterSuccess(credentials);
            }*/

        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR !!",  result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));
            Log.d("result register: ", " " + resultsJSON);
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mListener.onRegisterSuccess(mCredentials);
//                        resultsJSON.getString(
//                                getString(R.string.keys_json_login_jwt)));
                return;
            } else {
                //Register was unsuccessful. Don’t switch fragments and
                // inform the user
                Log.w("test json", " " + resultsJSON.get("error"));
                String error = (String) resultsJSON.get("error");
                if(error.equals("first")) {
                    ((TextView) getView().findViewById(R.id.firstName))
                            .setError("First Name Can Not Be Null");
                    Log.d("error"," " + result);
                } else if (error.equals("last")) {
                    ((TextView) getView().findViewById(R.id.lastName))
                            .setError("Last Name Can Not Be Null");
                    Log.d("error"," " + result);
                } else if (error.equals("username")) {
                    ((TextView) getView().findViewById(R.id.userName))
                            .setError("UserName Can Not Be Null");
                    Log.d("error"," " + result);
                } else if (error.equals("email")) {
                    ((TextView) getView().findViewById(R.id.newEmail))
                            .setError("Email Can Not Be Null");
                    Log.d("error"," " + result);
                } else if (error.equals("password")) {
                    ((TextView) getView().findViewById(R.id.newPassword))
                            .setError("Password Can Not Be Null");
                    Log.d("error"," " + result);
                } else if (error.contains("username")) {
                    ((TextView) getView().findViewById(R.id.userName))
                            .setError("Username already exists");
                    Log.d("error"," " + result);
                } else if (error.contains("email")) {
                    ((TextView) getView().findViewById(R.id.newEmail))
                            .setError("Email already exits");
                    Log.d("error"," " + result);
                }
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.newEmail))
                    .setError("Register Unsuccessful");
        }
    }
}
