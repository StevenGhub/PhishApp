package tcss450.uw.edu.phishapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetPostFragment extends Fragment implements View.OnClickListener {


    private String mUrl;

    public SetPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_post, container, false);

        Button b = (Button) v.findViewById(R.id.full_post_Set_List);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        startActivity(browserIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            String longDate = getArguments().getString(getString(R.string.view_long_date_text));
            updateContent(longDate, "longDate");
            String location = getArguments().getString(getString(R.string.view_location_text));
            updateContent(location, "location");
            String listData = getArguments().getString(getString(R.string.view_set_list_data_text));
            updateContent(listData, "listData");
            String listNote = getArguments().getString(getString(R.string.view_set_list_note_text));
            updateContent(listNote, "listNote");
            mUrl = getArguments().getString(getString(R.string.full_post));
        }
    }

    public void updateContent (String theS, String theKey) {

        if (theKey.equals("longDate")) {
            TextView tv = getActivity().findViewById(R.id.longDateTextView);
            tv.setText(theS);
        } else if (theKey.equals("location")) {
            TextView tv = getActivity().findViewById(R.id.locationTextView);
            tv.setText(theS);
        } else if (theKey.equals("listData")){
            TextView tv = getActivity().findViewById(R.id.setListDataTextView);
            tv.setText(Html.fromHtml(theS, Html.FROM_HTML_MODE_COMPACT));
        } else {
            TextView tv = getActivity().findViewById(R.id.setListNoteTextView);
            tv.setText(Html.fromHtml(theS, Html.FROM_HTML_MODE_COMPACT));
        }
    }

}
