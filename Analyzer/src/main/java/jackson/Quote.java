package jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quote {
	public Quote() {
	}
	
	private String symbol;
	
	@JsonProperty("Ask")
	private String ask;
	@JsonProperty("Bid")
	private String bid;
	@JsonProperty("MarketCapitalization")
	private String marketCapitalization;

	public String getMarketCapitalization() {
		return marketCapitalization;
	}
	public void setMarketCapitalization(String marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getAsk() {
		return ask;
	}
	public void setAsk(String ask) {
		this.ask = ask;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
}
