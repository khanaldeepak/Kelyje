package lt.laboratorinis.psi.pavezkprasau.journeyshistory;

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

import lt.laboratorinis.psi.pavezkprasau.R;
import lt.laboratorinis.psi.pavezkprasau.users.Driver;

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

                            Driver driver = new Driver("Tomas", "Petronis", "86123456", "BMW x5", "ABC 123", "2012");
                            Journey demoJourney = new Journey(driver, "4.99", "2017 05 22", "17:11");
                            journeys.add(demoJourney);

                            driver = new Driver("Hary", "Doggo", "86987654", "Audi A4", "ASD 987", "2007");
                            demoJourney = new Journey(driver, "6.00", "2017 05 24", "05:55");
                            journeys.add(demoJourney);

                            driver = new Driver("Tomas", "Petronis", "86123456", "BMW x5", "ABC 123", "2012");
                            demoJourney = new Journey(driver, "5.30", "2017 05 27", "08:21");
                            journeys.add(demoJourney);

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
