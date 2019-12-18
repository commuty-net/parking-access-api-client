module commuty.parking.access.client {
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens net.commuty.parking.rest to com.fasterxml.jackson.databind;
    opens net.commuty.parking.model to com.fasterxml.jackson.databind;
    opens net.commuty.parking.http to com.fasterxml.jackson.databind;

    exports net.commuty.parking;
    exports net.commuty.parking.model;
    exports net.commuty.parking.http;
}