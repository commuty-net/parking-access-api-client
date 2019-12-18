package net.commuty.parking.http;

import java.io.IOException;
import java.io.InputStream;

public interface Mapper {
    String write(Object body) throws IOException;
    <R> R read(InputStream stream, Class<R> clazz) throws IOException;
    Error readError(InputStream stream) throws IOException;
}
