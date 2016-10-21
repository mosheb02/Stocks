package DAO;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class StocksDAO {
	static MongoDatabase m_db = null;

	@SuppressWarnings("resource")
	public static boolean createDB(final String server, final int port, final String dbName) {
		m_db = new MongoClient("localhost", 27017).getDatabase("stocks");
		return m_db != null;
	}

	public static void insert(final String collectionName, final String jsonDocument) {
		final MongoCollection<Document> collection = getCollection(collectionName);
		Document document = (Document) Document.parse(jsonDocument);
		collection.insertOne(document);
	}
	
	public static void insert(final String collectionName, final List<String> jsonDocuments) {
		final MongoCollection<Document> collection = getCollection(collectionName);
		List<Document> documents = new ArrayList<Document>(jsonDocuments.size());
		for (final String jsonDocument : jsonDocuments)
		{
			documents.add((Document) Document.parse(jsonDocument));
		}
		collection.insertMany(documents);
	}

	private static MongoCollection<Document> getCollection(final String collectionName) {
		if (m_db == null) {
			throw new RuntimeException("not connected to db");
		}
		
		return m_db.getCollection(collectionName);
	}
}
