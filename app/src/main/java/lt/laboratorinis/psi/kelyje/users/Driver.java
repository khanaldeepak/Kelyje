package lt.laboratorinis.psi.kelyje.users;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Paulius on 2017-05-16.
 */

@IgnoreExtraProperties
public class Driver extends Passenger {

    private String mark;
    private String plate;
    private String years;
    private int rating;
    private boolean busy;

    public Driver() {

    }

    public Driver(String name, String surname, String phone, String mark, String plate, String years) {
        super(name, surname, phone);

        this.mark = mark;
        this.plate = plate;
        this.years = years;

        rating = 0;
        busy = false;
    }

    public String getMark() {
        return mark;
    }

    public String getPlate() {
        return plate;
    }

    public String getYears() {
        return years;
    }

    public boolean isBusy() {
        return busy;
    }

    public int getRating() {
        return rating;
    }
}
