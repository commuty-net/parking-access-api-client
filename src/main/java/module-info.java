module commuty.parking.access.client {
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.module.paranamer;

    opens net.commuty.parking.configuration;
    opens net.commuty.parking.model to com.fasterxml.jackson.databind;
    opens net.commuty.parking.http.request to com.fasterxml.jackson.databind;
    opens net.commuty.parking.http.response to com.fasterxml.jackson.databind;

    exports net.commuty.parking;
    exports net.commuty.parking.exception;
    exports net.commuty.parking.model;

    uses com.fasterxml.jackson.databind.Module;
}