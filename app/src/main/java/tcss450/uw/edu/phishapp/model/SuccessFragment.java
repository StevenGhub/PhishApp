package tcss450.uw.edu.phishapp.model;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import tcss450.uw.edu.phishapp.R;

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
            String s = getArguments().getString(getString(R.string.success_message));
            updateContent(s);

        }
    }
}
