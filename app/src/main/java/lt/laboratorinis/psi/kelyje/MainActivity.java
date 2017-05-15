package lt.laboratorinis.psi.kelyje;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import lt.laboratorinis.psi.kelyje.authentication.login.LoginActivity;
import lt.laboratorinis.psi.kelyje.fragments.HelpFragment;
import lt.laboratorinis.psi.kelyje.fragments.JourneyFragment;
import lt.laboratorinis.psi.kelyje.fragments.JourneysHistoryFragment;
import lt.laboratorinis.psi.kelyje.fragments.PaymentsFragment;
import lt.laboratorinis.psi.kelyje.fragments.PeriodicJourneyFragment;
import lt.laboratorinis.psi.kelyje.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private Toolbar toolbar;

    private TextView email;
    private TextView phone;
    private TextView username;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        email = (TextView) header.findViewById(R.id.email);
        email.setText(user.getEmail());

        /*phone = (TextView) header.findViewById(R.id.phone);
        phone.setText(firebaseAuth.getCurrentUser().get());*/

        username = (TextView) header.findViewById(R.id.username);
        username.setText(user.getDisplayName());

        //Button button = (Button) findViewById(R.id.button);
        //button.setVisibility(View.INVISIBLE);
       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.payments);
            }
        });*/

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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_journey) {
            loadFragment(new JourneyFragment());
        } else if (id == R.id.nav_history) {
            loadFragment(new JourneysHistoryFragment());
        } else if (id == R.id.nav_payments) {
            loadFragment(new PaymentsFragment());
        } else if (id == R.id.nav_periodic_journey) {
            loadFragment(new PeriodicJourneyFragment());
        } else if (id == R.id.nav_help) {
            loadFragment(new HelpFragment());
        } else if (id == R.id.nav_profile) {
            loadFragment(new ProfileFragment());
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut(); // Facebook sign out

            Toast.makeText(this, "Goodbye!", Toast.LENGTH_LONG).show();

            finish();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    /*public void addCard() {

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Mėnuo");

        List<String> categories2 = new ArrayList<>();
        categories2.add("Metai");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter2);
    }*/

    /*public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Informacija apie sėkmingai atliktą veiksmą!\n", Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(10);
        snackbar.show();
    }*/
}
