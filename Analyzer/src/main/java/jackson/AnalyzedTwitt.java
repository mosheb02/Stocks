package jackson;

public class AnalyzedTwitt {
	private String text;
	private String userName;
	private double grade = 0;

	public AnalyzedTwitt() {

	}

	public AnalyzedTwitt(final String text, final String userName, double grade) {
		this.text = text;
		this.userName = userName;
		this.grade = grade;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}
}
