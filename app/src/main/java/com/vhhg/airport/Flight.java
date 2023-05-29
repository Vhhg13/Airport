package com.vhhg.airport;

import android.content.Intent;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.LinkedList;

public class Flight {
    private final int ID;
    private final String from;
    private final String to;
    private final Date depart;
    private final Date arrive;
    private final double price;

    public Flight(int ID, String from, String to, Date depart, Date arrive, double price) {
        this.ID = ID;
        this.from = from;
        this.to = to;
        this.depart = depart;
        this.arrive = arrive;
        this.price = price;
    }
    public static Flight[] arrayFrom(String xml)throws XmlPullParserException, IOException{
        LinkedList<Flight> lst = new LinkedList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        int ID=42, price=42;
        String from="Error parsing", to="Error parsing";
        Date depart=new Date(), arrive=new Date();
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
                lst.add(new Flight(ID, from, to, depart, arrive, price));
            }
            parser.next();
        }
        Flight[] flights = new Flight[lst.size()];
        for(int i=0;i < flights.length;++i)
            flights[i] = lst.pollFirst();
        return flights;
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

    public double getPrice() {
        return price;
    }

}
