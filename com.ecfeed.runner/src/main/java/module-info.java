module com.ecfeed {
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;
    exports com.ecfeed;
    exports com.ecfeed.helper;
    exports com.ecfeed.parser;
    exports com.ecfeed.params;
}