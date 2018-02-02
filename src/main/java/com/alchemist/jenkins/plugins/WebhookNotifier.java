package com.alchemist.jenkins.plugins;

import com.alchemist.jenkins.plugins.messages.MattermostMessageBuilder;
import com.alchemist.jenkins.plugins.messages.MessageBuilder;
import com.alchemist.jenkins.plugins.senders.Sender;
import com.alchemist.jenkins.plugins.senders.WebhookSender;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class WebhookNotifier.
 *
 * @author alchemist
 */
public final class WebhookNotifier extends Notifier {

    // Logger
    private static final Logger logger = Logger.getLogger(WebhookNotifier.class.getName());

    // Mattermost channel webhook
    private final String webhook;
    // Send if fail build flag
    private final boolean onlyFail;

    /**
     * Constructor.
     *
     * @param webhook String
     * @param onlyFail boolean
     */
    @DataBoundConstructor
    public WebhookNotifier(final String webhook, final boolean onlyFail) {
        this.webhook = webhook;
        this.onlyFail = onlyFail;
    }

    /**
     * Get required monitor service.
     *
     * @return BuildStepMonitor
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Perform
     *
     * @param build    AbstractBuild
     * @param launcher Launcher
     * @param listener BuildListener
     * @return boolean
     * @throws InterruptedException Exception
     * @throws IOException          Exception
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        // Check configuration
        try {
            checkConfig();
        } catch (Exception e) {
            throw new IOException("Missing configuration: " + e.getMessage());
        }

        // Check config and build result
        if (onlyFail && build.getResult() == Result.SUCCESS) {
            return true;
        }

        // Get sender
        final Sender sender = new WebhookSender(webhook);
        // Get message builder
        final MessageBuilder builder = new MattermostMessageBuilder(build);

        return sender.send(builder.build());
    }

    /**
     * Get webhook.
     *
     * @return String
     */
    public String getWebhook() {
        return webhook;
    }

    /**
     * Is send only fail.
     *
     * @return boolean
     */
    public boolean isOnlyFail() {
        return onlyFail;
    }

    /**
     * Check config.
     *
     * @throws Exception Exception
     */
    private void checkConfig() throws Exception {
        if (webhook == null || webhook.isEmpty()) {
            throw new Exception("Webhook configuration is mandatory and must not be empty");
        }
    }

    /**
     * Class MattermostDescriptor.
     *
     * @author alchemist
     */
    @Extension
    public static class MattermostDescriptor extends BuildStepDescriptor<Publisher> {

        /**
         * Is applicable.
         *
         * @param jobType Class
         * @return boolean
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Get display name.
         *
         * @return String
         */
        @Override
        public String getDisplayName() {
            return "Publish build status via webhook";
        }
    }
}
