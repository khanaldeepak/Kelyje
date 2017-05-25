package lt.laboratorinis.psi.kelyje.authentication.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lt.laboratorinis.psi.kelyje.MainActivity;
import lt.laboratorinis.psi.kelyje.R;
import lt.laboratorinis.psi.kelyje.authentication.registration.RegistrationActivity;
import lt.laboratorinis.psi.kelyje.authentication.registration.SocialNetworksRegistrationActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //facebook
    private LoginButton mFacebookButton;
    private CallbackManager mCallbackManager;

    //google
    private SignInButton mGoogleButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    //email & password
    private EditText email;
    private EditText password;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            loginSuccessful(user);
        } else {
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        loginSuccessful(user);
                    }
                }
            };

            email = (EditText) findViewById(R.id.editEmail);
            password = (EditText) findViewById(R.id.editPassword);

            initiateFacebookLogin();
            initiateGoogleLogin();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthListener != null) {
            firebaseAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnLogin:
                loginWithEmailAndPassword();
                break;
            case R.id.btnRegister:
                register();
                break;
            case R.id.textForgotPassword:
                forgotPassword();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // google
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                loginFailed();
            }
        } else { //facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loginSuccessful(final FirebaseUser user) {
        progressDialog = null;

        if(!LoginActivity.this.isFinishing()) {
            progressDialog = new ProgressDialog(this);
            //progressDialog.setMessage("Please wait...");
            progressDialog.setMessage("Jungiamasi...");
            progressDialog.show();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        String id = user.getUid();
        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //user exists in database
                    finish();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } else {
                    finish();

                    Intent intent = new Intent(LoginActivity.this, SocialNetworksRegistrationActivity.class);
                    startActivity(intent);

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void loginFailed() {
        //Toast.makeText(LoginActivity.this, "Sign-in failed!", Toast.LENGTH_LONG).show();
        Toast.makeText(LoginActivity.this, "Klaida bandant prisijungti!", Toast.LENGTH_LONG).show();
    }

    private void initiateFacebookLogin() {
        mFacebookButton = (LoginButton) findViewById(R.id.facebookBtn);

        mFacebookButton.setReadPermissions("email", "public_profile");

        // Callback registration
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //Toast.makeText(LoginActivity.this, "Canceled!", Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, "Atšaukta!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                loginFailed();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    loginFailed();
                }
            }
        });
    }

    private void initiateGoogleLogin() {
        mGoogleButton = (SignInButton) findViewById(R.id.googleBtn);

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        loginFailed();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loginFailed();
                        }
                    }
                });
    }

    private void googleSignIn() {
        if (mGoogleApiClient != null) { // Google sign out
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginWithEmailAndPassword() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            //Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Pirma įveskite el. paštą!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            //Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite slaptažodį!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        //dialog.setMessage("Connecting. Please wait...");
        dialog.setMessage("Jugiamasi...");
        dialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();

                if (!task.isSuccessful()) {
                    //Toast.makeText(LoginActivity.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, "Blogai įvestas el. paštas arba slaptažodis!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void forgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }
}
