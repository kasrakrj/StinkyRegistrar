package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    //TODO  Could have better variable names
    public void enroll(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = student.getTranscript();

        for (Offering o : offerings) {
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
            for (Offering o2 : offerings) {
                if (o == o2)
                    continue;
                //TODO method extraction
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }

        checkForGPALimit(student, offerings);

        for (Offering o : offerings)
            student.takeCourse(o.getCourse(), o.getSection());
    }

    private void checkForGPALimit(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        int unitsRequested = offerings.stream().mapToInt(o -> o.getCourse().getUnits()).sum();

        if ((student.getGpa() < 12 && unitsRequested > 14) ||
                (student.getGpa() < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, student.getGpa()));
    }
}