package de.hsb.mschnelle.grumps.service.ytapiv2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class GrumpXmlParser {

	private XmlPullParser parser;
	
	public GrumpXmlParser() {
		parser = Xml.newPullParser();
	}
	
	public Map<String, String> parse(String xmlString) throws IOException, XmlPullParserException {
		InputStream is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		
		// Init
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(is, "UTF-8");
        parser.nextTag();
        
        String title = "";
        String id = "";
    	
        try {
            parser.require(XmlPullParser.START_TAG, null, "feed");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("entry")) {
                	parser.require(XmlPullParser.START_TAG, null, "entry");
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // ID
                        if (name.equals("id")) {
                        	parser.require(XmlPullParser.START_TAG, null, "id");
                            id = readText(parser);
                            parser.require(XmlPullParser.END_TAG, null, "id");
                        } else if (name.equals("title")) {
                        	parser.require(XmlPullParser.START_TAG, null, "title");
                            title = readText(parser);
                            parser.require(XmlPullParser.END_TAG, null, "title");
                        }
                    }
                	
                }
            } 
    	} finally {
    		is.close();
    	}
        
        // Crop id
        StringTokenizer tokenizer = new StringTokenizer(id, "/");
        while (tokenizer.hasMoreTokens())
        	id = tokenizer.nextToken();
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("id", id);
        
        return map;
	}
	
	private String readText(XmlPullParser p) throws XmlPullParserException, IOException {
		String result = "";
	    if (p.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        p.nextTag();
	    }
	    return result;
	}
}
