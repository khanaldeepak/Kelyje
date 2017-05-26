package lt.laboratorinis.psi.kelyje.journey;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lt.laboratorinis.psi.kelyje.MainActivity;
import lt.laboratorinis.psi.kelyje.R;
import lt.laboratorinis.psi.kelyje.users.Driver;

public class FreeDriversFragment extends Fragment {

    private ArrayList<Driver> drivers;
    private ArrayList<String> IDs;

    private ListView list;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private View mView;

    public FreeDriversFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_free_drivers, container, false);

        drivers = new ArrayList<>();
        IDs = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        searchDrivers();

        return mView;
    }

    private void searchDrivers(){
        final ProgressDialog dialog = new ProgressDialog(mView.getContext());
        dialog.setMessage("Ieškoma...");
        dialog.show();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.child("busy").getValue() != null) {
                        boolean busy = snapshot.child("busy").getValue(boolean.class);

                        if (!busy) {
                            Driver driver = snapshot.getValue(Driver.class);
                            drivers.add(driver);
                            IDs.add(snapshot.getKey());
                        }
                    }
                }

                list = (ListView) mView.findViewById(R.id.list);
                DriversAdapter adapter = new DriversAdapter(mView.getContext(), (MainActivity) getActivity(), drivers, IDs);
                list.setAdapter(adapter);

                dialog.dismiss();

                if (drivers.isEmpty()) {
                    Toast.makeText(mView.getContext(), "Atsiprašome, šiuo metu laisvų vairutojų nerasta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
