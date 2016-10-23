package DAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class StocksDAO {
	public final static String STOCKER_DB = "stocks";

	public final static String STOCKER_QUOTES_COLLECTION = "quotes";
	public final static String STOCKER_LARGEST_COMPANIES = "largest_companies";
	public final static String STOCKER_ANALYSIS_RESULTS = "analysis_results";
	public final static String STOCKER_TWITTER_INFORMATION = "twitter_information";
	public final static String STOCKER_ERROR_LOG_COLLECTION = "errors";
	public final static String STOCKER_USERS_COLLECTION = "users";

	static MongoClient m_client = null;
	final private static StocksDAO m_stocksDAO = new StocksDAO("localhost", 27017);

	@SuppressWarnings("unused")
	private StocksDAO() {

	}

	public StocksDAO(final String server, final int port) {
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		m_client = new MongoClient(server, port);

	}

	public List<Document> getAllCollectionElements(final String dbName, final String collectionName) {
		final MongoCollection<Document> collection = getCollection(dbName, collectionName);
		FindIterable<Document> findIterable = collection.find();
		return getIterResults(findIterable);
	}

	public List<Document> getCollectionElements(final String dbName, final String collectionName,
			final int startingIndex, final int numberOfElements) {
		final MongoCollection<Document> collection = getCollection(dbName, collectionName);
		FindIterable<Document> findIterable = collection.find().skip(startingIndex < 0 ? 0 : startingIndex)
				.limit(numberOfElements);
		return getIterResults(findIterable);
	}

	public List<Document> getIterResults(FindIterable<Document> findIterable) {
		List<Document> results = new LinkedList<>();
		final MongoCursor<Document> iterator = findIterable.iterator();
		while (iterator.hasNext()) {
			results.add(iterator.next());
		}

		return results;
	}

	public List<?> getIterResults(FindIterable<Document> findIterable, Class<?> classToMap)
			throws JsonParseException, JsonMappingException, IOException {

		List<Document> iterResults = getIterResults(findIterable);
		return getMappedDocuments(iterResults, classToMap);
	}

	public List<?> getMappedDocuments(List<Document> iterResults, Class<?> classToMap)
			throws IOException, JsonParseException, JsonMappingException {
		// Configure jackson mapper
		List<Object> results = new LinkedList<>();
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		for (final Document document : iterResults) {
			results.add(mapper.readValue(document.toJson(), classToMap));
		}

		return results;
	}

	public void insert(final String dbName, final String collectionName, final String jsonDocument) {
		final MongoCollection<Document> collection = getCollection(dbName, collectionName);
		Document document = (Document) Document.parse(jsonDocument);
		collection.insertOne(document);
	}

	public void insert(final String dbName, final String collectionName, final List<String> jsonDocuments,
			boolean isThrowParsingException) {
		final MongoCollection<Document> collection = getCollection(dbName, collectionName);
		List<Document> documents = new ArrayList<Document>(jsonDocuments.size());
		for (final String jsonDocument : jsonDocuments) {
			try {
				documents.add((Document) Document.parse(jsonDocument));
			} catch (Exception e) {
				e.printStackTrace();
				if (isThrowParsingException) {
					throw e;
				}
			}
		}
		collection.insertMany(documents);
	}

	public void dropCollection(final String dbName, final String collectionName) {
		getCollection(dbName, collectionName).drop();
	}

	public MongoCollection<Document> getCollection(final String dbName, final String collectionName) {
		return m_client.getDatabase(dbName).getCollection(collectionName);
	}

	public static StocksDAO getConnection() {
		return m_stocksDAO;
	}
}
