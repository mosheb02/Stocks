package core.jackson;

public class CompanyEvent {
	public CompanyEvent() {

	}

	private Company m_company;
	private EventTime m_eventTime;

	public Company getCompany() {
		return m_company;
	}

	public void setCompany(Company company) {
		this.m_company = company;
	}

	public EventTime getEventTime() {
		return m_eventTime; 
	}

	public void setEventTime(EventTime eventTime) {
		this.m_eventTime = eventTime;
	}
	public class Company {
		public Company() {
		}

		private String m_ticker;
		
		private String m_name;

		public String getTicker() {
			return m_ticker;
		}
		
		public String getUsableTicker() {
			if (m_ticker == null)
			{
				return "";
			}
			int indexOfSeperator = m_ticker.indexOf(":");
			return indexOfSeperator > 0 ? m_ticker.substring(0,indexOfSeperator):m_ticker; 
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
	}

	public class EventTime {
		public EventTime() {
		}

		private String m_date;

		public String getDate() {
			return m_date;
		}

		public void setDate(String date) {
			this.m_date = date;
		}
	}
}
