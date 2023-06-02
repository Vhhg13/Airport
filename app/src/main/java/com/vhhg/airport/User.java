package com.vhhg.airport;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {
    private final int ID;

    private final String firstName;
    private final String lastName;
    private final String thirdName;
    private final String profilePic;

    public User(int ID, String firstName, String lastName, String thirdName, String profilePic) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.thirdName = thirdName;
        this.profilePic = profilePic;
    }

    public int getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public static void listFrom(String xml, List<User> lst) throws XmlPullParserException, IOException{
        lst.clear();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        while(parser.getEventType()!=XmlPullParser.END_DOCUMENT){
            if(parser.getEventType() == XmlPullParser.START_TAG
                    && parser.getName().equalsIgnoreCase("user")){
                User user = new User(
                        Integer.parseInt(parser.getAttributeValue(0)),
                        parser.getAttributeValue(1),
                        parser.getAttributeValue(2),
                        parser.getAttributeValue(3),
                        parser.getAttributeValue(4));
                lst.add(user);
            }
            parser.next();
        }
    }
    public static List<User> listFrom(String xml) throws XmlPullParserException, IOException{
        LinkedList<User> lst = new LinkedList<>();
        listFrom(xml, lst);
        return lst;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", thirdName='" + thirdName + '\'' +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }
}
