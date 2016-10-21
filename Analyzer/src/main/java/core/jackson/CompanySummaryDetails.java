package core.jackson;

import java.time.LocalDate;
import java.util.List;

public class CompanySummaryDetails {
	private String m_ticker;
	private String m_name;
	private double m_marketValue;
	private LocalDate m_nextEarningDate;
	private List<String> m_readableNames;

	public String getTicker() {
		return m_ticker;
	}

	public void setTicker(String ticker) {
		this.m_ticker = ticker;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public double getMarketValue() {
		return m_marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.m_marketValue = marketValue;
	}

	public LocalDate getNextEarningDate() {
		return m_nextEarningDate;
	}

	public void setNextEarningDate(LocalDate nextEarningDate) {
		this.m_nextEarningDate = nextEarningDate;
	}

	public List<String> getReadableNames() {
		return m_readableNames;
	}

	public void setReadableNames(List<String> readableNames) {
		m_readableNames = readableNames;
	}

}
