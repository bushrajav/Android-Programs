package com.example.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmldata) {
        boolean status = true;
        boolean inEntry = false;
        FeedEntry currentRecord = null;
        String text = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmldata));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) { //1
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: //2
                      //  Log.d(TAG, "parse: starting tag " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT: //4
                        text = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG: //3
                        //Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(text);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(text);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(text);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(text);
                            } else if ("imageURL".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(text);
                            }
                        }
                        break;
                    default:
                        //nothing
                }
                eventType = xpp.next();
            }
//            for (FeedEntry e : applications) {
//                Log.d(TAG, "parse: **********************");
//                Log.d(TAG, "parse: " + e.toString());
//            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;

    }
}

