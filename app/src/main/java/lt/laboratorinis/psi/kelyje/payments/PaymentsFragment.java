package lt.laboratorinis.psi.kelyje.payments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import lt.laboratorinis.psi.kelyje.R;

public class PaymentsFragment extends Fragment{

    private ImageView cash;
    private ImageView card;
    private ImageView paypal;
    private Button defaultPayment;

    private View mView;

    public PaymentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_payments, container, false);

        cash = (ImageView) mView.findViewById(R.id.imageWallet);
        card = (ImageView) mView.findViewById(R.id.imageCard);
        paypal = (ImageView) mView.findViewById(R.id.imagePaypal);
        defaultPayment = (Button) mView.findViewById(R.id.btnChooseDefault);

        //todo: get information about active payment methods

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                switch (id) {
                    case R.id.imageWallet:
                        //todo: if not active - load add fragment, else - dialog box
                        break;
                    case R.id.imageCard:
                        //todo
                        break;
                    case R.id.imagePaypal:
                        //todo
                        break;
                    case R.id.btnChooseDefault:
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, new DefaultPaymentFragment());
                        fragmentTransaction.commit();
                        break;
                    default:
                        break;
                }
            }
        };

        cash.setOnClickListener(listener);
        card.setOnClickListener(listener);
        paypal.setOnClickListener(listener);
        defaultPayment.setOnClickListener(listener);

        return mView;
    }
}
