package lt.laboratorinis.psi.kelyje.periodicjourney;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import lt.laboratorinis.psi.kelyje.R;

public class PeriodicJourneyTimeFragment extends Fragment {

    private Button mondey, tuesday, wednesday, thursday, friday, saturday, sunday;
    private TimePicker timePicker;
    private CheckBox traditional, selfDriving, packet;
    private Button addPeriodicJourney;

    private boolean activeWeekDays[];

    private View mView;

    public PeriodicJourneyTimeFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_periodic_journey_time, container, false);

        mondey = (Button) mView.findViewById(R.id.btnMonday);
        tuesday = (Button) mView.findViewById(R.id.btnTuesday);
        wednesday = (Button) mView.findViewById(R.id.btnWednesday);
        thursday = (Button) mView.findViewById(R.id.btnThursday);
        friday = (Button) mView.findViewById(R.id.btnFriday);
        saturday = (Button) mView.findViewById(R.id.btnSaturday);
        sunday = (Button) mView.findViewById(R.id.btnSunday);

        timePicker = (TimePicker) mView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= 23 ) {
            timePicker.setHour(currentHour);
            timePicker.setMinute(currentMin);
        } else {
            timePicker.setCurrentHour(currentHour);
            timePicker.setCurrentMinute(currentMin);
        }

        traditional = (CheckBox) mView.findViewById(R.id.checkTraditional);
        selfDriving = (CheckBox) mView.findViewById(R.id.checkSelfDriving);
        packet = (CheckBox) mView.findViewById(R.id.checkPacket);

        activeWeekDays = new boolean[]{false, false, false, false, false, false, false};

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable btnColor = (ColorDrawable) view.getBackground();
                int colorId = btnColor.getColor();

                int redId = ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.holo_red_light);
                int greenId = ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.holo_green_light);

                if (colorId == redId) {
                    view.setBackgroundColor(greenId);
                    setActiveWeek(view.getId(), true);
                } else {
                    view.setBackgroundColor(redId);
                    setActiveWeek(view.getId(), false);
                }
            }
        };

        mondey.setOnClickListener(listener);
        tuesday.setOnClickListener(listener);
        wednesday.setOnClickListener(listener);
        thursday.setOnClickListener(listener);
        friday.setOnClickListener(listener);
        saturday.setOnClickListener(listener);
        sunday.setOnClickListener(listener);

        addPeriodicJourney = (Button) mView.findViewById(R.id.btnNext);
        addPeriodicJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInput();
            }
        });

        return mView;
    }

    private void setActiveWeek(int id, boolean status){
        switch (id) {
            case R.id.btnMonday:
                activeWeekDays[0] = status;
                break;
            case R.id.btnTuesday:
                activeWeekDays[1] = status;
                break;
            case R.id.btnWednesday:
                activeWeekDays[2] = status;
                break;
            case R.id.btnThursday:
                activeWeekDays[3] = status;
                break;
            case R.id.btnFriday:
                activeWeekDays[4] = status;
                break;
            case R.id.btnSaturday:
                activeWeekDays[5] = status;
                break;
            case R.id.btnSunday:
                activeWeekDays[6] = status;
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void confirmInput() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= 23 ) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Bundle bundle = new Bundle();

        boolean atLeastOneActive = false;
        for (boolean day: activeWeekDays) {
            if (day) {
                atLeastOneActive = true;
                break;
            }
        }

        if (!atLeastOneActive) {
            Toast.makeText(getActivity().getApplicationContext(), "Please select at least one day!", Toast.LENGTH_LONG).show();
            return;
        }

        bundle.putBooleanArray("weekdays", activeWeekDays);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        bundle.putBoolean("traditional", traditional.isChecked());
        bundle.putBoolean("selfDriving", selfDriving.isChecked());
        bundle.putBoolean("packet", packet.isChecked());

        PeriodicJourneyPathFragment fragment = new PeriodicJourneyPathFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
