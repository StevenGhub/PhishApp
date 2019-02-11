package tcss450.uw.edu.phishapp;


import android.content.Context;
import android.content.SharedPreferences;
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

import tcss450.uw.edu.phishapp.R;
import tcss450.uw.edu.phishapp.model.Credentials;
import tcss450.uw.edu.phishapp.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwt;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_login, container, false);

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = v.findViewById(R.id.signin);
        b.setOnClickListener( v1 -> sentMessage(v));

        b = (Button) v.findViewById(R.id.registerB);
        b.setOnClickListener(this:: goRegister);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.enterEmail);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.enterPassword);
            passwordEdit.setText(password);

            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else  {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            sentMessage(v);
        }
    }

    public interface OnLoginFragmentInteractionListener  extends WaitFragment.OnFragmentInteractionListener{

        void onLoginSuccess (Credentials login, String jwt);
        void onRegisterClicked();
    }

    public void sentMessage(View theRootView) {
        if (mListener != null) {

            EditText email = (EditText) theRootView.findViewById(R.id.enterEmail);
            EditText password = (EditText) theRootView.findViewById(R.id.enterPassword);
            boolean hasError = false;
            if (email.getText().length() == 0) {
                hasError = true;
                email.setError("Field must not be empty.");
            }  else if (email.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
                hasError = true;
                email.setError("Field must contain a valid email address.");
            }
            if (password.getText().length() == 0) {
                hasError = true;
                password.setError("Field must not be empty.");
            }

            if (!hasError) {
                doLogin(new Credentials.Builder(
                        email.getText().toString(),
                        password.getText().toString())
                        .build());
            }
//            //build the web service URL
//            Uri uri = new Uri.Builder()
//                    .scheme("https")
//                    .appendPath(getString(R.string.ep_base_url))
//                    .appendPath(getString(R.string.ep_login))
//                    .build();
//
//            //build the JSONObject
//            JSONObject msg = credentials.asJSONObject();
//
//            mCredentials = credentials;
//
//            //instantiate and execute the AsyncTask.
//            new SendPostAsyncTask.Builder(uri.toString(), msg)
//                    .onPreExecute(this::handleLoginOnPre)
//                    .onPostExecute(this::handleLoginOnPost)
//                    .onCancelled(this::handleErrorsInTask)
//                    .build().execute();
/*
            Credentials credentials;
            EditText email = (EditText) theRootView.findViewById(R.id.enterEmail);
            EditText password = (EditText) theRootView.findViewById(R.id.enterPassword);
            String emailMes = email.getText().toString();
            String passwordMes = password.getText().toString();
            int check = 1;
            for (int i = 0; i < emailMes.length(); i ++) {
                if (emailMes.charAt(i) == '@') {
                    check *=0;
                }
            }
            if (email.length() == 0) {
                email.setError("Please enter the email.");
            }
            if (check != 0) {
                email.setError("This is not a valid email.");
            } else {
                credentials = new Credentials.Builder(emailMes, passwordMes).build();
                mListener.onLoginSuccess(credentials, "something");
            }*/
        }
    }

    public void goRegister(View theView) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getArguments() != null) {
           String  newEmail = getArguments().getString(getString(R.string.new_email));
           String newPass = getArguments().getString((getString(R.string.new_password)));
           EditText oldEmail = getActivity().findViewById(R.id.enterEmail);
           EditText oldPass = getActivity().findViewById(R.id.enterPassword);
           oldEmail.setText(newEmail);
           oldPass.setText(newPass);
        }
    }

    /**
        * Handle errors that may occur during the AsyncTask.
        * @param result the error message provide from the AsyncTask
        */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    /**
        * Handle the setup of the UI before the HTTP call to the webservice.
        */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }


    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
        private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean(getString(R.string.keys_json_login_success));

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mJwt = resultsJSON.getString(
                        getString(R.string.keys_json_login_jwt));

                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);


                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.enterEmail))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.enterEmail))
                    .setError("Login Unsuccessful");
        }
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        Log.d("JSON Credentials", msg.toString());

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }



}
