package lt.laboratorinis.psi.kelyje.journey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import lt.laboratorinis.psi.kelyje.R;

public class DriverPageFragment extends Fragment {

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
