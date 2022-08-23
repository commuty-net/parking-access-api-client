package net.commuty.parking.http;

import org.slf4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

public class HttpClient {

    private static final Logger LOG = getLogger(HttpClient.class);

    private static final String GET = "GET";
    private static final String POST = "GET";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN_TEMPLATE = "Bearer %s";

    private final URL baseUrl;
    private final Mapper mapper;
    private final Proxy proxy;
    private final int connectionTimeoutInMs;
    private final int requestTimeoutInMs;

    public HttpClient(URL baseUrl,
                      Mapper mapper,
                      Proxy proxy,
                      int connectionTimeoutInMs,
                      int requestTimeoutInMs) {
        this.baseUrl = baseUrl;
        this.mapper = mapper;
        this.proxy = proxy;
        this.connectionTimeoutInMs = connectionTimeoutInMs;
        this.requestTimeoutInMs = requestTimeoutInMs;
    }

    public <T> T makeGetRequest(String path, String token, Map<String, Collection<String>> requestParams, Class<T> type) throws HttpClientException, HttpRequestException {
        try {
            URL url = buildUrl(path, toQueryString(requestParams));
            HttpURLConnection connection = createGetConnection(url, token);

            return executeMethod(connection, type);
        } catch (IOException e) {
            LOG.trace("Unrecoverable issue when trying to build the HTTP Client");
            throw new HttpClientException(e);
        }
    }

    public <T> T makePostRequest(String path, String token, Object body, Class<T> type) throws HttpClientException, HttpRequestException {
        try {
            URL url = buildUrl(path);
            HttpURLConnection connection = createPostConnection(url, token);
            writeRequestBody(body, connection);
            return executeMethod(connection, type);
        } catch (IOException e) {
            LOG.trace("Unrecoverable issue when trying to build the HTTP Client");
            throw new HttpClientException(e);
        }
    }

    private void writeRequestBody(Object body, HttpURLConnection connection) throws HttpClientException {
        try (DataOutputStream payloadStream = new DataOutputStream(connection.getOutputStream())) {
            payloadStream.writeBytes(mapper.write(body));
        } catch (IOException e) {
            LOG.trace("Unrecoverable issue when creating a message body for the HTTP Client");
            throw new HttpClientException(e);
        }
    }

    private <T> T executeMethod(HttpURLConnection connection, Class<T> type) throws IOException, HttpRequestException {
        connection.connect();
        try (InputStream stream = connection.getInputStream()) {
            LOG.trace("{} [{}] {}", connection.getRequestMethod(), connection.getResponseCode(), connection.getURL());
            return mapper.read(stream, type);
        } catch (IOException e) {
            LOG.trace("{} [{}] {}", connection.getRequestMethod(), connection.getResponseCode(), connection.getURL());
            throw wrapToHttpRequestException(connection);
        }
    }

    private HttpRequestException wrapToHttpRequestException(HttpURLConnection connection) throws IOException {
        try (InputStream stream = connection.getErrorStream()) {
            if (stream == null) {
                throw new IOException("No error stream available");
            }
            return new HttpRequestException(connection.getResponseCode(), mapper.readError(stream));
        } catch (IOException e) {
            LOG.trace("Error stream is empty or not readable, returning only the response code");
            return new HttpRequestException(connection.getResponseCode(), null);
        }
    }

    private String toQueryString(Map<String, Collection<String>> queryParameters) {
        String params = queryParameters.entrySet().stream().map(HttpClient::toQueryParam).collect(joining("&"));
        return params.trim().isEmpty() ? "" : "?" + params.trim();
    }

    private static String toQueryParam(Map.Entry<String, Collection<String>> entry) {
        return entry.getValue()
                .stream()
                .map(value -> String.format("%s=%s", entry.getKey(), value))
                .collect(joining("&"));
    }

    private URL buildUrl(String path) throws MalformedURLException {
        return buildUrl(path, "");
    }

    private URL buildUrl(String path, String queryParams) throws MalformedURLException {
        return new URL(baseUrl, path + queryParams);
    }

    private HttpURLConnection createGetConnection(URL url, String token) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod(GET);
        connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
        if (token != null && !token.trim().isEmpty()) {
            connection.setRequestProperty(AUTHORIZATION, String.format(TOKEN_TEMPLATE, token));
        }
        connection.setConnectTimeout(this.connectionTimeoutInMs);
        connection.setReadTimeout(this.requestTimeoutInMs);
        return connection;
    }

    private HttpURLConnection createPostConnection(URL url, String token) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod(POST);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
        if (token != null && !token.trim().isEmpty()) {
            connection.setRequestProperty(AUTHORIZATION, String.format(TOKEN_TEMPLATE, token));
        }
        connection.setConnectTimeout(this.connectionTimeoutInMs);
        connection.setDoOutput(true);
        return connection;
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        return proxy != null ? (HttpURLConnection) url.openConnection(proxy) : (HttpURLConnection) url.openConnection();
    }
}
