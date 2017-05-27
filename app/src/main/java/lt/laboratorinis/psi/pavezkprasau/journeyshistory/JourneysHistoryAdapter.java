package lt.laboratorinis.psi.pavezkprasau.journeyshistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lt.laboratorinis.psi.pavezkprasau.R;

/**
 * Created by Paulius on 2017-05-21.
 */

public class JourneysHistoryAdapter extends BaseAdapter {

    private ArrayList<Journey> journeys;

    private static LayoutInflater inflater = null;

    public JourneysHistoryAdapter(Context context, ArrayList<Journey> journeys) {
        this.journeys = journeys;

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
        TextView dateTime;
        TextView driverNameSurname;
        TextView price;
        ImageView driverImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_old_journey, null);

        holder.dateTime = (TextView) rowView.findViewById(R.id.dateAndTime);
        holder.driverNameSurname = (TextView) rowView.findViewById(R.id.driverNameSurname);
        holder.price = (TextView) rowView.findViewById(R.id.price);
        holder.driverImage = (ImageView) rowView.findViewById(R.id.imageDriver);

        final Journey journey = journeys.get(position);

        holder.dateTime.setText(journey.getDate() + " " + journey.getTime());
        holder.driverNameSurname.setText(journey.getDriver().getName() + " " + journey.getDriver().getSurname());
        holder.price.setText(journey.getPrice());

        holder.driverImage.setImageResource(R.drawable.user_96_grey); //temporary image

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // // TODO: 2017-05-22 what happens when user clicks on a specific journey?
            }
        });

        return rowView;
    }
}
