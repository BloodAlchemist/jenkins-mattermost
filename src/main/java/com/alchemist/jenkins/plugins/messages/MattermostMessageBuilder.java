package com.alchemist.jenkins.plugins.messages;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hudson.model.AbstractBuild;
import hudson.model.Result;

import java.util.Map;

/**
 * Class MattermostMessageBuilder.
 *
 * @author alchemist
 */
public final class MattermostMessageBuilder extends MessageBuilder {

    // Icon for build status
    private static final Map<Result, String> ICON_STATUS = ImmutableMap.of(
            Result.SUCCESS, ":sunny:",
            Result.UNSTABLE, ":partly_sunny:",
            Result.ABORTED, ":skull:",
            Result.FAILURE, ":fire:",
            Result.NOT_BUILT, ":boom:"
    );

    /**
     * Constructor.
     *
     * @param build AbstractBuild
     */
    public MattermostMessageBuilder(AbstractBuild<?, ?> build) {
        super(build);
    }

    /**
     * Build message from build.
     *
     * @return String
     */
    @Override
    public String build() {
        final Result result = build.getResult();

        // Prepare title
        final String title = String.format("Job: %s Status: %s", getJobName(), result != null ? result.toString() : "None");
        // Prepare text
        final String text = String.format("%s %s %s [View](%s)", ICON_STATUS.get(build.getResult()), getCause(),
                getTime(), getUrl());

        // Prepare message for attachments
        final JsonObject msg = new JsonObject();
        msg.addProperty("text", text);
        msg.addProperty("color", ifSuccess() ? "#228a00" : "#8B0000");
        msg.addProperty("title", title);

        // Prepare attachments
        final JsonArray attachments = new JsonArray();
        attachments.add(msg);

        // Prepare final json
        final JsonObject json = new JsonObject();
        json.addProperty("username", "Jenkins");
        json.add("attachments", attachments);

        return json.toString();
    }
}
