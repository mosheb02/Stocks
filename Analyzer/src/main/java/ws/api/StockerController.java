package ws.api;

import java.util.List;

import org.bson.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.Stocker;
import core.jackson.CompanySummaryDetails;

@RestController
public class StockerController {
	@RequestMapping(value = "/stocker/stocks/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CompanySummaryDetails> analyze(@RequestParam(value = "days", defaultValue = "2") int noOfDaysForwad,
			@RequestParam(value = "companies", defaultValue = "10") int noOfCompanies) {

		Stocker stocker = new Stocker();
		try {
			return stocker.analyzeByFutureEarnings(noOfDaysForwad, noOfCompanies);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping(value = "/stocker/getLargestCompanies", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Document> getLargestCompanies() {

		Stocker stocker = new Stocker();
		return stocker.getLargestCompaniesAnalysis();

	}
}
