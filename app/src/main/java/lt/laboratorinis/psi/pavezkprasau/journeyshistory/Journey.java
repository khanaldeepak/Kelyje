package lt.laboratorinis.psi.pavezkprasau.journeyshistory;

import com.google.firebase.database.IgnoreExtraProperties;

import lt.laboratorinis.psi.pavezkprasau.users.Driver;

/**
 * Created by Paulius on 2017-05-22.
 */

@IgnoreExtraProperties
public class Journey {

    private Driver driver;
    private String price;
    private String date;
    private String time;

    public Journey(){

    }

    public Journey(Driver driver, String price, String date, String time) {
        this.driver = driver;
        this.price = price;
        this.date = date;
        this.time = time;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
