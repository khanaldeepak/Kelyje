package lt.laboratorinis.psi.pavezkprasau.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lt.laboratorinis.psi.pavezkprasau.R;

public class HelpPageFragment extends Fragment{

    private View mView;

    public HelpPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.help_page, container, false);

        return mView;
    }
}
