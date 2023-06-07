package com.vhhg.airport;

import android.content.Intent;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class Flight implements Serializable {
    private final int ID;
    private final String from;
    private final String to;
    private final Date depart;
    private final Date arrive;
    private final int price;
    private boolean fav;
    private int price2;

    public Flight(int ID, String from, String to, Date depart, Date arrive, int price, boolean fav) {
        this.ID = ID;
        this.from = from;
        this.to = to;
        this.depart = depart;
        this.arrive = arrive;
        this.price = price;
        this.fav = fav;
    }

    public Flight(int ID, String from, String to, Date depart, Date arrive, int price, boolean fav, int price2) {
        this.ID = ID;
        this.from = from;
        this.to = to;
        this.depart = depart;
        this.arrive = arrive;
        this.price = price;
        this.fav = fav;
        this.price2 = price2;
    }
    public static Flight[] arrayFrom(String xml)throws XmlPullParserException, IOException{
        LinkedList<Flight> lst = new LinkedList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        int ID=42, price=42;
        String from="Error parsing", to="Error parsing";
        Date depart=new Date(), arrive=new Date();
        boolean fav = false;
        while(parser.getEventType()!=XmlPullParser.END_DOCUMENT){
            int evtType = parser.getEventType();
            String name = parser.getName();
            if(evtType == XmlPullParser.START_TAG
                    && name.equalsIgnoreCase("flight")){
                int count = parser.getAttributeCount();
                ID = Integer.parseInt(parser.getAttributeValue(0));
                from = parser.getAttributeValue(1);
                to = parser.getAttributeValue(2);
                depart = new Date(Integer.parseInt(parser.getAttributeValue(3)));
                arrive = new Date(Integer.parseInt(parser.getAttributeValue(4)));
                price = Integer.parseInt(parser.getAttributeValue(5));
                fav = parser.getAttributeValue(6).equals("1");
                lst.add(new Flight(ID, from, to, depart, arrive, price, fav));
            }
            parser.next();
        }
        Flight[] flights = new Flight[lst.size()];
        for(int i=0;i < flights.length;++i)
            flights[i] = lst.pollFirst();
        return flights;
    }
    public static void listFrom(String xml, LinkedList<Flight> lst) throws XmlPullParserException, IOException{
        lst.clear();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        int ID=42, price=42;
        String from="Error parsing", to="Error parsing";
        Date depart=new Date(), arrive=new Date();
        boolean fav = false;
        while(parser.getEventType()!=XmlPullParser.END_DOCUMENT){
            int evtType = parser.getEventType();
            String name = parser.getName();
            if(evtType == XmlPullParser.START_TAG
                    && name.equalsIgnoreCase("flight")){
                int count = parser.getAttributeCount();
                ID = Integer.parseInt(parser.getAttributeValue(0));
                from = parser.getAttributeValue(1);
                to = parser.getAttributeValue(2);
                depart = new Date(Long.parseLong(parser.getAttributeValue(3)));
                arrive = new Date(Long.parseLong(parser.getAttributeValue(4)));
                price = Integer.parseInt(parser.getAttributeValue(5));
                fav = parser.getAttributeValue(6).equals("1");
                lst.add(new Flight(ID, from, to, depart, arrive, price, fav));
            }
            parser.next();
        }
    }

    public int getID() {
        return ID;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Date getDepart() {
        return depart;
    }

    public Date getArrive() {
        return arrive;
    }

    public int getPrice() {
        return price;
    }
    public boolean isFav(){ return fav; }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String toFindFlightString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return String.format(Locale.getDefault(), "\"%s\" \"%s\" \"%s\" \"%s\" %d %d", from, to, sdf.format(depart), sdf.format(arrive), price, price2);
        //return from + " " + to + " " + sdf.format(depart) + " " + sdf.format(arrive) + " " + price + " " + price2;
    }
}
