package dansplugins.rpsystem.integrators;

import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.rpsystem.utils.Logger;
import org.bukkit.Bukkit;

public class MailboxesIntegrator {

    private static MailboxesIntegrator instance;

    private MailboxesAPI m_api = null;

    private MailboxesIntegrator() {
        if (isMailboxesPresent()) {
            Logger.getInstance().log("Mailboxes was found successfully!");
            m_api = new MailboxesAPI();
        }
        else {
            Logger.getInstance().log("Mailboxes was not found!");
        }
    }

    public static MailboxesIntegrator getInstance() {
        if (instance == null) {
            instance = new MailboxesIntegrator();
        }
        return instance;
    }

    public boolean isMailboxesPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("Mailboxes") != null);
    }

    public MailboxesAPI getAPI() {
        return m_api;
    }

}
