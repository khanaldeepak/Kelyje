package lt.laboratorinis.psi.pavezkprasau.payments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lt.laboratorinis.psi.pavezkprasau.R;

public class AddPaypalFragment extends Fragment {

    public AddPaypalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_paypal, container, false);
    }
}
