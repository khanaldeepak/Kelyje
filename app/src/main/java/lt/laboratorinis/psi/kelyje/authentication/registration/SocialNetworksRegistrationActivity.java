package lt.laboratorinis.psi.kelyje.authentication.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lt.laboratorinis.psi.kelyje.R;

public class SocialNetworksRegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText name;
    private EditText surname;
    private EditText phone;
    private CheckBox driver;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_networks_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        name = (EditText) findViewById(R.id.editName);
        surname = (EditText) findViewById(R.id.editSurname);
        phone = (EditText) findViewById(R.id.editPhone);
        driver = (CheckBox) findViewById(R.id.checkDriver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnRegister:
                registration();
                break;
        }
    }

    private void registration() {
        final String nameInput = name.getText().toString().trim();
        final String surnameInput = surname.getText().toString().trim();
        final String phoneInput = phone.getText().toString().trim();
        boolean driverOption = driver.isChecked();

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

        if (driverOption) {
            //add driver details
            Intent intent = new Intent(SocialNetworksRegistrationActivity.this, DriverRegistrationActivity.class);
            intent.putExtra("name", nameInput);
            intent.putExtra("surname", surnameInput);
            intent.putExtra("phone", phoneInput);
            intent.putExtra("socialNetwork", true);
            startActivity(intent);

            finish();
        } else {
            //finish registration
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String id = user.getUid();
                writeNewUser(myRef, id, nameInput, surnameInput, phoneInput);
            }

            finish();
        }

    }

    private void writeNewUser(DatabaseReference databaseReference, String id,
                              String name, String surname, String phone) {

        databaseReference.child(id).child("name").setValue(name);
        databaseReference.child(id).child("surname").setValue(surname);
        databaseReference.child(id).child("phone").setValue(phone);
    }
}
