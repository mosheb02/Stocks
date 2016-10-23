package jackson;

import java.util.List;

public class CompaniesIteration {
	private int iterationNo;
	
	private String iterationDateTime ;
	private int noOfDaysForward;
	
	public int getIterationNo() {
		return iterationNo;
	}
	public void setIterationNo(int iterationNo) {
		this.iterationNo = iterationNo;
	}
	public String getIterationDateTime() {
		return iterationDateTime;
	}
	public void setIterationDateTime(String iterationDateTime) {
		this.iterationDateTime = iterationDateTime;
	}
	public int getNoOfDaysForward() {
		return noOfDaysForward;
	}
	public void setNoOfDaysForward(int noOfDaysForward) {
		this.noOfDaysForward = noOfDaysForward;
	}
	public List<CompanySummaryDetails> getCompanies() {
		return companies;
	}
	public void setCompanies(List<CompanySummaryDetails> companies) {
		this.companies = companies;
	}
	private List<CompanySummaryDetails> companies;
}
