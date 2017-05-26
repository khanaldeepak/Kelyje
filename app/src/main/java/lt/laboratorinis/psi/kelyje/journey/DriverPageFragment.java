package lt.laboratorinis.psi.kelyje.journey;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lt.laboratorinis.psi.kelyje.R;

public class DriverPageFragment extends Fragment {

    private TextView nameSurname;
    private TextView carInfo;
    private ImageView image;
    private Button chooseDriver;

    private View mView;

    public DriverPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_driver_page, container, false);

        nameSurname = (TextView) mView.findViewById(R.id.driverNameSurname);
        carInfo = (TextView) mView.findViewById(R.id.carInfo);
        image = (ImageView) mView.findViewById(R.id.driverImage);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            nameSurname.setText(bundle.getString("nameSurname"));
            carInfo.setText(bundle.getString("carInfo"));
            // try to load image from firebase storage
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            String id = bundle.getString("id");
            StorageReference loadImage = storage.child("ProfileImages").child(id);

            loadImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mView.getContext()).load(uri.toString()).into(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //todo: loading from storage failed - load social networks photo
                }
            });
        }

        chooseDriver = (Button) mView.findViewById(R.id.btnChoose);
        chooseDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mView.getContext(), "Laukite vairuotojo patvirtinimo!", Toast.LENGTH_LONG).show();

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new DriverCallFragment());
                fragmentTransaction.commit();
            }
        });

        return mView;
    }
}
