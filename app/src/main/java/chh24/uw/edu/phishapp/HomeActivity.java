package chh24.uw.edu.phishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import chh24.uw.edu.phishapp.blog.BlogPost;
import chh24.uw.edu.phishapp.model.Credentials;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlogFragment.OnListFragmentInteractionListener,
        SetFragment.OnSetFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener{

    private SuccessFragment successFragment;
    private String mJwToken;
    private String mEmail;

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
        Credentials credentials = (Credentials) args.get("credentials message");
        mEmail = credentials.getEmail();
        successFragment = new SuccessFragment();
        mJwToken = intent.getStringExtra(getString(R.string.keys_intent_jwt));
        Fragment fragment;
        if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
            fragment = new ChatFragment();
        } else {
            fragment = new SuccessFragment();
            fragment.setArguments(args);
        }

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
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d("token ", " " + mJwToken);

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

        } else if (id == R.id.menu_global_chat) {
            Bundle args = new Bundle();
            ChatFragment chatFragment = new ChatFragment();
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
            args.putSerializable(getString(R.string.enter_email), mEmail);
            chatFragment.setArguments(args);
            loadFragment(chatFragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Log.d("got here!!", " After for loop");
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
    public void onSetFragmentInteraction(BlogPost theItem) {
        SetPostFragment bpf = new SetPostFragment();
        Bundle args = new Bundle();

        args.putSerializable(getString(R.string.view_long_date_text), theItem.getLongDate());
        args.putSerializable(getString(R.string.view_location_text), theItem.getLocation());
        args.putSerializable(getString(R.string.view_set_list_data_text), theItem.getListData());
        args.putSerializable(getString(R.string.view_set_list_note_text), theItem.getListNote());
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
        Log.d("Wait ", " fragment interraction show");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_home, new WaitFragment(), "WAIT")
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

                //mJwToken = root.getString(getString(R.string.keys_json_login_jwt));

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
        Log.d("got here!!", " In handle set list ");
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
                                        getString(R.string.keys_json_lists_short_date)))
                                .addLongDate(jsonList.getString(
                                        getString(R.string.keys_json_lists_long_date)))
                                .addLocation(jsonList.getString(
                                        getString(R.string.keys_json_lists_location)))
                                .addVenue(jsonList.getString(
                                        getString(R.string.keys_json_lists_venue)))
                                .addUrl(jsonList.getString(
                                        getString(R.string.keys_json_blogs_url)))
                                .addListData(jsonList.getString(
                                        getString(R.string.keys_json_lists_list_data)))
                                .addListNote(jsonList.getString(
                                        getString(R.string.keys_json_lists_list_note)))
                                .build());
                    }

                    BlogPost[] listsAsArray = new BlogPost[lists.size()];
                    listsAsArray = lists.toArray(listsAsArray);

                    Bundle args = new Bundle();
                    args.putSerializable(SetFragment.ARG_SET_LIST, listsAsArray);
                    Fragment frag = new SetFragment();
                    frag.setArguments(args);
                    Log.d("got here!!", " After frag");

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

    private void logout() {
        new DeleteTokenAsyncTask().execute();
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs

        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        //close the app
        finishAndRemoveTask();

        //or close this activity and bring back the Login
        //Intent i = new Intent(this, MainActivity.class);
        //startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        //finish();
    }

    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();

            //or close this activity and bring back the Login
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            //Ends this Activity and removes it from the Activity back stack.
//            finish();
        }
    }



}
