package ws.api;

import java.util.List;

import org.bson.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.Stocker;
import core.jackson.CompanySummaryDetails;
import twitter.TwitterStocker;

@RestController
public class TwitterController {
	@RequestMapping(value = "/stocker/twitter/getUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Document> getUsers(@RequestParam(value = "startingIndex", defaultValue = "0") int startingIndex,
			@RequestParam(value = "numberOfUsers", defaultValue = "100") int numberOfUsers) {

		TwitterStocker twitterStocker = new TwitterStocker(); 
		try {
			return twitterStocker.getUsers(startingIndex, numberOfUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
