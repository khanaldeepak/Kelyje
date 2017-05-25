package lt.laboratorinis.psi.kelyje.journey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import lt.laboratorinis.psi.kelyje.R;

public class FreeDriversFragment extends Fragment implements View.OnClickListener{

    private View mView;

    public FreeDriversFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_free_drivers, container, false);

        LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.driver1);
        linearLayout.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new DriverPageFragment());
        fragmentTransaction.commit();
    }
}
