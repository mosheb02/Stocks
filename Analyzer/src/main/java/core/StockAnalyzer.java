package core;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;

import DAO.StocksDAO;
import jackson.AnalysisResult;
import jackson.AnalysisResult.Network;
import jackson.AnalysisResult.Network.AnalyzedCompany;
import jackson.AnalyzedTwitt;
import jackson.CompaniesIteration;
import jackson.CompanySummaryDetails;
import jackson.SentimentAnalysis;
import jackson.TwittInformation;
import jackson.TwitterInformation;
import stocks.Stocker;

public class StockAnalyzer {
	private final StocksDAO m_stocksConnection = StocksDAO.getConnection();
	private final Stocker m_stocker = new Stocker();

	int TWITTS_CACHE_SIZE = 10000;

	public static void main(String[] args) {
		StockAnalyzer stockAnalyzer = new StockAnalyzer();
		try {
			AnalysisResult analyze = stockAnalyzer.analyze(4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AnalysisResult analyze(int noOfDaysForward, int numOfCompanies)
			throws JsonParseException, JsonMappingException, IOException {
		CompaniesIteration companiesIteration = m_stocker.calculateLargestCompaniesByFutureEarnings(noOfDaysForward,
				numOfCompanies);
		return analyze(companiesIteration);
	}

	private AnalysisResult analyze(final CompaniesIteration companiesIteration)
			throws JsonParseException, JsonMappingException, IOException {
		AnalysisResult analysisResult = new AnalysisResult();
		analysisResult.setStartDate(LocalDateTime.now());
		analysisResult.setIterationNo(companiesIteration.getIterationNo());
		analysisResult.setSubIterationAnylsisNo(getMaxSubIteration(companiesIteration) + 1);
		addTwitterInformation(companiesIteration, analysisResult);
		analysisResult.setEndDate(LocalDateTime.now());
		return analysisResult;
	}

	@SuppressWarnings("unchecked")
	private void addTwitterInformation(CompaniesIteration companiesIteration, AnalysisResult analysisResult)
			throws JsonParseException, JsonMappingException, IOException {

		int twittIndex = 0;
		final Network network = analysisResult.new Network();
		final Map<String, AnalyzedCompany> companies = new ConcurrentHashMap<>();

		network.setName("Twitter");
		for (CompanySummaryDetails companySummaryDetails : companiesIteration.getCompanies()) {
			AnalyzedCompany analyzedCompany = network.new AnalyzedCompany();
			analyzedCompany.setTicker(companySummaryDetails.getTicker());
			analyzedCompany.setName(companySummaryDetails.getName());
			analyzedCompany.getRelatedKeyWords().add(analyzedCompany.getTicker());
			for (String keyWord : companySummaryDetails.getReadableNames()) {
				analyzedCompany.getRelatedKeyWords().add(keyWord);
			}
			companies.put(analyzedCompany.getTicker(), analyzedCompany);
		}

		List<TwitterInformation> twitts = (List<TwitterInformation>) m_stocksConnection
				.getMappedDocuments(
						m_stocksConnection.getCollectionElements(StocksDAO.STOCKER_DB,
								StocksDAO.STOCKER_TWITTER_INFORMATION, twittIndex, TWITTS_CACHE_SIZE),
						TwitterInformation.class);

		while (twitts.size() > 0) {
			for (TwitterInformation twitterInformation : twitts) {
				handleTwitt(twitterInformation, companies);
			}
			twittIndex += TWITTS_CACHE_SIZE;
			twitts = (List<TwitterInformation>) m_stocksConnection.getMappedDocuments(
					m_stocksConnection.getCollectionElements(StocksDAO.STOCKER_DB,
							StocksDAO.STOCKER_TWITTER_INFORMATION, twittIndex, TWITTS_CACHE_SIZE),
					TwitterInformation.class);
		}

		network.setCompanies(companies);
		analysisResult.getNetworks().add(network);

	}

	private void handleTwitt(TwitterInformation twitterInformation, Map<String, AnalyzedCompany> companies) {
		for (TwittInformation twittInformation : twitterInformation.getTweets()) {
			for (AnalyzedCompany analyzedCompany : companies.values()) {
				for (String keyWord : analyzedCompany.getRelatedKeyWords()) {
					if (twittInformation.getText().toUpperCase().contains(keyWord.toUpperCase())) {
						final AnalyzedTwitt analyzedTwitt = new AnalyzedTwitt();
						analyzedTwitt.setUserName(twitterInformation.getUserName());
						analyzedTwitt.setText(twittInformation.getText());
						analyzedCompany.increaseNumOfDistinctAppearances();						 
						analyzedCompany.getAnalyzedTwitts().add(analyzedTwitt);
						SentimentAnalysis sentimentAnalysis = twittInformation.getSentimentAnalysis();
						final String sentiment = sentimentAnalysis.getSentiment();
						switch (sentiment) {
						case "Positive":
							analyzedTwitt.setGrade(sentimentAnalysis.getConfidence() / 100);
							break;
						case "Negative":
							analyzedTwitt.setGrade(-1 * sentimentAnalysis.getConfidence() / 100);
							break;
						default:
							break;
						}
						analyzedCompany.setNominalGrade(
								analyzedCompany.getNominalGrade() + analyzedTwitt.getGrade());

						break;
					}
				}
			}
		}

	}

	public AnalysisResult analyze(int iterationNo) throws JsonParseException, JsonMappingException, IOException {
		final FindIterable<Document> results = m_stocksConnection
				.getCollection(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_LARGEST_COMPANIES)
				.find(new BasicDBObject("iterationNo", iterationNo));
		@SuppressWarnings("unchecked")
		final CompaniesIteration companiesIteration = ((List<CompaniesIteration>) m_stocksConnection
				.getIterResults(results, CompaniesIteration.class)).get(0);
		return analyze(companiesIteration);
	}

	private int getMaxSubIteration(final CompaniesIteration companiesIteration)
			throws JsonParseException, JsonMappingException, IOException {
		final FindIterable<Document> results = m_stocksConnection
				.getCollection(StocksDAO.STOCKER_DB, StocksDAO.STOCKER_ANALYSIS_RESULTS)
				.find(new BasicDBObject("iterationNo", companiesIteration.getIterationNo()))
				.sort(new BasicDBObject("subIterationAnylsisNo", -1)).limit(1);
		@SuppressWarnings("unchecked")
		List<AnalysisResult> analysisResults = (List<AnalysisResult>) m_stocksConnection.getIterResults(results,
				AnalysisResult.class);
		return analysisResults.size() > 0 ? analysisResults.get(0).getSubIterationAnylsisNo() : 0;
	}
}
