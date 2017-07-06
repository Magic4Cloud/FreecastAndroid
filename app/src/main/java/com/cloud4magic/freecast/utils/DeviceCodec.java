package com.cloud4magic.freecast.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * media codec
 * Date    2017/7/6
 * Author  xiaomao
 */

public class DeviceCodec {

    public static InputStream getSystemCodec() {
        // read config: /system/etc/media_codecs.xml
        File file = new File("/system/etc/media_codecs.xml");
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.e("xmzd", "File not found: media_codecs.xml");
        }
        if (in == null) {
            Logger.e("xmzd", "in stream is null");
        } else {
            Logger.e("xmzd", "in stream is not null");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder builder = new StringBuilder();
            try {
                while ((line = br.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.e("xmzd", "content: " + builder.toString());
        }
        return in;
    }

    public static boolean isSupportHardwareDecode() {
        boolean hard = false;
        InputStream in = getSystemCodec();
        XmlPullParserFactory pullFactory;
        try {
            pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = pullFactory.newPullParser();
            xmlPullParser.setInput(in, "UTF-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xmlPullParser.getName();
                Logger.e("xmzd", "eventType: " + eventType + " --- tagName: " + tagName);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("MediaCodecs".equals(tagName)) {
                            int count = xmlPullParser.getAttributeCount();
                            Logger.e("xmzd", "count: " + count);
                            String componentName = xmlPullParser.getAttributeValue(0);
                            Logger.e("xmzd", "MediaCodec: " + componentName);
                            if (componentName.startsWith("OMX.")) {
                                if (!componentName.startsWith("OMX.google.")) {
                                    hard = true;
                                }
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = xmlPullParser.getText();
                        Logger.e("xmzd", "TEXT: " + text);
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            Logger.e("xmzd", "XmlPullParserException: " + e.toString());
        } catch (IOException e) {
            Logger.e("xmzd", "IOException: " + e.toString());
        }
        return hard;
    }

}
