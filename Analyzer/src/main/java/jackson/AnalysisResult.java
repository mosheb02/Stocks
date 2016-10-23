package jackson;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalysisResult {
	private int iterationNo;
	private int subIterationAnylsisNo;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	List<Network> networks = new LinkedList<>();

	public int getIterationNo() {
		return iterationNo;
	}

	public void setIterationNo(int iterationNo) {
		this.iterationNo = iterationNo;
	}

	public int getSubIterationAnylsisNo() {
		return subIterationAnylsisNo;
	}

	public void setSubIterationAnylsisNo(int subIterationAnylsisNo) {
		this.subIterationAnylsisNo = subIterationAnylsisNo;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public List<Network> getNetworks() {
		return networks;
	}

	public void setNetworks(List<Network> networks) {
		this.networks = networks;
	}

	public class Network {
		private String name;
		private Map<String, AnalyzedCompany> companies = new ConcurrentHashMap<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public  Map<String, AnalyzedCompany> getCompanies() {
			return companies;
		}

		public void setCompanies( Map<String, AnalyzedCompany> companies) {
			this.companies = companies;
		}

		public class AnalyzedCompany {
			private String ticker;
			private String name;
			private List<String> relatedKeyWords = new LinkedList<>();
			private double numOfDistinctAppearances = 0;
			private boolean isNumOfDistinctAppearancesAbnormal;
			private double nominalGrade = 0;
			private boolean isNominalGradeAbnormal;
			private double weightedGrade = 0;
			private boolean isWeightedGradeAbnormal;
			private List<AnalyzedTwitt> analyzedTwitts = new LinkedList<>();

			public String getTicker() {
				return ticker;
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

			public double getNumOfDistinctAppearances() {
				return numOfDistinctAppearances;
			}

			public void setNumOfDistinctAppearances(double numOfDistinctAppearances) {
				this.numOfDistinctAppearances = numOfDistinctAppearances;
			}

			public void increaseNumOfDistinctAppearances() {
				numOfDistinctAppearances++;
			}

			public boolean isNumOfDistinctAppearancesAbnormal() {
				return isNumOfDistinctAppearancesAbnormal;
			}

			public void setNumOfDistinctAppearancesAbnormal(boolean isNumOfDistinctAppearancesAbnormal) {
				this.isNumOfDistinctAppearancesAbnormal = isNumOfDistinctAppearancesAbnormal;
			}

			public double getNominalGrade() {
				return nominalGrade;
			}

			public void setNominalGrade(double nominalGrade) {
				this.nominalGrade = nominalGrade;
			}

			public boolean isNominalGradeAbnormal() {
				return isNominalGradeAbnormal;
			}

			public void setNominalGradeAbnormal(boolean isNominalGradeAbnormal) {
				this.isNominalGradeAbnormal = isNominalGradeAbnormal;
			}

			public double getWeightedGrade() {
				return weightedGrade;
			}

			public void setWeightedGrade(double weightedGrade) {
				this.weightedGrade = weightedGrade;
			}

			public boolean isWeightedGradeAbnormal() {
				return isWeightedGradeAbnormal;
			}

			public void setWeightedGradeAbnormal(boolean isWeightedGradeAbnormal) {
				this.isWeightedGradeAbnormal = isWeightedGradeAbnormal;
			}

			public List<String> getRelatedKeyWords() {
				return relatedKeyWords;
			}

			public void setRelatedKeyWords(List<String> relatedKeyWords) {
				this.relatedKeyWords = relatedKeyWords;
			}

			public List<AnalyzedTwitt> getAnalyzedTwitts() {
				return analyzedTwitts;
			}

			public void setAnalyzedTwitts(List<AnalyzedTwitt> analyzedTwitts) {
				this.analyzedTwitts = analyzedTwitts;
			}
		}
	}

}
