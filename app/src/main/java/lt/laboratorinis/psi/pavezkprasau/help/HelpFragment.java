package lt.laboratorinis.psi.pavezkprasau.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import lt.laboratorinis.psi.pavezkprasau.R;

public class HelpFragment extends Fragment{

    private Button help1;
    private Button help2;
    private Button help3;
    private Button help4;
    private Button help5;

    private View mView;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_help, container, false);

        help1 = (Button) mView.findViewById(R.id.help1);
        help2 = (Button) mView.findViewById(R.id.help2);
        help3 = (Button) mView.findViewById(R.id.help3);
        help4 = (Button) mView.findViewById(R.id.help4);
        help5 = (Button) mView.findViewById(R.id.help5);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                switch (id) {
                    case R.id.help1:
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, new HelpPageFragment());
                        fragmentTransaction.commit();
                        break;
                    case R.id.help2:
                        Toast.makeText(mView.getContext(), "Atsiprašome, laikinai neveikia!", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.help3:
                        Toast.makeText(mView.getContext(), "Atsiprašome, laikinai neveikia!", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.help4:
                        Toast.makeText(mView.getContext(), "Atsiprašome, laikinai neveikia!", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.help5:
                        Toast.makeText(mView.getContext(), "Atsiprašome, laikinai neveikia!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };

        help1.setOnClickListener(listener);
        help2.setOnClickListener(listener);
        help3.setOnClickListener(listener);
        help4.setOnClickListener(listener);
        help5.setOnClickListener(listener);

        return mView;
    }
}
