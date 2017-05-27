package lt.laboratorinis.psi.pavezkprasau.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import lt.laboratorinis.psi.pavezkprasau.MainActivity;
import lt.laboratorinis.psi.pavezkprasau.R;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ImageView profileImage;
    private TextView nameSurname;
    private TextView cityAndCountry;
    private TextView money;
    private TextView journeys;
    private TextView email;
    private TextView phone;

    private FirebaseUser user;
    private StorageReference storage;
    private StorageReference userStorage;

    private static final int GALLERY_INTENT = 1;
    private static final int REQUEST_PERMISSION = 2;
    private ProgressDialog progressDialog;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private View mView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        profileImage = (ImageView) mView.findViewById(R.id.image);
        nameSurname = (TextView) mView.findViewById(R.id.nameSurname);
        cityAndCountry = (TextView) mView.findViewById(R.id.cityCountry);
        money = (TextView) mView.findViewById(R.id.moneyCount);
        journeys = (TextView) mView.findViewById(R.id.travelCount);
        email = (TextView) mView.findViewById(R.id.email);
        phone = (TextView) mView.findViewById(R.id.phone);

        profileImage.setOnClickListener(this);
        nameSurname.setOnClickListener(this);
        cityAndCountry.setOnClickListener(this);
        money.setOnClickListener(this);
        journeys.setOnClickListener(this);
        email.setOnClickListener(this);
        phone.setOnClickListener(this);

        loadImage();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameSurname.setText(bundle.getString("nameSurname"));
            email.setText(bundle.getString("email"));
            phone.setText(bundle.getString("phone"));
        }

        return mView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.image:
                progressDialog = new ProgressDialog(mView.getContext());
                requestReadFilePermission();

                break;
            case R.id.nameSurname:
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setTitle("Keisti vardą/pavardę");

                LinearLayout layout = new LinearLayout(mView.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText name = new EditText(mView.getContext());
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                name.setHint("Vardas");
                layout.addView(name);

                final EditText surname = new EditText(mView.getContext());
                surname.setInputType(InputType.TYPE_CLASS_TEXT);
                surname.setHint("Pavardė");
                layout.addView(surname);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nameInput = name.getText().toString().trim();
                        String surnameInput = surname.getText().toString().trim();
                        changeNameSurname(nameInput, surnameInput);
                    }
                });
                builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                break;
            case R.id.cityCountry:
                Toast.makeText(mView.getContext(), "City and country", Toast.LENGTH_SHORT).show();
                break;
            case R.id.moneyCount:
                Toast.makeText(mView.getContext(), "money", Toast.LENGTH_SHORT).show();
                break;
            case R.id.travelCount:
                Toast.makeText(mView.getContext(), "travels", Toast.LENGTH_SHORT).show();
                break;
            case R.id.email:
                Toast.makeText(mView.getContext(), "email", Toast.LENGTH_SHORT).show();
                break;
            case R.id.phone:
                Toast.makeText(mView.getContext(), "phone", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void requestReadFilePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            } else {
                Toast.makeText(mView.getContext(), "Reikalingas leidimas pasiekti failus!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            progressDialog.setMessage("Palaukite...");
            progressDialog.show();

            Uri uri = data.getData();

            if (user != null) {
                StorageReference childRef = storage.child("ProfileImages").child(user.getUid());

                childRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(mView.getContext(), "Pakeista sėkmingai!", Toast.LENGTH_LONG).show();
                            loadImage();
                        }
                    }
                });
            }
        }
    }

    private void loadImage(){
        // try to load image from firebase storage
        storage = FirebaseStorage.getInstance().getReference();
        userStorage = storage.child("ProfileImages").child(user.getUid());

        userStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mView.getContext()).load(uri.toString()).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // loading from storage failed - load social networks photo
                Uri photoUri = user.getPhotoUrl();
                if (photoUri != null) {
                    Glide.with(mView.getContext()).load(photoUri).into(profileImage);
                }
            }
        });

        //refresh navigation drawer image
        ((MainActivity) getActivity()).loadImage();
    }

    private void changeNameSurname(String name, String surname){
        myRef.child(user.getUid()).child("name").setValue(name);
        myRef.child(user.getUid()).child("surname").setValue(surname);
    }
}
