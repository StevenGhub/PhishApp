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

import tcss450.uw.edu.phishapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnRegisterFragmentInteractionListener mListerner;

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

    public interface OnRegisterFragmentInteractionListener {
        void onRegisterSuccess(Credentials credentials);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  OnRegisterFragmentInteractionListener) {
            mListerner = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void doneRegister(View theView) {
        if (mListerner != null) {
            Credentials credentials;
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
            }

        }
    }

}
