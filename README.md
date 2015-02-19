WXR2Roller
==========

This imports entries from RSS 2.0 / WordPress WXR (but not tested with real one yet!) XML into Apache Roller.

Usage
-----

1. Install Apache Roller (Tested with 5.1.1) into your local Maven repository 
2. clone this repository
3. cd wxr2roller; mvn clean package
4. Edit run.sh and conf/roller-custom.properties to suit your environment
5. ./run.sh -file [PATH_TO_XML_FILE] -handle [HANDLE_OF_DESIRED_BLOG] -user [USER_NAME]