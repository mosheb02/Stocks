package ws.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jackson.CompaniesIteration;
import stocks.Stocker;

@RestController
public class StockerController {
	@RequestMapping(value = "/stocker/largestCompanies/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
	public CompaniesIteration calculateLargestCompaniesByFutureEarnings(@RequestParam(value = "days", defaultValue = "2") int noOfDaysForwad,
			@RequestParam(value = "companies", defaultValue = "10") int noOfCompanies) {

		Stocker stocker = new Stocker();
		try {
			return stocker.calculateLargestCompaniesByFutureEarnings(noOfDaysForwad, noOfCompanies);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping(value = "/stocker/largestCompanies/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public CompaniesIteration getLargestCompanies(
			@RequestParam(value = "iterationNo", defaultValue = "-1") int iterationNo) {

		Stocker stocker = new Stocker();
		try {
			return stocker.getLargestCompaniesAnalysisByIteration(iterationNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
