package fr.clement.rennsurrection.bluesound.Objects;


import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Laure on 05/02/2017.
 */

public class Speaker implements Parcelable{
    private int volume;
    private String name_speaker;
    private String address;


    public Speaker(String ename_speaker, String eaddress, int evolume) {
        this.volume = evolume;
        this.name_speaker = ename_speaker;
        this.address = eaddress;
    }

    public int describeContents() {
        return 0;
    }

    public String getNom(){
        return this.name_speaker;
    }

    public String getAddress(){
        return this.address;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name_speaker);
        dest.writeString(this.address);
        dest.writeInt(this.volume);
    }

    public static final Creator<Speaker> CREATOR = new Creator<Speaker>() {
        @Override
        public Speaker createFromParcel(Parcel source) {
            return new Speaker(source);
        }
        @Override
        public Speaker[] newArray(int size) {
            return new Speaker[size];
        }

    };

    public Speaker(Parcel in) {
        this.name_speaker = in.readString();
        this.address = in.readString();
        this.volume = in.readInt();

    }

    public void setName_speaker(String nom){ this.name_speaker = nom; }

    public void setVolume(int evolume){
        this.volume = evolume;
    }

    public int getVolume(){
        return this.volume;
    }

    public String toString(){
        return this.name_speaker + " " + this.address + " Volume : " + this.volume;
    }
}