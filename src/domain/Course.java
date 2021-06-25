package domain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Course {
	private String id;
	private String name;
	private int units;
	
	private List<Course> prerequisites;

	public Course(String id, String name, int units) {
		this.id = id;
		this.name = name;
		this.units = units;
		prerequisites = new ArrayList<Course>();
	}
	
	public void addPre(Course c) {
		getPrerequisites().add(c);
	}

	public Course withPre(Course... pres) {
		prerequisites.addAll(Arrays.asList(pres));
		return this;//TODO Doesn't need to returns it's instance!
	}

	public List<Course> getPrerequisites() {
		return prerequisites;
	}

	//TODO double check, Override?
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" {");
		for (Course pre : getPrerequisites()) {
			sb.append(pre.getName());
			sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public int getUnits() {
		return units;
	}

	public String getId() {
		return id;
	}

	//TODO override, if obj's type isn't Course exception is thrown
	public boolean equals(Object obj) {
		Course other = (Course)obj;
		return id.equals(other.id);
	}
}
