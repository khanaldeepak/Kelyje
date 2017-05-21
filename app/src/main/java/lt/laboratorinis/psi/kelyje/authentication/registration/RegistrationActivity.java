package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lt.laboratorinis.psi.kelyje.MainActivity;
import lt.laboratorinis.psi.kelyje.R;
import lt.laboratorinis.psi.kelyje.users.Passenger;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText name;
    private EditText surname;
    private EditText phone;
    private EditText password;
    private EditText password2;
    private CheckBox driver;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        email = (EditText) findViewById(R.id.editEmail);
        name = (EditText) findViewById(R.id.editName);
        surname = (EditText) findViewById(R.id.editSurname);
        phone = (EditText) findViewById(R.id.editPhone);
        password = (EditText) findViewById(R.id.editPassword);
        password2 = (EditText) findViewById(R.id.editPassword2);
        driver = (CheckBox) findViewById(R.id.checkDriver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnRegister:
                registration();
                break;
            default:
                break;
        }
    }

    private void registration() {
        final String emailInput = email.getText().toString().trim();
        final String nameInput = name.getText().toString().trim();
        final String surnameInput = surname.getText().toString().trim();
        final String phoneInput = phone.getText().toString().trim();
        final String passwordInput = password.getText().toString().trim();
        final String password2Input = password2.getText().toString().trim();
        boolean driverOption = driver.isChecked();

        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isEmailValid(emailInput)) {
            Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_LONG).show();
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

        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password should consist of at least 6 characters", Toast.LENGTH_LONG).show();
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
            intent.putExtra("socialNetwork", false);
            startActivity(intent);
        } else {
            //finish registration
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Registering. Please Wait...");
            dialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();

                                FirebaseUser user = task.getResult().getUser();
                                String id = user.getUid();
                                writeNewUser(myRef, id, nameInput, surnameInput, phoneInput);

                                finish();

                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    private void writeNewUser(DatabaseReference databaseReference, String id, String name, String surname, String phone) {
        Passenger user = new Passenger(name, surname, phone);
        databaseReference.child(id).setValue(user);
    }
}
