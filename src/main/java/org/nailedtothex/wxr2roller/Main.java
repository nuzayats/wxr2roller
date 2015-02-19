package org.nailedtothex.wxr2roller;

import org.apache.commons.cli.*;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.startup.WebloggerStartup;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    private static final String DERBY_SHUTDOWN = "derbyShutdown";
    private static final String HANDLE = "handle";
    private static final String USER = "user";
    private static final String FILE = "file";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        {
            Option option = new Option(DERBY_SHUTDOWN, false, "Add a shutdown hook which executes shutdown of Embedded Derby.");
            options.addOption(option);
        }
        {
            Option option = new Option(HANDLE, true, "The handle of weblog");
            option.setRequired(true);
            options.addOption(option);
        }
        {
            Option option = new Option(USER, true, "The user name of creator of entries to be imported");
            option.setRequired(true);
            options.addOption(option);
        }
        {
            Option option = new Option(FILE, true, "RSS 2.0 / WXR file");
            option.setRequired(true);
            options.addOption(option);
        }

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(DERBY_SHUTDOWN)) {
                Util.registerDerbyShutdownHook();
            }

            final String handle = cmd.getOptionValue(HANDLE);
            final String user = cmd.getOptionValue(USER);
            final String file = cmd.getOptionValue(FILE);

            exec(new File(file), handle, user);
            log.info("done. please rebuild the search indexes of the blog.");
        } catch (MissingOptionException e) {
            HelpFormatter formatter = new HelpFormatter();
            log.severe(e.getMessage());
            formatter.printHelp("run", options);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static void exec(File wxr, String weblogHandle, String userName) throws WebloggerException, ParserConfigurationException, SAXException, IOException {
        WebloggerStartup.prepare();
        WebloggerFactory.bootstrap();
        final Weblogger roller = WebloggerFactory.getWeblogger();
        final WXRHandler handler = new WXRHandler(roller, roller.getWeblogManager().getWeblogByHandle(weblogHandle), userName);
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser parser = factory.newSAXParser();
        parser.parse(wxr, handler);
    }
}
