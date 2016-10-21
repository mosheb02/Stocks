package core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.StocksDAO;
import core.jackson.CompanyEvent;
import core.jackson.Quote;
import utils.ClientUtils;

public class StocksAnalyzerClient {
	private static final String TICKER_REPLACMENT_SYMBOL = "<TICKER>";
	private static final String QUOTE_ID = "\"quote\"";
  static final String GET_FUTURE_EVENTS_BY_DATE_URL = "http://www.bloomberg.com/markets/api/calendar/earnings/US?locale=en&date";
	private static final String GET_QUOTE_BY_TICKER_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20%3D%22"
			+ TICKER_REPLACMENT_SYMBOL
			+ "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	public static void main(String[] args) {
		// Configure jackson mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		final String todayformattedStr = new StringBuilder(GET_FUTURE_EVENTS_BY_DATE_URL)
				.append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).toString();
		String jsonString;
		try {
			jsonString = ClientUtils.getJsonResponse(new URL(todayformattedStr));
			List<CompanyEvent> companyEvents = mapper.readValue(
					jsonString.substring(jsonString.indexOf("["), jsonString.length() - 2),
					new TypeReference<List<CompanyEvent>>() {
					});
			for (CompanyEvent companyEvent : companyEvents) {
				final String quoteResponse;
				try
				{ 
					quoteResponse = ClientUtils.getJsonResponse(new URL(GET_QUOTE_BY_TICKER_URL
						.replace(TICKER_REPLACMENT_SYMBOL, companyEvent.getCompany().getUsableTicker())));
				}
				catch (Exception e)
				{
					continue;
				}
				int quoteIndex = quoteResponse.indexOf(QUOTE_ID);
				if (quoteIndex > 0) {
					Quote quote = mapper.readValue(
							quoteResponse.substring(quoteIndex + QUOTE_ID.length() + 1, quoteResponse.length() - 3),
							Quote.class);

					StocksDAO.createDB("localhost", 27017, "stocks");
					StocksDAO.insert("test", quoteResponse);
					System.out.println(companyEvent.getCompany().getUsableTicker() + " "
							+ companyEvent.getCompany().getName() + " Market value " + quote.getMarketCapitalization() + " " + quote.getAsk());
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
