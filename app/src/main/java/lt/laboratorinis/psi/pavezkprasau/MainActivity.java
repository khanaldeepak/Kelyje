package lt.laboratorinis.psi.pavezkprasau;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lt.laboratorinis.psi.pavezkprasau.authentication.login.LoginActivity;
import lt.laboratorinis.psi.pavezkprasau.help.HelpFragment;
import lt.laboratorinis.psi.pavezkprasau.journey.JourneyFragment;
import lt.laboratorinis.psi.pavezkprasau.journeyshistory.JourneysHistoryFragment;
import lt.laboratorinis.psi.pavezkprasau.payments.PaymentsFragment;
import lt.laboratorinis.psi.pavezkprasau.periodicjourney.PeriodicJourneyFragment;
import lt.laboratorinis.psi.pavezkprasau.profile.ProfileFragment;
import lt.laboratorinis.psi.pavezkprasau.users.Passenger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private Toolbar toolbar;

    private TextView email;
    private TextView phone;
    private TextView username;
    private ImageView profileImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private boolean driver = false;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        //getting current user
        user = firebaseAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        //if the user is not logged in
        //that means current user will return null
        if(user == null){
            //closing this activity
            finish();
            //starting login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        JourneyFragment journeyFragment = new JourneyFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, journeyFragment);
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

        email = (TextView) header.findViewById(R.id.email);
        username = (TextView) header.findViewById(R.id.username);
        phone = (TextView) header.findViewById(R.id.phone);
        profileImage = (ImageView) header.findViewById(R.id.image);

        if (user != null) {
            email.setText(user.getEmail());

            String nameSurname = user.getDisplayName();
            if (nameSurname != null) {
                username.setText(nameSurname);
                String[] array = user.getDisplayName().split("\\s+");
                myRef.child(user.getUid()).child("name").setValue(array[0]);
                myRef.child(user.getUid()).child("surname").setValue(array[1]);
            }

            loadImage();

            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Passenger passenger = dataSnapshot.getValue(Passenger.class);

                    // overrides current username (if user is logged-in via social networks
                    String name = passenger.getName();
                    String surname = passenger.getSurname();
                    if (name != null && surname != null) {
                        username.setText(name + " " + surname);
                    }

//                    Toast.makeText(MainActivity.this, "Welcome, " + username.getText().toString().trim(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Sveiki, " + username.getText().toString().trim(), Toast.LENGTH_LONG).show();

                    phone.setText(passenger.getPhone());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // user is online
        if (user != null) {
            myRef.child(user.getUid()).child("online").setValue(true);
        }

        // if user is driver - listen for incoming calls
        myRef.child(user.getUid()).child("busy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("zxc", "user is driver");
                        driver = true;
                        listenForCalls(user.getUid());
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            Toast.makeText(this, "Nustatymai kol kas neveikia!", Toast.LENGTH_LONG).show();
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
            ProfileFragment fragment = new ProfileFragment();

            Bundle bundle = new Bundle();
            bundle.putString("nameSurname", username.getText().toString().trim());
            bundle.putString("email", email.getText().toString().trim());
            bundle.putString("phone", phone.getText().toString().trim());

            fragment.setArguments(bundle);
            loadFragment(fragment);
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut(); // Facebook sign out

//            Toast.makeText(this, "Goodbye!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Sėkmingai atsijungėte!", Toast.LENGTH_LONG).show();

            // driver is not busy anymore
            if (driver) {
                myRef.child(user.getUid()).child("busy").setValue(false);
            }

            // user is offline
            myRef.child(user.getUid()).child("online").setValue(false);

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

    @Override
    protected void onStop() {
        super.onStop();

        // driver is not busy anymore
        if (driver) {
            myRef.child(user.getUid()).child("busy").setValue(false);
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    public void loadImage() {
        // try to load image from firebase storage
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference loadImage = storage.child("ProfileImages").child(user.getUid());

        loadImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri.toString()).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // loading from storage failed - load social networks photo
                Uri photoUri = user.getPhotoUrl();
                if (photoUri != null) {
                    Glide.with(MainActivity.this).load(photoUri).into(profileImage);
                }
            }
        });
    }

    public void listenForCalls(final String userID){
        myRef.child(userID).child("busy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    boolean busy = dataSnapshot.getValue(Boolean.class);

                    if (busy) {
                        /*Toast.makeText(MainActivity.this, "Gavote naują iškvietimą!", Toast.LENGTH_LONG).show();
                        loadFragment(new ProfileFragment());*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Naujas iškvietimas!");

                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final TextView source = new TextView(MainActivity.this);
                        source.setText("Iš: A");
                        source.setGravity(Gravity.CENTER);
                        layout.addView(source);

                        final TextView destination = new TextView(MainActivity.this);
                        destination.setText("Į: B");
                        destination.setGravity(Gravity.CENTER);
                        layout.addView(destination);

                        builder.setView(layout);

                        builder.setPositiveButton("Patvirtinti", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Iškvietimas patvirtintas!", Toast.LENGTH_LONG).show();
                                myRef.child(userID).child("confirmed").setValue(true);
                            }
                        });
                        builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Toast.makeText(MainActivity.this, "Atšaukta!", Toast.LENGTH_LONG).show();
                                myRef.child(userID).child("confirmed").setValue(false);
                                myRef.child(userID).child("busy").setValue(false);
                            }
                        });

                        builder.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
