package dansplugins.rpsystem.integrators;

import org.bukkit.Bukkit;

import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.rpsystem.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class MailboxesIntegrator {
    private final Logger logger;

    private MailboxesAPI m_api = null;

    public MailboxesIntegrator(Logger logger) {
        this.logger = logger;
        if (isMailboxesPresent()) {
            this.logger.log("Mailboxes was found successfully!");
            m_api = new MailboxesAPI();
        }
        else {
            this.logger.log("Mailboxes was not found!");
        }
    }

    public boolean isMailboxesPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("Mailboxes") != null);
    }

    public MailboxesAPI getAPI() {
        return m_api;
    }
}