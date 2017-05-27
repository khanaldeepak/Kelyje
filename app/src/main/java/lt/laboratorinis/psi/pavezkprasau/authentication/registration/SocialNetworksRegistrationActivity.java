package lt.laboratorinis.psi.pavezkprasau.authentication.registration;

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

import lt.laboratorinis.psi.pavezkprasau.MainActivity;
import lt.laboratorinis.psi.pavezkprasau.R;
import lt.laboratorinis.psi.pavezkprasau.users.Passenger;

public class SocialNetworksRegistrationActivity extends AppCompatActivity implements View.OnClickListener{

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
            default:
                break;
        }
    }

    private void registration() {
        final String phoneInput = phone.getText().toString().trim();
        boolean driverOption = driver.isChecked();

        if (TextUtils.isEmpty(phoneInput)) {
//            Toast.makeText(this, "Please enter phone!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Įveskite tel. numerį!", Toast.LENGTH_LONG).show();
            return;
        }

        if (driverOption) {
            //add item_driver details
            Intent intent = new Intent(SocialNetworksRegistrationActivity.this, DriverRegistrationActivity.class);
            intent.putExtra("phone", phoneInput);
            intent.putExtra("socialNetwork", true);
            startActivity(intent);

            finish();
        } else {
            //finish registration
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String id = user.getUid();
                writeNewUser(myRef, id, phoneInput);
            }

            finish();

            Intent intent = new Intent(SocialNetworksRegistrationActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void writeNewUser(DatabaseReference databaseReference, String id, String phone) {
        Passenger user = new Passenger(phone);
        databaseReference.child(id).setValue(user);
    }
}
