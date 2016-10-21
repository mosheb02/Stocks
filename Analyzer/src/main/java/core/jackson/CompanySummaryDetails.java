package core.jackson;

import java.time.LocalDate;

public class CompanySummaryDetails {
	String m_ticker;
	String m_name;
	double m_marketValue;
	LocalDate m_nextEarningDate;

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

}
