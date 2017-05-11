package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import lt.laboratorinis.psi.kelyje.R;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private LoginButton mFacebookButton;
    private SignInButton mGoogleButton;
    private EditText email;
    private EditText name;
    private EditText surname;
    private EditText phone;
    private EditText password;
    private EditText password2;
    private CheckBox driver;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //facebook = (ImageButton) findViewById(R.id.btnFacebook);
        //google = (ImageButton) findViewById(R.id.btnGoogle);
        email = (EditText) findViewById(R.id.editEmail);
        name = (EditText) findViewById(R.id.editName);
        surname = (EditText) findViewById(R.id.editSurname);
        phone = (EditText) findViewById(R.id.editPhone);
        password = (EditText) findViewById(R.id.editPassword);
        password2 = (EditText) findViewById(R.id.editPassword2);
        driver = (CheckBox) findViewById(R.id.checkDriver);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            /*case R.id.btnFacebook:
                facebookRegistration();
                break;
            case R.id.btnGoogle:
                googleRegistration();
                break;*/
            case R.id.btnRegister:
                registration();
                break;
        }
    }

    private void facebookRegistration() {
        // ToDo
    }

    private void googleRegistration() {
        // ToDo
    }

    private void registration() {
        final String emailInput = email.getText().toString().trim();
        String nameInput = name.getText().toString().trim();
        String surnameInput = surname.getText().toString().trim();
        String phoneInput = phone.getText().toString().trim();
        final String passwordInput = password.getText().toString().trim();
        final String password2Input = password2.getText().toString().trim();
        boolean driverOption = driver.isChecked();

        final ProgressDialog dialog = new ProgressDialog(this);

        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(nameInput)) {
            Toast.makeText(this, "Please enter name!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(surnameInput)) {
            Toast.makeText(this, "Please enter surname!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(phoneInput)) {
            Toast.makeText(this, "Please enter phone!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password2Input)) {
            Toast.makeText(this, "Please enter password again!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!passwordInput.equals(password2Input)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            return;
        }

        if (driverOption) {
            //add driver details

            Intent intent = new Intent(RegistrationActivity.this, DriverRegistrationActivity.class);
            intent.putExtra("email", emailInput);
            intent.putExtra("name", nameInput);
            intent.putExtra("surname", surnameInput);
            intent.putExtra("phone", phoneInput);
            intent.putExtra("password", passwordInput);
            startActivity(intent);
        } else {
            //finish registration

            dialog.setMessage("Registering. Please Wait...");
            dialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();

                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
    }
}
