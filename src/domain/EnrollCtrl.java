package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    public void enroll(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        checkForAlreadyPassedCourses(student, offerings);
        checkForPrerequisiteRequirements(student, offerings);
        checkForExamTimeConflict(offerings);
        checkForDuplicateEnrollRequest(offerings);
        checkForGPALimit(student, offerings);

        for (Offering o : offerings)
            student.takeCourse(o.getCourse(), o.getSection());
    }

    private void checkForAlreadyPassedCourses(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        for (Offering o : offerings) {
            if (student.hasPassed(o.getCourse())) {
                throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
            }
        }
    }

    private void checkForPrerequisiteRequirements(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        for (Offering o : offerings) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if (!student.hasPassed(pre)) {
                    throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
                }
            }
        }
    }

    private void checkForExamTimeConflict(List<Offering> offerings) throws EnrollmentRulesViolationException {
        for (Offering o : offerings) {
            for (Offering o2 : offerings) {
                if (o == o2)
                    continue;

                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
            }
        }
    }

    private void checkForDuplicateEnrollRequest(List<Offering> offerings) throws EnrollmentRulesViolationException {
        for (Offering o : offerings) {
            for (Offering o2 : offerings) {
                if (o == o2)
                    continue;

                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }
    }

    private void checkForGPALimit(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        int unitsRequested = offerings.stream().mapToInt(o -> o.getCourse().getUnits()).sum();

        if ((student.getGpa() < 12 && unitsRequested > 14) ||
                (student.getGpa() < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, student.getGpa()));
    }
}