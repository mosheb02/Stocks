package jackson;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterInformation 
{
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("TweetsCount")
	private int tweetsCount;

	@JsonProperty("FollowingCount")
	private int followingCount;

	@JsonProperty("FollowersCount")
	private int followersCount;

	@JsonProperty("LikesCount")
	private int likesCount;
	
	@JsonProperty(value = "Tweets")
	private List<TwittInformation> tweets;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getTweetsCount() {
		return tweetsCount;
	}

	public void setTweetsCount(int tweetsCount) {
		this.tweetsCount = tweetsCount;
	}

	public int getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public List<TwittInformation> getTweets() {
		return tweets;
	}

	public void setTweets(List<TwittInformation> twittsInformation) {
		this.tweets = twittsInformation;
	}


	
	

	
}
