package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    //TODO  Could have better variable names
	public void enroll(Student student, List<CSE> courses) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = student.getTranscript();

		for (CSE o : courses) {
		    //TODO method extraction checkIsPassed
            for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
                for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                    if (r.getKey().equals(o.getCourse()) && r.getValue() >= 10)
                        throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
			List<Course> prereqs = o.getCourse().getPrerequisites();
			nextPre://continue
			for (Course pre : prereqs) {

                for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
                    for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                        if (r.getKey().equals(pre) && r.getValue() >= 10)//TODO not the condition and replace continue with exception
                            continue nextPre;
                    }
				}
				throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
			}
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                //TODO method extraction
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
		}

		int unitsRequested = courses.stream().mapToInt(o -> o.getCourse().getUnits()).sum();

        double gpa = student.getGpa();

        //TODO hasPassedMinimumGPAUnits
		if ((gpa < 12 && unitsRequested > 14) ||
				(gpa < 16 && unitsRequested > 16) ||
				(unitsRequested > 20))
			throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
		for (CSE o : courses)
			student.takeCourse(o.getCourse(), o.getSection());
	}
}
