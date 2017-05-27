package lt.laboratorinis.psi.pavezkprasau.payments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import lt.laboratorinis.psi.pavezkprasau.R;

public class DefaultPaymentFragment extends Fragment {

    private LinearLayout wallet;
    private LinearLayout card;
    private LinearLayout paypal;
    private Button confirm;

    private View mView;

    public DefaultPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_default_payment, container, false);

        wallet = (LinearLayout) mView.findViewById(R.id.layoutWallet);
        card = (LinearLayout) mView.findViewById(R.id.layoutCard);
        paypal = (LinearLayout) mView.findViewById(R.id.layoutPaypal);
        confirm = (Button) mView.findViewById(R.id.btnConfirm);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                switch (id) {
                    case R.id.layoutWallet:
                        //
                        break;
                    case R.id.layoutCard:
                        Toast.makeText(mView.getContext(), "Neįgalinta", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.layoutPaypal:
                        Toast.makeText(mView.getContext(), "Neįgalinta", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnConfirm:
                        saveChanges();
                        break;
                    default:
                        break;
                }
            }
        };

        wallet.setOnClickListener(listener);
        card.setOnClickListener(listener);
        paypal.setOnClickListener(listener);
        confirm.setOnClickListener(listener);

        return mView;
    }

    private void saveChanges(){
        Toast.makeText(mView.getContext(), "Išsaugota!", Toast.LENGTH_SHORT).show();
    }
}
