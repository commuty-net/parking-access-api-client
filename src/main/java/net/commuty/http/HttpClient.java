package net.commuty.http;

import net.commuty.configuration.ClientBuilder;
import net.commuty.configuration.JsonMapper;
import net.commuty.exception.HttpClientException;
import net.commuty.exception.HttpRequestException;
import net.commuty.http.request.Requestable;
import net.commuty.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static final String GET = "GET";
    private static final String POST = "GET";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN_TEMPLATE = "Bearer %s";
    private static final int TIMEOUT_IN_MS = 5000;

    private final URL baseUrl;
    private final JsonMapper mapper;
    private final Optional<Proxy> proxy;


    public HttpClient(ClientBuilder builder, JsonMapper mapper) {
        this.baseUrl = builder.getHost();
        this.mapper = mapper;
        this.proxy = builder.getProxy();
    }


    public <T> T makeGetRequest(String path, String token, Map<String, String> requestParams, Class<T> clazz) throws HttpClientException, HttpRequestException {
        try {
            URL url = buildUrl(path, toQueryString(requestParams));
            HttpURLConnection connection = createGetConnection(url, token);

            return executeMethod(connection, clazz);
        } catch (IOException e) {
            LOG.trace("Unrecoverable issue when trying to build the HTTP Client");
            throw new HttpClientException(e);
        }
    }

    public <T> T makePostRequest(String path, String token, Requestable body, Class<T> clazz) throws HttpClientException, HttpRequestException {
        try {
            URL url = buildUrl(path);
            HttpURLConnection connection = createPostConnection(url, token);

            try (DataOutputStream payloadStream = new DataOutputStream(connection.getOutputStream())) {
                payloadStream.writeBytes(mapper.write(body));
            } catch (IOException e) {
                LOG.trace("Unrecoverable issue when creating a message body for the HTTP Client");
                throw new HttpClientException(e);
            }

            return executeMethod(connection, clazz);
        } catch (IOException e) {
            LOG.trace("Unrecoverable issue when trying to build the HTTP Client");
            throw new HttpClientException(e);
        }
    }

    private <T> T executeMethod(HttpURLConnection connection, Class<T> clazz) throws IOException, HttpRequestException {
        connection.connect();
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            LOG.trace("{} [{}] {}", connection.getRequestMethod(), connection.getResponseCode(), connection.getURL());
            return mapper.read(buffer, clazz);
        } catch (IOException e) {
            LOG.trace("{} [{}] {}", connection.getRequestMethod(), connection.getResponseCode(), connection.getURL());
            throw wrapToHttpRequestException(connection);
        }
    }

    private HttpRequestException wrapToHttpRequestException(HttpURLConnection connection) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(connection.getErrorStream(), UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            return new HttpRequestException(connection.getResponseCode(), mapper.read(buffer, Message.class));
        } catch (IOException | NullPointerException e) {
            LOG.trace("Error stream is empty or not readable, returning only the response code");
            return new HttpRequestException(connection.getResponseCode(), null);
        }
    }

    private String toQueryString(Map<String, String> queryParameters) {
        String params = queryParameters.entrySet().stream().map(HttpClient::toQueryParam).collect(joining("&"));
        return params.trim().isEmpty() ? "" : "?" + params.trim();
    }

    private static String toQueryParam(Map.Entry<String, String> entry) {
        return String.format("%s=%s", entry.getKey(), entry.getValue());
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
        if (token != null && !"".equals(token)) {
            connection.setRequestProperty(AUTHORIZATION, String.format(TOKEN_TEMPLATE, token));
        }
        connection.setConnectTimeout(TIMEOUT_IN_MS);
        connection.setReadTimeout(TIMEOUT_IN_MS);
        return connection;
    }

    private HttpURLConnection createPostConnection(URL url, String token) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod(POST);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
        if (token != null && !"".equals(token)) {
            connection.setRequestProperty(AUTHORIZATION, String.format(TOKEN_TEMPLATE, token));
        }
        connection.setConnectTimeout(TIMEOUT_IN_MS);
        connection.setDoOutput(true);
        return connection;
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        return proxy.isPresent() ? (HttpURLConnection) url.openConnection(proxy.get()) : (HttpURLConnection) url.openConnection();
    }
}
