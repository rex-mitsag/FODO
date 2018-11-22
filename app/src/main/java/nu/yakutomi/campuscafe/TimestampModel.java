package nu.yakutomi.campuscafe;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TimestampModel {
    private String mTimestamp;

    public TimestampModel() {}  // Needed for Firebase

    public TimestampModel(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getTimestamp() { return mTimestamp; }
    public void setTimestamp(String timestamp) { this.mTimestamp = timestamp; }
}