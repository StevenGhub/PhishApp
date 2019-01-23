package tcss450.uw.edu.phishapp.model;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tcss450.uw.edu.phishapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private OnLoginFragmentInteractionListener mListener;

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

    public interface OnLoginFragmentInteractionListener {

        void onLoginSuccess (Credentials login, String jwt);
        void onRegisterClicked();
    }

    public void sentMessage(View theRootView) {
        if (mListener != null) {
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
            }
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

}
