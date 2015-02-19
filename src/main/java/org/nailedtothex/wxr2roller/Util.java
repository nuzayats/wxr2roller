package org.nailedtothex.wxr2roller;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Util {
    private static final Logger log = Logger.getLogger(Util.class.getName());

    static boolean registered = false;

    static void registerDerbyShutdownHook() {
        if (registered) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException e) {
                    log.info(e.getMessage());
                }
            }
        });
        log.info("Derby shutdown hook registered");
        registered = true;
    }
}
