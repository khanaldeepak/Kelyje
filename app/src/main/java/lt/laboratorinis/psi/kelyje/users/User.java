package lt.laboratorinis.psi.kelyje.users;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Paulius on 2017-05-16.
 */

@IgnoreExtraProperties
public class User {

    private String name;
    private String surname;
    private String phone;

    public User() {

    }

    public User(String phone) {
        this.phone = phone;
    }

    public User(String name, String surname, String phone) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }
}
