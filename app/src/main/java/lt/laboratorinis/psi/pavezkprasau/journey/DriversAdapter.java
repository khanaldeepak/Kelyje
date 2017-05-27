package lt.laboratorinis.psi.pavezkprasau.journey;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import lt.laboratorinis.psi.pavezkprasau.MainActivity;
import lt.laboratorinis.psi.pavezkprasau.R;
import lt.laboratorinis.psi.pavezkprasau.users.Driver;

/**
 * Created by Paulius on 2017-05-21.
 */

public class DriversAdapter extends BaseAdapter {

    private ArrayList<Driver> drivers;
    private ArrayList<String> IDs;
    private Context context;

    private MainActivity activity;

    private static LayoutInflater inflater = null;

    public DriversAdapter(Context context, MainActivity activity, ArrayList<Driver> drivers, ArrayList<String> IDs) {
        this.context = context;
        this.drivers = drivers;
        this.IDs = IDs;
        this.activity = activity;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return drivers.size();
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
        TextView nameSurname;
        TextView distance;
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;
        ImageView star5;
        ImageView image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.item_driver, null);

        holder.nameSurname = (TextView) rowView.findViewById(R.id.driverNameSurname);
        holder.distance = (TextView) rowView.findViewById(R.id.driverDistance);
        holder.star1 = (ImageView) rowView.findViewById(R.id.star1);
        holder.star2 = (ImageView) rowView.findViewById(R.id.star2);
        holder.star3 = (ImageView) rowView.findViewById(R.id.star3);
        holder.star4 = (ImageView) rowView.findViewById(R.id.star4);
        holder.star5 = (ImageView) rowView.findViewById(R.id.star5);
        holder.image = (ImageView) rowView.findViewById(R.id.driverImage);

        final Driver driver = drivers.get(position);

        holder.nameSurname.setText(driver.getName() + " " + driver.getSurname());
        holder.distance.setText(driver.getMark() + ", " + driver.getYears());

        // try to load image from firebase storage
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference loadImage = storage.child("ProfileImages").child(IDs.get(position));

        loadImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(holder.image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //todo: loading from storage failed - load social networks photo
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverPageFragment fragment = new DriverPageFragment();

                Bundle bundle = new Bundle();
                bundle.putString("nameSurname", driver.getName() + " " + driver.getSurname());
                bundle.putString("id", IDs.get(position));
                bundle.putString("carInfo", driver.getMark() + ", " + driver.getYears());

                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                fragmentTransaction.commit();
            }
        });

        return rowView;
    }
}
