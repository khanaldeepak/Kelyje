package lt.laboratorinis.psi.pavezkprasau.periodicjourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lt.laboratorinis.psi.pavezkprasau.R;

public class PeriodicJourneyFragment extends Fragment {

    private ArrayList<PeriodicJourney> journeys;
    private ArrayList<String> journeyIds;

    private ListView list;
    private Button addPeriodicJourney;

    private View mView;

    public PeriodicJourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_periodic_journey, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String id = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(id).child("periodic");

            myRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            journeys = new ArrayList<>();
                            journeyIds = new ArrayList<>();

                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                PeriodicJourney journey = dsp.getValue(PeriodicJourney.class);
                                journeys.add(journey);

                                String id = dsp.getKey();
                                journeyIds.add(id);
                            }

                            list = (ListView) mView.findViewById(R.id.listView);
                            PeriodicJourneysAdapter adapter = new PeriodicJourneysAdapter(mView.getContext(), journeys, journeyIds);
                            list.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }

        addPeriodicJourney = (Button) mView.findViewById(R.id.btnAdd);
        addPeriodicJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new PeriodicJourneyTimeFragment());
                fragmentTransaction.commit();
            }
        });

        return mView;
    }
}
