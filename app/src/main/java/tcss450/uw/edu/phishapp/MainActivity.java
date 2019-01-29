package tcss450.uw.edu.phishapp;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

import tcss450.uw.edu.phishapp.model.Credentials;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener ,
        RegisterFragment.OnRegisterFragmentInteractionListener{
    public static final String EXTRA_MESSAGE = "credentials message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            if (findViewById(R.id.frame_main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_main_container, new LoginFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onLoginSuccess(Credentials login, String jwt) {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_MESSAGE, login);
        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);
        startActivity(intent);
        finish();

        /*Bundle args = new Bundle();
        args.putSerializable(getString(R.string.success_message), login.getEmail());
        successFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.frame_main_container, successFragment)
                .disallowAddToBackStack();
        transaction.commit();*/
    }

    @Override
    public void onRegisterClicked() {

        RegisterFragment registerFragment = new RegisterFragment();

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.frame_main_container, registerFragment)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRegisterSuccess(Credentials theCredentials) {
        Credentials credentials1 = theCredentials;
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.new_email), credentials1.getEmail());
        args.putSerializable(getString(R.string.new_password), credentials1.getPassword());
        loginFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.frame_main_container, loginFragment)
                .disallowAddToBackStack();
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i< fm.getBackStackEntryCount(); i ++) {
            fm.popBackStack();
        }

        transaction.commit();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_main_container, new WaitFragment(), "WAIT")
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
}
