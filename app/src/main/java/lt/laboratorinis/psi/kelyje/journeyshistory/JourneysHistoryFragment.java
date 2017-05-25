package lt.laboratorinis.psi.kelyje.journeyshistory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lt.laboratorinis.psi.kelyje.R;
import lt.laboratorinis.psi.kelyje.periodicjourney.PeriodicJourney;
import lt.laboratorinis.psi.kelyje.periodicjourney.PeriodicJourneysAdapter;
import lt.laboratorinis.psi.kelyje.users.Driver;

public class JourneysHistoryFragment extends Fragment {

    private ArrayList<Journey> journeys;

    private ListView list;
    private View mView;

    public JourneysHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_journeys_history, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Driver driver = new Driver("Tomas", "Petronis", "86123456", "Audi A4", "ABC 1230", "2001");
        Journey journey = new Journey(driver, "4.99", "2017 05 22", "17:11");

        if (user != null) {
            String id = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(id).child("history");

            //fake
            /*String key = myRef.push().getKey();
            myRef.child(key).setValue(journey);*/
            //fake

            myRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            journeys = new ArrayList<>();

                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                Journey journey = dsp.getValue(Journey.class);
                                journeys.add(journey);
                            }

                            if (journeys.isEmpty()) {
//                                Toast.makeText(mView.getContext(), "No previous journeys found!", Toast.LENGTH_LONG).show();
                                Toast.makeText(mView.getContext(), "Nerasta ankstesnių kelionių!", Toast.LENGTH_LONG).show();
                            } else {
                                list = (ListView) mView.findViewById(R.id.listView);
                                JourneysHistoryAdapter adapter = new JourneysHistoryAdapter(mView.getContext(), journeys);
                                list.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }

        return mView;
    }
}
