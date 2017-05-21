package lt.laboratorinis.psi.kelyje.periodicjourney;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lt.laboratorinis.psi.kelyje.R;

public class PeriodicJourneyPathFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;

    private static final int LOCATION_REQUEST_CODE = 101;

    private EditText source;
    private EditText destination;
    private Button savePeriodicJourney;

    private View mView;

    public PeriodicJourneyPathFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_periodic_journey_path, container, false);

        source = (EditText) mView.findViewById(R.id.editSource);
        destination = (EditText) mView.findViewById(R.id.editDestination);

        final Bundle bundle = this.getArguments();

        initializeMaps(savedInstanceState);

        savePeriodicJourney = (Button) mView.findViewById(R.id.btnSave);
        savePeriodicJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] days = bundle.getBooleanArray("weekdays");
                int hour = bundle.getInt("hour");
                int minute = bundle.getInt("minute");
                boolean traditional = bundle.getBoolean("traditional");
                boolean selfDriving = bundle.getBoolean("selfDriving");
                boolean packet = bundle.getBoolean("packet");

                String sourceInput = source.getText().toString().trim();
                String destinationInput = destination.getText().toString().trim();

                if (checkAddresses(sourceInput, destinationInput)) {
                    PeriodicJourney journey = new PeriodicJourney(days, hour, minute, traditional, selfDriving, packet, sourceInput, destinationInput);
                    saveJourney(journey);
                    Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new PeriodicJourneyFragment());
                    fragmentTransaction.commit();
                }
            }
        });

        return mView;
    }

    private void initializeMaps(Bundle savedInstanceState){
        // ask for permission
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);

        mMapView = (MapView) mView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                UiSettings mapSettings = googleMap.getUiSettings();
                mapSettings.setZoomControlsEnabled(true);
                mapSettings.setCompassEnabled(true);
                mapSettings.setZoomGesturesEnabled(true);
                mapSettings.setScrollGesturesEnabled(true);

                // For showing a move to my location button
                if (googleMap != null) {
                    try {
                        googleMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

                LatLng MUSEUM = new LatLng(38.8874245, -77.0200729);
                Marker museum = googleMap.addMarker(new MarkerOptions()
                        .position(MUSEUM)
                        .title("Museum")
                        .snippet("National Air and Space Museum"));

                // For dropping a marker at a point on the Map
                /*LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));*/

                // For zooming automatically to the location of the marker
                /*CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            }
        });
    }

    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(getContext(), permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {permissionType}, requestCode
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Unable to show location - permission required", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkAddresses(String source, String destination) {
        if (TextUtils.isEmpty(source)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter source address!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(destination)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter destination address!", Toast.LENGTH_LONG).show();
            return false;
        }

        // TODO: 2017-05-21 additional validation of addresses

        return true;
    }

    private void saveJourney(PeriodicJourney journey) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String id = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(id).child("periodic");

            String key = myRef.push().getKey();
            myRef.child(key).setValue(journey);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
