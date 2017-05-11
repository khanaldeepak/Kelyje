package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Driver;

import lt.laboratorinis.psi.kelyje.R;

public class DriverRegistrationActivity extends AppCompatActivity {

    private EditText mark;
    private EditText plate;
    private EditText years;
    private Button register;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        firebaseAuth = FirebaseAuth.getInstance();

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
        String markInput = mark.getText().toString().trim();
        String plateInput = plate.getText().toString().trim();
        String yearsInput = years.getText().toString().trim();

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

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering. Please Wait...");
        dialog.show();

        //String
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            /*intent.putExtra("email", emailInput);
            intent.putExtra("name", nameInput);
            intent.putExtra("surname", surnameInput);
            intent.putExtra("phone", phoneInput);
            intent.putExtra("password", passwordInput);*/
        }
        /*firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(DriverRegistrationActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();

                            finish();
                        } else {
                            Toast.makeText(DriverRegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                        }

                    }
                });*/
    }
}
