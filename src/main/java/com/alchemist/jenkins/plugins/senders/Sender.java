package com.alchemist.jenkins.plugins.senders;

import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Class Sender.
 *
 * @author alchemist
 */
public abstract class Sender {

    /**
     * Send message.
     *
     * @param message String
     * @return boolean
     * @throws IOException Exception
     */
    public abstract boolean send(final String message) throws IOException;

    /**
     * Send json.
     *
     * @param json JsonObject
     * @return boolean
     * @throws IOException Exception
     */
    public boolean send(final JsonObject json) throws IOException {
        return send(json.toString());
    }
}
