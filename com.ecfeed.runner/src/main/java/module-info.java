module com.ecfeed {
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;
    exports com.ecfeed;
    exports com.ecfeed.params;
    exports com.ecfeed.type;
    exports com.ecfeed.queue;
}