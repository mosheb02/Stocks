package stocks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import DAO.StocksDAO;
import jackson.CompaniesIteration;
import jackson.CompanyEvent;
import jackson.CompanySummaryDetails;
import jackson.Quote;
import utils.ClientUtils;

public class Stocker {
	private static final String TICKER_REPLACMENT_SYMBOL = "<TICKER>";
	private static final String QUOTE_ID = "\"quote\"";
	static final String GET_FUTURE_EVENTS_BY_DATE_URL = "http://www.bloomberg.com/markets/api/calendar/earnings/US?locale=en&date=";
	static final String GET_FUTURE_EVENTS_BY_DATE_INVALID_DATE_RESPONSE = "{\"_expanded\":true}";

	private static final String GET_QUOTE_BY_TICKER_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20%3D%22"
			+ TICKER_REPLACMENT_SYMBOL
			+ "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	private final StocksDAO m_stocksConnection = StocksDAO.getConnection();

	private final String[] REMOVABLE_NAME_SFUFFIXS = new String[] { " Co", " Inc", " Ltd", " Corp", "& Co" };

	public CompaniesIteration getLargestCompaniesAnalysisByIteration(int iterationNo) throws JsonParseException, JsonMappingException, IOException {

		@SuppressWarnings("unchecked")
		List<CompaniesIteration> iterResults = (List<CompaniesIteration>)m_stocksConnection.getIterResults(
				m_stocksConnection.getCollection(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_LARGEST_COMPANIES).find(
						new BasicDBObject("iterationNo", iterationNo > 1 ? iterationNo : getMaxIterationNumber())),CompaniesIteration.class);
		if (iterResults != null && iterResults.size() >0)
		{
			return iterResults.get(0);
		}
		return null;
	}

	public CompaniesIteration calculateLargestCompaniesByFutureEarnings(int noOfDaysForward, int numOfCompaniesCache)
			throws JsonParseException, JsonMappingException, IOException {

		final CompaniesIteration companiesIteration = new CompaniesIteration();
		companiesIteration.setIterationNo(getMaxIterationNumber() + 1);
		companiesIteration.setNoOfDaysForward(noOfDaysForward);
		companiesIteration.setIterationDateTime(LocalDateTime.now().toString());
		final List<CompanySummaryDetails> sortedLargestCompanies = new ArrayList<CompanySummaryDetails>(
				numOfCompaniesCache);
		final Map<String, CompanySummaryDetails> stocksCache = new ConcurrentHashMap<String, CompanySummaryDetails>();

		// Configure jackson mapper
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		LocalDate dateOfEarnings = LocalDate.now();
		while (numOfCompaniesCache > 0 && noOfDaysForward > 0) {
			final String futureEventsByDateURL = new StringBuilder(GET_FUTURE_EVENTS_BY_DATE_URL).append(dateOfEarnings)
					.toString();

			final String futureEventsResponse = ClientUtils.getJsonResponse(m_stocksConnection, StocksDAO.STOCKER_DB,
					StocksDAO.STOCKER_ERROR_LOG_COLLECTION, new URL(futureEventsByDateURL));
			if (futureEventsResponse != null
					&& !futureEventsResponse.equals(GET_FUTURE_EVENTS_BY_DATE_INVALID_DATE_RESPONSE)) {
				final List<CompanyEvent> companyEvents = mapper.readValue(futureEventsResponse
						.substring(futureEventsResponse.indexOf("["), futureEventsResponse.length() - 2),
						new TypeReference<List<CompanyEvent>>() {
						});

				for (final CompanyEvent companyEvent : companyEvents) {
					final CompanySummaryDetails companySummaryDetails = handleQuote(mapper, companyEvent, stocksCache);
					if (companySummaryDetails == null) {
						continue;
					}
					if (sortedLargestCompanies.size() < numOfCompaniesCache) {
						sortedLargestCompanies.add(companySummaryDetails);
						sortCompanies(sortedLargestCompanies);
					} else if (companySummaryDetails.getMarketValue() > sortedLargestCompanies
							.get(numOfCompaniesCache - 1).getMarketValue()) {
						sortedLargestCompanies.remove(numOfCompaniesCache - 1);
						sortedLargestCompanies.add(companySummaryDetails);
						sortCompanies(sortedLargestCompanies);
					}
				}
			}
			noOfDaysForward--;
			dateOfEarnings = dateOfEarnings.plusDays(1);
		}

		companiesIteration.setCompanies(sortedLargestCompanies);

		m_stocksConnection.insert(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_LARGEST_COMPANIES,
				mapper.writeValueAsString(companiesIteration));

		return companiesIteration;
	}

	private int getMaxIterationNumber() {
		MongoCollection<Document> collection = m_stocksConnection.getCollection(StocksDAO.STOCKER_DB,
				StocksDAO.STOCKER_LARGEST_COMPANIES);
		FindIterable<Document> iterableResult = collection.find().sort(new BasicDBObject("iterationNo", -1)).limit(1);
		List<Document> results = m_stocksConnection.getIterResults(iterableResult);
		return results.size() > 0 ? results.get(0).getInteger("iterationNo") : 0;
	}

	private void sortCompanies(final List<CompanySummaryDetails> sortedLargestCompanies) {
		sortedLargestCompanies.sort(new Comparator<CompanySummaryDetails>() {

			public int compare(CompanySummaryDetails companySummaryDetails1,
					CompanySummaryDetails companySummaryDetails2) {
				double marketValue1 = companySummaryDetails1.getMarketValue();
				double marketValue2 = companySummaryDetails2.getMarketValue();
				return marketValue2 > marketValue1 ? 1 : marketValue1 == marketValue2 ? 0 : -1;

			}
		});
	}

	private CompanySummaryDetails handleQuote(final ObjectMapper mapper, final CompanyEvent companyEvent,
			Map<String, CompanySummaryDetails> stocksCache)
			throws IOException, MalformedURLException, JsonParseException, JsonMappingException {
		try {
			CompanySummaryDetails companySummaryDetails;
			companySummaryDetails = stocksCache.get(companyEvent.getCompany().getUsableTicker());
			if (companySummaryDetails != null) {
				return companySummaryDetails;
			}

			final String quoteResponse = ClientUtils.getJsonResponse(m_stocksConnection, StocksDAO.STOCKER_DB,
					StocksDAO.STOCKER_ERROR_LOG_COLLECTION, new URL(GET_QUOTE_BY_TICKER_URL
							.replace(TICKER_REPLACMENT_SYMBOL, companyEvent.getCompany().getUsableTicker())));
			int quoteIndex = quoteResponse.indexOf(QUOTE_ID);
			if (quoteIndex > 0) {
				Quote quote = mapper.readValue(
						quoteResponse.substring(quoteIndex + QUOTE_ID.length() + 1, quoteResponse.length() - 3),
						Quote.class);

				if (quote != null) {
					m_stocksConnection.insert(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_QUOTES_COLLECTION, quoteResponse);
					companySummaryDetails = new CompanySummaryDetails();
					companySummaryDetails.setTicker(companyEvent.getCompany().getUsableTicker());
					companySummaryDetails.setName(companyEvent.getCompany().getName());
					companySummaryDetails.setNextEarningDate(companyEvent.getEventTime().getDate());
					companySummaryDetails.setMarketValue(getMarketValue(quote.getMarketCapitalization()));
					companySummaryDetails.setReadableNames(getReadableNames(companyEvent.getCompany().getName()));
					stocksCache.put(companySummaryDetails.getTicker(), companySummaryDetails);

					System.out.println(
							companyEvent.getCompany().getUsableTicker() + " " + companyEvent.getCompany().getName()
									+ " Market value " + quote.getMarketCapitalization() + " " + quote.getAsk());

					return companySummaryDetails;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getReadableNames(String companyName) {
		List<String> readableNames = new LinkedList<>();
		int removableSuffixIndex = -1;
		for (final String removableSuffix : REMOVABLE_NAME_SFUFFIXS) {
			int currentIndex = companyName.indexOf(removableSuffix);
			if (currentIndex > -1 && (removableSuffixIndex == -1 || currentIndex < removableSuffixIndex)) {
				removableSuffixIndex = currentIndex;
			}
		}
		final String readableName = removableSuffixIndex > -1 ? companyName.substring(0, removableSuffixIndex - 1)
				: companyName;

		readableNames.add(readableName.trim());

		return readableNames;
	}

	private double getMarketValue(String marketValue) {
		if (marketValue == null) {
			return 0;
		}
		if (marketValue.endsWith("M")) {
			return Double.valueOf(marketValue.substring(0, marketValue.length() - 2)).doubleValue() * 1000000;
		} else if (marketValue.endsWith("B")) {
			return Double.valueOf(marketValue.substring(0, marketValue.length() - 2)).doubleValue() * 1000000000;
		}
		return Double.valueOf(marketValue);
	}

}
