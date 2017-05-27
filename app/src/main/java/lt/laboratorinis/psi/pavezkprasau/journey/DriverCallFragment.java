package lt.laboratorinis.psi.pavezkprasau.journey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lt.laboratorinis.psi.pavezkprasau.R;

public class DriverCallFragment extends Fragment {

    private TextView driver;
    private Button cancel;

    private String driverID;
    private boolean firstDriverCall = true;

    private View mView;

    public DriverCallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_driver_call, container, false);

        driver = (TextView) mView.findViewById(R.id.driverNameSurname);

        final Bundle bundle = this.getArguments();

        driverID = null;

        if (bundle != null) {
            driver.setText(bundle.getString("nameSurname"));

            driverID = bundle.getString("id");
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(driverID);
            myRef.child("busy").setValue(true);

            listenForConfirmation(myRef);
        }

        cancel = (Button) mView.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new JourneyFragment());
                fragmentTransaction.commit();

                if (driverID != null) {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(driverID);
                    myRef.child("busy").setValue(false);
                }
            }
        });

        return mView;
    }

    private void listenForConfirmation(DatabaseReference myRef){
        myRef.child("confirmed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    boolean confirmed = dataSnapshot.getValue(Boolean.class);

                    if (!firstDriverCall) {
                        if (confirmed) {
                            Toast.makeText(mView.getContext(), "Vairuotojas patvirtino iškvietimą!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mView.getContext(), "Vairuotojas atšaukė iškvietimą!", Toast.LENGTH_LONG).show();
                        }

                        if (getActivity() != null) {
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer, new JourneyFragment());
                            fragmentTransaction.commit();
                        }
                    }

                    firstDriverCall = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (driverID != null) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(driverID);
            myRef.child("busy").setValue(false);
        }
    }
}
