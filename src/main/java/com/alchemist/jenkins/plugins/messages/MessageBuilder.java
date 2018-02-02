package com.alchemist.jenkins.plugins.messages;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.triggers.SCMTrigger;
import jenkins.model.JenkinsLocationConfiguration;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Class MessageBuilder.
 *
 * @author alchemist
 */
public abstract class MessageBuilder {

    // Build
    protected final AbstractBuild<?, ?> build;

    /**
     * Constructor.
     *
     * @param build AbstractBuild
     */
    public MessageBuilder(final AbstractBuild<?, ?> build) {
        this.build = build;
    }

    /**
     * Build message from build.
     *
     * @return String
     */
    public abstract String build();

    /**
     * Get base url.
     *
     * @return String
     */
    protected String getBaseUrl() {
        try {
            return JenkinsLocationConfiguration.get().getUrl();
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Get build url.
     *
     * @return String
     */
    protected String getUrl() {
        return String.format("%s%s", getBaseUrl(), build.getUrl());
    }

    /**
     * Get build time.
     *
     * @return String
     */
    protected String getTime() {
        return build.getTimestampString();
    }

    /**
     * Get job name.
     *
     * @return String
     */
    protected String getJobName() {
        return build.getProject().getFullDisplayName();
    }

    /**
     * Get cause.
     *
     * @return String
     */
    protected String getCause() {

        // Try get cause from trigger
        final SCMTrigger.SCMTriggerCause scmTriggerCause = build.getCause(SCMTrigger.SCMTriggerCause.class);
        if (scmTriggerCause != null) {
            return scmTriggerCause.getShortDescription();
        }

        // Try get cause from build user
        final Cause.UserIdCause buildUserCause = getUserIdCause(build.getCauses());
        if (buildUserCause != null) {
            return String.format("Started by %s", buildUserCause.getUserName());
        }

        // Try get cause from upstream build
        final Cause.UpstreamCause upstreamCause = build.getCause(Cause.UpstreamCause.class);
        if (upstreamCause != null) {

            // try get user from upstream
            final Cause.UserIdCause upstreamUserCause = getUserIdCause(upstreamCause.getUpstreamCauses());
            if (upstreamUserCause != null) {
                return String.format("Started by %s", upstreamUserCause.getUserName());
            }
        }

        // Unknown cause
        return "Started by unknown cause";
    }

    /**
     * Get user cause.
     *
     * @param list List
     * @return Cause.UserIdCause
     */
    protected Cause.UserIdCause getUserIdCause(final List<Cause> list) {
        for (Cause cause : list) {
            if (Cause.UserIdCause.class.isInstance(cause)) {
                return Cause.UserIdCause.class.cast(cause);
            }
        }
        return null;
    }

    /**
     * Is success.
     *
     * @return boolean
     */
    protected boolean ifSuccess() {
        return build.getResult() == Result.SUCCESS;
    }
}
