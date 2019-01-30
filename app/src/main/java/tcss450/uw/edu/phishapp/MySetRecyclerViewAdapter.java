package tcss450.uw.edu.phishapp;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.phishapp.SetFragment.OnSetFragmentInteractionListener;
import tcss450.uw.edu.phishapp.blog.BlogPost;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BlogPost} and makes a call to the
 * specified {@link OnSetFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySetRecyclerViewAdapter extends RecyclerView.Adapter<MySetRecyclerViewAdapter.ViewHolder> {

    private final List<BlogPost> mValues;
    private final OnSetFragmentInteractionListener mListener;

    public MySetRecyclerViewAdapter(List<BlogPost> items, OnSetFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLongDateView.setText(mValues.get(position).getLongDate());
        holder.mLocationView.setText(mValues.get(position).getLocation());
        holder.mVenueView.setText(Html.fromHtml(mValues.get(position).getVenue(), Html.FROM_HTML_MODE_COMPACT));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSetFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLongDateView;
        public final TextView mLocationView;
        public final TextView mVenueView;
        public BlogPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLongDateView = (TextView) view.findViewById(R.id.long_date);
            mLocationView = (TextView) view.findViewById(R.id.location);
            mVenueView = (TextView) view.findViewById(R.id.venue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mVenueView.getText() + "'";
        }
    }
}
