package jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwittInformation
	{
		public TwittInformation() 
		{
		}
		
		@JsonProperty("Text")
		private String text;
		
		@JsonProperty("Replies")
		private int replies;

		@JsonProperty("Retweets")
		private int retweets;
		
		@JsonProperty("Likes")
		private int likes;
		
		@JsonProperty("Retweeted")
		private boolean isRetweeted;
		
		@JsonProperty("LastUpdateInHours")
		private String lastUpdateInHours;
		
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public int getReplies() {
			return replies;
		}

		public void setReplies(int replies) {
			this.replies = replies;
		}

		public int getRetweets() {
			return retweets;
		}

		public void setRetweets(int retweets) {
			this.retweets = retweets;
		}

		public int getLikes() {
			return likes;
		}

		public void setLikes(int likes) {
			this.likes = likes;
		}

		public boolean getRetweeted() {
			return isRetweeted;
		}

		public void setRetweeted(boolean isRetweeted) {
			this.isRetweeted = isRetweeted;
		}

		public String getLastUpdateInHours() {
			return lastUpdateInHours;
		}

		public void setLastUpdateInHours(String lastUpdateInHours) {
			this.lastUpdateInHours = lastUpdateInHours;
		}

		public SentimentAnalysis getSentimentAnalysis() {
			return sentimentAnalysis;
		}

		public void setSentimentAnalysis(SentimentAnalysis sentimentAnalysis) {
			this.sentimentAnalysis = sentimentAnalysis;
		}

		@JsonProperty("SentimentAnalysis")
		private SentimentAnalysis sentimentAnalysis;

	}
