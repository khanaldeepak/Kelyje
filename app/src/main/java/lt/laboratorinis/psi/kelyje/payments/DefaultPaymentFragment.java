package lt.laboratorinis.psi.kelyje.payments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lt.laboratorinis.psi.kelyje.R;

public class DefaultPaymentFragment extends Fragment {

    public DefaultPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_payment, container, false);
    }
}
