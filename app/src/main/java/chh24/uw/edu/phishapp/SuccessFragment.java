package chh24.uw.edu.phishapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import chh24.uw.edu.phishapp.model.Credentials;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {


    public SuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success, container, false);
    }

    public void updateContent (String theS) {
        TextView tv = getActivity().findViewById(R.id.success);
        tv.setText(theS);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            Credentials credentials = (Credentials) getArguments().getSerializable("credentials message");
            updateContent(credentials.getEmail());
        }
    }
}
