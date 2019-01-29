package tcss450.uw.edu.phishapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.phishapp.blog.BlogPost;
import tcss450.uw.edu.phishapp.model.Credentials;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlogFragment.OnListFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener{

    private SuccessFragment successFragment;
    private String mJwToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        successFragment = new SuccessFragment();
        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //args.putSerializable(getString(R.string.success_message), message);
        successFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction().add(R.id.content_home, successFragment)
                .disallowAddToBackStack();
        transaction.commit();

        /*if (savedInstanceState == null) {
            if (findViewById(R.id.content_home) != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_home, successFragment).commit();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menuHome) {
            loadFragment(successFragment);
        } else if (id == R.id.menuBlog) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_blog))
                    .appendPath(getString(R.string.ep_get))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleBlogGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();
        } else if (id == R.id.setLists) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_setlists))
                    .appendPath(getString(R.string.ep_recent))
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleSetListsOnPostExecute)
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_home, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(BlogPost theItem) {
        BlogPostFragment bpf = new BlogPostFragment();
        Bundle args = new Bundle();

        args.putSerializable(getString(R.string.view_title_text), theItem.getTitle());
        args.putSerializable(getString(R.string.view_date_text), theItem.getPubDate());
        args.putSerializable(getString(R.string.view_teaser_text), theItem.getTeaser());
        args.putSerializable(getString(R.string.full_post), theItem.getUrl());

        bpf.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_home, bpf)
                .addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_success_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }


    private void handleBlogGetOnPostExecute(final String result) {
    //parse JSON
    try {
        JSONObject root = new JSONObject(result);
        if (root.has(getString(R.string.keys_json_blogs_response))) {
            JSONObject response = root.getJSONObject(
                    getString(R.string.keys_json_blogs_response));
            if (response.has(getString(R.string.keys_json_blogs_data))) {
                JSONArray data = response.getJSONArray(
                        getString(R.string.keys_json_blogs_data));

                List<BlogPost> blogs = new ArrayList<>();

                for(int i = 0; i < data.length(); i++) {
                    JSONObject jsonBlog = data.getJSONObject(i);

                    blogs.add(new BlogPost.Builder(
                            jsonBlog.getString(
                                    getString(R.string.keys_json_blogs_pubdate)),
                            jsonBlog.getString(
                                    getString(R.string.keys_json_blogs_title)))
                            .addTeaser(jsonBlog.getString(
                                    getString(R.string.keys_json_blogs_teaser)))
                            .addUrl(jsonBlog.getString(
                                    getString(R.string.keys_json_blogs_url)))
                            .build());
                }

                BlogPost[] blogsAsArray = new BlogPost[blogs.size()];
                blogsAsArray = blogs.toArray(blogsAsArray);

                Bundle args = new Bundle();
                args.putSerializable(BlogFragment.ARG_BLOG_LIST, blogsAsArray);
                Fragment frag = new BlogFragment();
                frag.setArguments(args);

                onWaitFragmentInteractionHide();
                loadFragment(frag);
            } else {
                Log.e("ERROR!", "No data array");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } else {
            Log.e("ERROR!", "No response");
            //notify user
            onWaitFragmentInteractionHide();
        }

    } catch (JSONException e) {
        e.printStackTrace();
        Log.e("ERROR!", e.getMessage());
        //notify user
        onWaitFragmentInteractionHide();
    }
    }

    private void handleSetListsOnPostExecute(final String result) {

        Log.d("result"," " + result);
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_lists_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_blogs_response));
                if (response.has(getString(R.string.keys_json_lists_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_lists_data));

                    List<BlogPost> lists = new ArrayList<>();

                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonList = data.getJSONObject(i);

                        lists.add(new BlogPost.Builder(jsonList.getString(
                                getString(R.string.keys_json_blogs_url)),
                                jsonList.getString(
                                        getString(R.string.keys_json_blogs_title)))
                                .addLongDate(jsonList.getString(
                                        getString(R.string.keys_json_lists_long_date)))
                                .addLocation(jsonList.getString(
                                        getString(R.string.keys_json_lists_location)))
                                .addVenue(jsonList.getString(
                                        getString(R.string.keys_json_lists_venue)))
                                .build());
                    }

                    BlogPost[] listsAsArray = new BlogPost[lists.size()];
                    listsAsArray = lists.toArray(listsAsArray);

                    Bundle args = new Bundle();
                    args.putSerializable(SetFragment.ARG_SET_LIST, listsAsArray);
                    Fragment frag = new SetFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    //loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }


}
