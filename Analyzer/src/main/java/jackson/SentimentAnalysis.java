package jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SentimentAnalysis
{
		@JsonProperty("Sentiment")
		private String sentiment;
		
		@JsonProperty("Confidence")
		private double confidence;

		public String getSentiment() {
			return sentiment;
		}

		public void setSentiment(String sentiment) {
			this.sentiment = sentiment;
		}

		public double getConfidence() {
			return confidence;
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}
}
