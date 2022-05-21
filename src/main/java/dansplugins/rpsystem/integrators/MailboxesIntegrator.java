package dansplugins.rpsystem.integrators;

import dansplugins.exceptions.MailboxesNotFoundException;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.rpsystem.utils.Logger;
import org.bukkit.Bukkit;

public class MailboxesIntegrator {

    private static MailboxesIntegrator instance;

    private MailboxesAPI m_api = null;

    private MailboxesIntegrator() throws MailboxesNotFoundException {
        try {
            if (isMailboxesPresent()) {
                Logger.getInstance().log("Mailboxes was found successfully!");
                m_api = new MailboxesAPI();
            }
            else {
                Logger.getInstance().log("Mailboxes was not found!");
            }
        }
        catch(Exception e) {
            throw new MailboxesNotFoundException();
        }
    }

    public static MailboxesIntegrator getInstance() throws MailboxesNotFoundException {
        if (instance == null) {
            instance = new MailboxesIntegrator();
        }
        return instance;
    }

    public boolean isMailboxesPresent() {
        return (Bukkit.getServer().getPluginManager().getPlugin("Mailboxes") != null);
    }

    public MailboxesAPI getAPI() throws MailboxesNotFoundException {
        if (m_api == null) {
            throw new MailboxesNotFoundException();
        }
        return m_api;
    }

}
