package lt.laboratorinis.psi.pavezkprasau.journey;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import lt.laboratorinis.psi.pavezkprasau.MainActivity;
import lt.laboratorinis.psi.pavezkprasau.R;
import lt.laboratorinis.psi.pavezkprasau.profile.ProfileFragment;

public class JourneyFragment extends Fragment {

    private EditText source;
    private EditText destination;
    private CheckBox gps;
    private EditText hours;
    private EditText mins;
    private Button start;
    private CheckBox traditional;
    private CheckBox selfDriving;
    private CheckBox packet;

    private MapView mMapView;
    private GoogleMap googleMap;

    private static final int LOCATION_REQUEST_CODE = 101;

    private View mView;

    public JourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_journey, container, false);

        source = (EditText) mView.findViewById(R.id.editSource);
        destination = (EditText) mView.findViewById(R.id.editDestination);
        gps = (CheckBox) mView.findViewById(R.id.radioGPS);
        hours = (EditText) mView.findViewById(R.id.editHours);
        mins = (EditText) mView.findViewById(R.id.editMins);
        start = (Button) mView.findViewById(R.id.btnStart);
        traditional = (CheckBox) mView.findViewById(R.id.checkTraditional);
        selfDriving = (CheckBox) mView.findViewById(R.id.checkSelfDriving);
        packet = (CheckBox) mView.findViewById(R.id.checkPacket);

        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR);
        int currentMinute = c.get(Calendar.MINUTE);

        hours.setText(String.valueOf(currentHour));
        mins.setText(String.valueOf(currentMinute));

        gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                source.setEnabled(!b);

                if (b) {
                    source.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    source.setBackgroundColor(ContextCompat.getColor(mView.getContext(), android.R.color.white));
                }
            }
        });

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

                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validateInput();
                    }
                });

                /*LatLng MUSEUM = new LatLng(38.8874245, -77.0200729);
                Marker museum = googleMap.addMarker(new MarkerOptions()
                                    .position(MUSEUM)
                                    .title("Museum")
                                    .snippet("National Air and Space Museum"));*/

                // For dropping a marker at a point on the Map
                /*LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));*/

                // For zooming automatically to the location of the marker
                /*CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            }
        });

        return mView;
    }

    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(getContext(), permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {permissionType}, requestCode
            );
        } else {
            showCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getContext(), "Unable to show location - permission required", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Nepavyko rasti lokacijos - reikia leidimo", Toast.LENGTH_LONG).show();
                } else {
                    showCurrentLocation();
                }
            }
        }
    }

    private void showCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = null;

        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        if (location != null && googleMap != null)
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void validateInput(){
        String sourceInput = source.getText().toString().trim();
        String destinationInput = destination.getText().toString().trim();
        String hoursInput = hours.getText().toString().trim();
        String minutesInput = mins.getText().toString().trim();

        if (TextUtils.isEmpty(destinationInput)) {
            Toast.makeText(mView.getContext(), "Įveskite kelionės tikslo adresą!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!gps.isChecked() && TextUtils.isEmpty(sourceInput)) {
            Toast.makeText(mView.getContext(), "Įveskite kelionės pradžios adresą!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(hoursInput) || TextUtils.isEmpty(minutesInput)) {
            Toast.makeText(mView.getContext(), "Įveskite kelionės laiką!", Toast.LENGTH_LONG).show();
            return;
        }

        int hoursNumber = Integer.parseInt(hoursInput);
        if (hoursNumber < 0 || hoursNumber > 23) {
            Toast.makeText(mView.getContext(), "Klaidinga valanda! Turi būti [0 - 23]", Toast.LENGTH_LONG).show();
            return;
        }

        int minutesNumber = Integer.parseInt(minutesInput);
        if (minutesNumber < 0 || minutesNumber > 59) {
            Toast.makeText(mView.getContext(), "Klaidingos minutės! Turi būti [0 - 59]", Toast.LENGTH_LONG).show();
            return;
        }

        if (!traditional.isChecked()) {
            Toast.makeText(mView.getContext(), "Pasirinkite tradicinį automobilio tipą! (kiti kol kas neįgalinti)", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new FreeDriversFragment());
        fragmentTransaction.commit();
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
