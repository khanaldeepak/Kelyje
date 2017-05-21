package lt.laboratorinis.psi.kelyje.periodicjourney;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Paulius on 2017-05-20.
 */

@IgnoreExtraProperties
public class PeriodicJourney {

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    private int hour;
    private int minute;

    private boolean traditional;
    private boolean selfDriving;
    private boolean packet;

    private String source;
    private String destination;

    private boolean active;

    public PeriodicJourney(){

    }

    public PeriodicJourney(boolean[] weekDays, int hour, int minute, boolean traditional, boolean selfDriving, boolean packet, String source, String destination) {
        monday = weekDays[0];
        tuesday = weekDays[1];
        wednesday = weekDays[2];
        thursday = weekDays[3];
        friday = weekDays[4];
        saturday = weekDays[5];
        sunday = weekDays[6];

        this.hour = hour;
        this.minute = minute;
        this.traditional = traditional;
        this.selfDriving = selfDriving;
        this.packet = packet;
        this.source = source;
        this.destination = destination;

        active = true;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isPacket() {
        return packet;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public boolean isTraditional() {
        return traditional;
    }

    public boolean isSelfDriving() {
        return selfDriving;
    }

    public String getSource() {
        return source;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
