package jackson;

import java.util.List;

public class CompanySummaryDetails {
	private String ticker;
	private String name;
	private double marketValue;
	
	private String nextEarningDate;
	
	private List<String> readableNames;

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public String getNextEarningDate() {
		return nextEarningDate;
	}

	public void setNextEarningDate(String nextEarningDate) {
		this.nextEarningDate = nextEarningDate;
	}

	public List<String> getReadableNames() {
		return readableNames;
	}

	public void setReadableNames(List<String> readableNames) {
		this.readableNames = readableNames;
	}

}
