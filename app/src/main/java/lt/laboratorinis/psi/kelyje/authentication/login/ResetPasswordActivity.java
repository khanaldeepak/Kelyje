package lt.laboratorinis.psi.kelyje.authentication.login;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import lt.laboratorinis.psi.kelyje.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button confirm;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.editEmail);
        confirm = (Button) findViewById(R.id.btnConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String emailAddress = email.getText().toString().trim();

        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Checking. Please wait...");
        dialog.setMessage("Tikrinama...");
        dialog.show();

        firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            //Toast.makeText(ResetPasswordActivity.this, "Email sent!", Toast.LENGTH_LONG).show();
                            Toast.makeText(ResetPasswordActivity.this, "Laiškas išsiųstas!", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(ResetPasswordActivity.this, "User not found!", Toast.LENGTH_LONG).show();
                            Toast.makeText(ResetPasswordActivity.this, "Vartotojas nerastas!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
