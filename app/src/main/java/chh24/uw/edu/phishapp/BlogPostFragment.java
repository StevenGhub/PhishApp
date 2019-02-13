package chh24.uw.edu.phishapp;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogPostFragment extends Fragment implements View.OnClickListener {

    private String mUrl;

    public BlogPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blog_post, container, false);

        Button b = (Button) v.findViewById(R.id.full_post);
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
            String title = getArguments().getString(getString(R.string.view_title_text));
            updateContent(title, "title");
            String date = getArguments().getString(getString(R.string.view_date_text));
            updateContent(date, "date");
            String teaser = getArguments().getString(getString(R.string.view_teaser_text));
            updateContent(teaser, "teaser");
            mUrl = getArguments().getString(getString(R.string.full_post));
        }

    }

    public void updateContent (String theS, String theKey) {

        if (theKey.equals("title")) {
            TextView tv = getActivity().findViewById(R.id.longDateTextView);
            tv.setText(theS);
        } else if (theKey.equals("date")) {
            TextView tv = getActivity().findViewById(R.id.locationTextView);
            tv.setText(theS);
        } else {
            TextView tv = getActivity().findViewById(R.id.View_teaser);
            tv.setText(Html.fromHtml(theS,Html.FROM_HTML_MODE_COMPACT));
        }
    }

}
