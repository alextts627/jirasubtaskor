package com.tsao.jira.client;

import net.rcarz.jiraclient.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by TTsao on 15/01/2017.
 */
public class Client {
	final private static String CONFIGURATION_FILE_NAME = "credential.property";
    public static void main(String[] args) {
	    if (args.length < 2)    throw new RuntimeException("please provide a ticket as argument");
        String ticket = args[1].toUpperCase();
	    Client client = new Client();
		User user = client.readProperties();

        BasicCredentials creds = new BasicCredentials(user.getUser(), user.getPassword());
        JiraClient jira = new JiraClient("http://jira.emdeon.net/", creds);

        try {
            final Issue issue = jira.getIssue(ticket);

			Issue subtask = client.subtask(issue, "write design doc for " + ticket);
	        client.assign(subtask, user.getUser());

	        subtask = client.subtask(issue, "review design doc for " + ticket);
	        client.assign(subtask, user.getUser());

	        subtask = client.subtask(issue, "implement design for " + ticket);
	        client.assign(subtask, user.getUser());

	        subtask = client.subtask(issue, "review code for " + ticket);
	        client.assign(subtask, user.getUser());

	        subtask = client.subtask(issue, "implement unit test for " + ticket);
	        client.assign(subtask, user.getUser());

	        subtask = client.subtask(issue, "review unit test for " + ticket);
	        client.assign(subtask, user.getUser());

        } catch (JiraException ex) {
            System.err.println(ex.getMessage());

            if (ex.getCause() != null)
                System.err.println(ex.getCause().getMessage());
        }
    }

    private Issue subtask(Issue parent, String summary) throws JiraException {
	    /* Create sub-task */
	    Issue subtask = parent.createSubtask()
			    .field(Field.SUMMARY, summary)
			    .execute();
	    return subtask;
    }

    private void assign(Issue subtask, String user) throws JiraException {
	    /* Assign to me */
	    subtask.update()
			    .field(Field.ASSIGNEE, user)
			    .execute();
    }

	private User readProperties() {
		User result = new User();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(CONFIGURATION_FILE_NAME)) {
			props.load(resourceStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		result.setUser(props.getProperty("user"));
		result.setPassword(props.getProperty("password"));
		return result;
	}

	private class User {
		private String user;
		private String password;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
