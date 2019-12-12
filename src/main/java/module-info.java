module commuty.parking.access.client {
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.module.paranamer;

    opens net.commuty.configuration;
    opens net.commuty.model to com.fasterxml.jackson.databind;
    opens net.commuty.http.request to com.fasterxml.jackson.databind;
    opens net.commuty.http.response to com.fasterxml.jackson.databind;

    exports net.commuty;
    exports net.commuty.exception;
    exports net.commuty.model;

    uses com.fasterxml.jackson.databind.Module;
}