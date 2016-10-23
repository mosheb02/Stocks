package jackson;

public class CompanyEvent {
	public CompanyEvent() {

	}

	private Company company;
	private EventTime eventTime;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EventTime getEventTime() {
		return eventTime; 
	}

	public void setEventTime(EventTime eventTime) {
		this.eventTime = eventTime;
	}
	public class Company {
		public Company() {
		}

		private String ticker;
		
		private String name;

		public String getTicker() {
			return ticker;
		}
		
		public String getUsableTicker() {
			if (ticker == null)
			{
				return "";
			}
			int indexOfSeperator = ticker.indexOf(":");
			return indexOfSeperator > 0 ? ticker.substring(0,indexOfSeperator):ticker; 
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
	}

	public class EventTime {
		public EventTime() {
		}

		private String date;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	}
}
