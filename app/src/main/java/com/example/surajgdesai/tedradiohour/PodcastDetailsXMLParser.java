package com.example.surajgdesai.tedradiohour;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PodcastDetailsXMLParser {
    static public class PodcastDetailsPullParser {
        static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        StringBuilder xmlInnerText;
        static TedRadioPodcast podcast;

        static public ArrayList<TedRadioPodcast> parsePodcastListPullParser(InputStream in) throws XmlPullParserException, IOException {
            final XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, Xml.Encoding.UTF_8.toString());
            ArrayList<TedRadioPodcast> podCastList = new ArrayList<>();
            boolean isItemElement = false;
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item")) {
                            podcast = new TedRadioPodcast();
                            isItemElement = true;
                        } else if (parser.getName().equals("title") && isItemElement) {
                            podcast.setTitle(parser.nextText());
                        } else if (parser.getName().equals("description") && isItemElement) {
                            podcast.setDescription(parser.nextText().trim());
                        } else if (parser.getName().equals("itunes:image") && isItemElement) {
                            podcast.setImageUrl(parser.getAttributeValue(null, "href"));
                        } else if (parser.getName().equals("pubDate") && isItemElement) {
                            try {
                                podcast.setPublicationDate(new Date(parser.nextText().trim()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (parser.getName().equals("enclosure") && isItemElement) {
                            podcast.setMp3Url(parser.getAttributeValue(null, "url"));
                        } else if (parser.getName().equals("itunes:duration") && isItemElement) {
                            podcast.setDuration(Integer.parseInt(parser.nextText().trim()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            podCastList.add(podcast);
                            podcast = null;
                            isItemElement = false;
                        }
                        break;
                }

                event = parser.next();
            }

            return podCastList;
        }
    }
}