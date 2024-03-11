package fr.imt_atlantique.messaoudtp2;

// User.java
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable {
    private String name;
    private String surname;
    private String city;
    private String birthday;

    private List<String> phoneNumbers;

    // Constructor
    public User(String name, String surname, String birthday, String city ,List<String> phoneNumbers) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.birthday = birthday;
        this.phoneNumbers = phoneNumbers;

    }

    // necessity method for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(city);
        dest.writeString(birthday);
        dest.writeStringList(phoneNumbers);
    }

    // Parcelable Creator
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Constructor with given Parcel to recreate the object
    private User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        city = in.readString();
        birthday = in.readString();
        phoneNumbers = in.createStringArrayList();

    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }

    public String getBirthday() {
        return birthday;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }


}
