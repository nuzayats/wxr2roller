package org.nailedtothex.wxr2roller;

import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.WeblogEntryManager;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WXRHandler extends DefaultHandler {
    private static final Logger log = Logger.getLogger(WXRHandler.class.getName());

    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String PUB_DATE = "pubDate";
    private static final String CONTENT_ENCODED = "content:encoded";
    private static final String CATEGORY = "category";
    private static final String TAG = "tag";
    private static final String DOMAIN = "domain";
    private static final String NICKNAME = "nickname";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String NICENAME = "nicename";
    private static final String ITEM = "item";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final Set<String> INTEREST_ELEMENTS = new HashSet<>(Arrays.asList(
            TITLE, LINK, PUB_DATE, CONTENT_ENCODED, CATEGORY));

    private Weblogger weblogger;
    private Weblog weblog;
    private WeblogEntry entry;
    private String userName;
    private StringBuilder sb;
    private Set<String> tags;
    private Attributes attributes;
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    private boolean allowComments;

    public WXRHandler(Weblogger weblogger, Weblog weblog, String userName, boolean allowComments) {
        this.weblogger = weblogger;
        this.weblog = weblog;
        this.userName = userName;
        this.allowComments = allowComments;
    }

    public WXRHandler(Weblogger weblogger, Weblog weblog, String userName) {
        this(weblogger, weblog, userName, true);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (INTEREST_ELEMENTS.contains(qName)) {
            sb = new StringBuilder();
            this.attributes = attributes;
            return;
        }
        if (ITEM.equals(qName)) {
            entry = new WeblogEntry();
            entry.setWebsite(weblog);
            entry.setCreatorUserName(userName);
            entry.setAllowComments(allowComments);
            tags = new HashSet<>();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (sb != null) {
            sb.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (entry == null) {
            return;
        }

        if (ITEM.equals(qName)) {

            StringBuilder tagsSb = new StringBuilder();
            for (String tag : tags) {
                if (tagsSb.length() > 0) {
                    tagsSb.append(' ');
                }
                tagsSb.append(tag);
            }

            try {
                entry.setTagsAsString(tagsSb.toString());
                // register entry
                entry.setStatus(WeblogEntry.PubStatus.PUBLISHED);
                entry.setCommentDays(0);
                entry.setSummary(""); // TODO: need to set excerpt:encoded
                register();
            } catch (WebloggerException e) {
                throw new SAXException(e);
            }

            tags = null;
            entry = null;
            sb = null;
        }

        if (INTEREST_ELEMENTS.contains(qName)) {
            String s = sb.toString();
            switch (qName) {
                case TITLE:
                    entry.setTitle(s);
                    break;
                case LINK:
                    entry.setAnchor(getAnchorFromLink(s));
                    break;
                case PUB_DATE:
                    try {
                        final Timestamp time = new Timestamp(sdf.parse(s).getTime());
                        entry.setPubTime(time);
                        entry.setUpdateTime(time);
                    } catch (ParseException e) {
                        throw new SAXException(e);
                    }
                    break;
                case CONTENT_ENCODED:
                    entry.setText(s);
                    break;
                case CATEGORY:
                    if (TAG.equals(attributes.getValue(DOMAIN))) {
                        String tag = getTag(attributes.getValue(NICENAME), attributes.getValue(NICKNAME), s);
                        tags.add(tag);
                        return;
                    }
                    entry.setCategory(weblog.getWeblogCategory(s));
                    break;
            }
        }
    }

    private void register() throws WebloggerException {
        final WeblogEntryManager weblogEntryManager = weblogger.getWeblogEntryManager();
        weblogEntryManager.saveWeblogEntry(entry);
        weblogger.flush();
        log.info("saved: " + entry);
    }

    private static String getTag(String... candidates) {
        for (String s : candidates) {
            if (s != null) {
                return s;
            }
        }
        throw new IllegalArgumentException();
    }

    private static final Pattern PATTERN = Pattern.compile(".*://.*/([^/]+)/?$");

    static String getAnchorFromLink(String link) {
        final Matcher matcher = PATTERN.matcher(link);
        final boolean b = matcher.find();
        if (b) {
            return matcher.group(1);
        }
        return link;
    }
}
