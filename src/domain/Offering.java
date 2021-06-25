package domain;
import java.util.Date;

public class Offering {
	private Course course;
	private int section;//TODO section is badly named group could be a better choice
	private Date examDate;

	public Offering(Course course) {
		this.course = course;
		this.section = 1;
		this.examDate = null;
	}

	public Offering(Course course, Date examDate) {
		this.course = course;
		this.section = 1;
		this.examDate = examDate;
	}

	public Offering(Course course, Date examDate, int section) {
		this.course = course;
		this.section = section;
		this.examDate = examDate;
	}
	
	public Course getCourse() {
		return course;
	}
	
	public String toString() {
		return course.getName() + " - " + section;
	}
	
	public Date getExamTime() {
		return examDate;
	}

	public int getSection() { return section; }
}
