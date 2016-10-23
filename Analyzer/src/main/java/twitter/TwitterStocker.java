
package twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import DAO.StocksDAO;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterStocker {
	final static String CONSUMER_KEY = "nndeQTwXazWtnRvcqoIjFWaxK";
	final static String CONSUMER_SECRET = "Sb6ZeAwermqIcuyFFDSSvYiFpvfYGNvJp2bXlGrm5CLqeRmAzx";
	final static String ACCESS_KEY = "787648973683191808-qTs2FJTHkqxBrTCkRrJLHsp56Tm36ki";
	final static String ACCESS_SECRET = "B3zkEwEpvVV2ZWUalYKorb3ZoOsOskGW6JL9uwd41kwWE";

	final static Twitter m_twitter = getTwitterInstance();
	final StocksDAO m_stocksConnection = StocksDAO.getConnection();
	
	public static void main(String[] args) {

		try {
			TwitterStocker twitterStocker = new TwitterStocker();
			twitterStocker.getFollowerByScreenName();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getFollowerByScreenName() throws TwitterException, InterruptedException, IOException {
		// stocksConnection.dropCollection(StocksDAO.STOCKER_DB,
		// StocksDAO.STOCKER_USERS_COLLECTION);
		Path path = FileSystems.getDefault().getPath("src/main/resources/input/users.txt");
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String userName;
			while ((userName = reader.readLine()) != null) {
				System.out.println("**************************USER_NAME IS " + userName
						+ "**************************************");
				addFollowersByUserName(userName, m_stocksConnection);
			}
		}
	}

	private static void addFollowersByUserName(final String userName, StocksDAO stocksConnection)
			throws TwitterException, InterruptedException {
		try {
			int numOfFollowersAdded = 0;
			PagableResponseList<User> followers;
			try {
				followers = m_twitter.getFollowersList(userName, -1, 199);

			} catch (TwitterException te) {
				if (te.getErrorCode() == 88) {
					// Wait for 15 min
					System.out.println("Sleeping for 15 min!");
					Thread.sleep(900 * 1000);
				}
				followers = m_twitter.getFollowersList(userName, -1, 199);
			}
			
			boolean isAwake = false;
			while (followers.getRateLimitStatus().getRemaining() > 0 || isAwake) 
			{
				isAwake = false;
				List<String> users = new LinkedList<>();
				for (User user : followers) {
					{
						users.add(
								"{" + "\"following\":\"" + userName + "\"," + "\"screenName\":\"" + user.getScreenName()
										+ "\"," + "\"name\":\"" + user.getName() + "\"," + "\"id\":\"" + user.getId()
										+ "\"," + "\"description\":\"" + user.getDescription() + "\"," + "\"email\":\""
										+ user.getEmail() + "\"," + "\"friendsCount\":" + user.getFriendsCount()
										+ ",\"followersCount\":" + user.getFollowersCount() +  ",\"favourites\":"+user.getFavouritesCount()+"}");
						
						numOfFollowersAdded++;
					}
				}
				if (users.isEmpty())
				{
					return;
				}
				stocksConnection.insert(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_USERS_COLLECTION, users, false);
				
				// Stop adding followers after 5,000 
				if (numOfFollowersAdded > 15000)
				{
					return;
				}
				followers = m_twitter.getFollowersList(userName, followers.getNextCursor(), 199);
				if (followers.getRateLimitStatus().getRemaining() == 0) {
					System.out.println("Sleeping for :" + followers.getRateLimitStatus().getSecondsUntilReset()
							+ " seconds." + "current user is:" + userName);
					Thread.sleep((followers.getRateLimitStatus().getSecondsUntilReset() + 1) * 1000);
					isAwake = true;
				}
			}
		} catch (Exception e) {
			try {
				stocksConnection.insert(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_ERROR_LOG_COLLECTION,
						"{\"error\":\"" + e.getMessage() + "\"}");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public List<Document> getUsers(int startingIndex, int numberOfUsers)
	{
		return m_stocksConnection.getCollectionElements(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_USERS_COLLECTION,startingIndex,numberOfUsers);
	}


	private static Twitter getTwitterInstance() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_KEY).setOAuthAccessTokenSecret(ACCESS_SECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}
}