package com.alchemist.jenkins.plugins.senders;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class WebhookSender.
 *
 * @author alchemist
 */
public final class WebhookSender extends Sender {

    // Logger
    private static final Logger logger = Logger.getLogger(WebhookSender.class.getName());

    // Webhook url
    private final String webhook;

    /**
     * Constructor.
     *
     * @param webhook String
     */
    public WebhookSender(final String webhook) {
        this.webhook = webhook;
    }

    /**
     * Send message.
     *
     * @param message String
     * @return boolean
     * @throws IOException Exception
     */
    @Override
    public boolean send(final String message) throws IOException {

        // Prepare webhook url
        final URL url;
        try {
            url = new URL(webhook);
        } catch (MalformedURLException e) {
            logger.severe("Error while constructing webhook URL");
            throw new IOException("Error while constructing webhook URL", e);
        }

        // Prepare request
        final HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            throw new IOException("Could not open connection to webhook API", e);
        }

        // Send request
        try (final Writer writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Could not POST to webhook API", e);
        }

        // Check response
        if (connection.getResponseCode() == 200) {
            try (final InputStream responseStream = connection.getInputStream()) {
                final byte[] responseBytes = ByteStreams.toByteArray(responseStream);
                final String response = new String(responseBytes, Charsets.UTF_8);
                logger.info("Response: " + response);
            } catch (IOException e) {
                throw new IOException("Could not read response body from webhook API", e);
            }
            return true;
        } else if (connection.getResponseCode() == 429) {
            logger.warning("Too many requests");
        } else if (connection.getResponseCode() == 400) {
            logger.warning("Bad request on message:" + message);
        } else {
            throw new IOException("Unexpected HTTP response status " + connection.getResponseCode());
        }

        return false;
    }
}
