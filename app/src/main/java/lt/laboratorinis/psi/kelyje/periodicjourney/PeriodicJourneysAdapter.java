package lt.laboratorinis.psi.kelyje.periodicjourney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import lt.laboratorinis.psi.kelyje.R;

/**
 * Created by Paulius on 2017-05-21.
 */

public class PeriodicJourneysAdapter extends BaseAdapter {

    private ArrayList<PeriodicJourney> journeys;
    private ArrayList<String> IDs;
    private Context context;

    private static LayoutInflater inflater = null;

    public PeriodicJourneysAdapter(Context context, ArrayList<PeriodicJourney> journeys, ArrayList<String> IDs) {
        this.context = context;
        this.journeys = journeys;
        this.IDs = IDs;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return journeys.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView path;
        TextView time;
        TextView days;
        ImageView active;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.periodic_journey, null);

        holder.path = (TextView) rowView.findViewById(R.id.path);
        holder.time = (TextView) rowView.findViewById(R.id.time);
        holder.days = (TextView) rowView.findViewById(R.id.days);
        holder.active = (ImageView) rowView.findViewById(R.id.active);

        final PeriodicJourney journey = journeys.get(position);

        holder.path.setText(journey.getSource() + " - " + journey.getDestination());
        holder.time.setText(journey.getHour() + " : " + journey.getMinute());

        StringBuilder builder = new StringBuilder();
        if (journey.isMonday()){
            builder.append("I ");
        }
        if (journey.isTuesday()){
            builder.append("II ");
        }
        if (journey.isWednesday()){
            builder.append("III ");
        }
        if (journey.isThursday()){
            builder.append("IV ");
        }
        if (journey.isFriday()){
            builder.append("V ");
        }
        if (journey.isSaturday()){
            builder.append("VI ");
        }
        if (journey.isSunday()){
            builder.append("VII ");
        }

        String weekDays = builder.toString();

        holder.days.setText(weekDays);
        if (journeys.get(position).isActive()) {
            holder.active.setImageResource(R.drawable.ok_512);
        } else {
            holder.active.setImageResource(R.drawable.cancel_512);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PeriodicJourney journey = journeys.get(position);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Periodic journey");
                alertDialog.setIcon(journey.isActive() ? R.drawable.ok_512 : R.drawable.cancel_512);
                alertDialog.setMessage(holder.path.getText().toString().trim());

                alertDialog.setPositiveButton("Close",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });

                String activeText = journey.isActive() ? "Make inactive" : "Activate";

                alertDialog.setNeutralButton(activeText,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                if (journey.isActive()) {
                                    journey.setActive(false);
                                    holder.active.setImageResource(R.drawable.cancel_512);
                                } else {
                                    journey.setActive(true);
                                    holder.active.setImageResource(R.drawable.ok_512);
                                }

                                saveChanges(IDs.get(position), journey.isActive());
                                dialog.cancel();
                            }
                        });

                alertDialog.setNegativeButton("Delete",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                journeys.remove(position);
                                notifyDataSetChanged();
                                deletePeriodicJourney(IDs.get(position));
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        return rowView;
    }

    private void saveChanges(String journeyId, boolean value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String id = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().
                    getReference("users").child(id).child("periodic").child(journeyId);

            myRef.child("active").setValue(value);
        }
    }

    private void deletePeriodicJourney(String journeyId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String id = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().
                    getReference("users").child(id).child("periodic");

            myRef.child(journeyId).removeValue();
        }
    }
}
