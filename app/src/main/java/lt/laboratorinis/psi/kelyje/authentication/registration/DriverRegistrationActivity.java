package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.app.ProgressDialog;
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

import lt.laboratorinis.psi.kelyje.R;

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
            Toast.makeText(this, "Please enter car's mark!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(plateInput)) {
            Toast.makeText(this, "Please enter car plate's number!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(yearsInput)) {
            Toast.makeText(this, "Please enter car's years!", Toast.LENGTH_LONG).show();
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
                // traditional (username & password) registration
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Registering. Please Wait...");
                dialog.show();

                firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();

                                if (task.isSuccessful()) {
                                    Toast.makeText(DriverRegistrationActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();

                                    FirebaseUser user = task.getResult().getUser();
                                    String id = user.getUid();
                                    writeNewDriver(myRef, id, nameInput, surnameInput, phoneInput, markInput, plateInput, yearsInput);

                                    finish();
                                } else {
                                    Toast.makeText(DriverRegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            } else {
                // social networks registration
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String id = user.getUid();
                    writeNewDriver(myRef, id, nameInput, surnameInput, phoneInput, markInput, plateInput, yearsInput);

                    finish();
                }
            }
        } else {
            Log.d("Registration","Missing information");
        }
    }

    private void writeNewDriver(DatabaseReference databaseReference, String id,
                              String name, String surname, String phone,
                              String mark, String plate, String years) {

        databaseReference.child(id).child("name").setValue(name);
        databaseReference.child(id).child("surname").setValue(surname);
        databaseReference.child(id).child("phone").setValue(phone);
        databaseReference.child(id).child("mark").setValue(mark);
        databaseReference.child(id).child("plate").setValue(plate);
        databaseReference.child(id).child("years").setValue(years);
    }
}
