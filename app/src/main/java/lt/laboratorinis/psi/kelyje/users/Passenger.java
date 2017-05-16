package lt.laboratorinis.psi.kelyje.users;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Paulius on 2017-05-16.
 */

@IgnoreExtraProperties
public class Passenger extends User {

    public Passenger() {

    }

    public Passenger(String phone) {
        super(phone);
    }

    public Passenger(String name, String surname, String phone) {
        super(name, surname, phone);
    }
}
