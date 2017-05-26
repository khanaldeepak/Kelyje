package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import lt.laboratorinis.psi.kelyje.MainActivity;
import lt.laboratorinis.psi.kelyje.R;
import lt.laboratorinis.psi.kelyje.users.Driver;

public class DriverRegistrationActivity extends AppCompatActivity {

    private EditText mark;
    private EditText plate;
    private EditText years;
    private Button register;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        mark = (EditText) findViewById(R.id.editMark);
        plate = (EditText) findViewById(R.id.editPlate);
        years = (EditText) findViewById(R.id.editYears);
        register = (Button) findViewById(R.id.btnFinishRegistration);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishRegistration();
            }
        });
    }

    private void finishRegistration() {
        final String markInput = mark.getText().toString().trim();
        final String plateInput = plate.getText().toString().trim();
        final String yearsInput = years.getText().toString().trim();

        if (TextUtils.isEmpty(markInput)) {
//            Toast.makeText(this, "Please enter car's mark!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite automobilio markę!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(plateInput)) {
//            Toast.makeText(this, "Please enter car plate's number!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite automobilio numerius!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(yearsInput)) {
//            Toast.makeText(this, "Please enter car's years!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite automobilio metus!", Toast.LENGTH_LONG).show();
            return;
        }

        int years = Integer.parseInt(yearsInput);
        int currentYears = Calendar.getInstance().get(Calendar.YEAR);
        if (years < currentYears - 50  || years > currentYears) {
            //Toast.makeText(this, "Please enter valid years! (" + (currentYears - 50) + " - " + currentYears + ")", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite tinkamus metus! (" + (currentYears - 50) + " - " + currentYears + ")", Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            String emailInput = bundle.getString("email", null);
            final String nameInput = bundle.getString("name");
            final String surnameInput = bundle.getString("surname");
            final String phoneInput = bundle.getString("phone");
            String passwordInput = bundle.getString("password", null);
            boolean sn = bundle.getBoolean("socialNetwork");

            if (!sn) {
                // traditional item_driver (username & password) registration
                final ProgressDialog dialog = new ProgressDialog(this);
                //dialog.setMessage("Registering. Please Wait...");
                dialog.setMessage("Registruojama...");
                dialog.show();

                firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();

                                if (task.isSuccessful()) {
                                    //Toast.makeText(DriverRegistrationActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();
                                    Toast.makeText(DriverRegistrationActivity.this, "Registracija sėkminga!", Toast.LENGTH_LONG).show();

                                    FirebaseUser user = task.getResult().getUser();
                                    String id = user.getUid();
                                    writeNewDriver(myRef, id, nameInput, surnameInput, phoneInput, markInput, plateInput, yearsInput);

                                    finish();

                                    Intent intent = new Intent(DriverRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    //Toast.makeText(DriverRegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                                    Toast.makeText(DriverRegistrationActivity.this, "Registracija nepavyko!", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            } else {
                // social networks item_driver registration
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String id = user.getUid();
                    writeNewDriver(myRef, id, nameInput, surnameInput, phoneInput, markInput, plateInput, yearsInput);

                    finish();

                    Intent intent = new Intent(DriverRegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            Log.d("Registration","Missing information");
        }
    }

    private void writeNewDriver(DatabaseReference databaseReference, String id,
                              String name, String surname, String phone,
                              String mark, String plate, String years) {

        Driver driver = new Driver(name, surname, phone, mark, plate, years);
        databaseReference.child(id).setValue(driver);
    }
}
