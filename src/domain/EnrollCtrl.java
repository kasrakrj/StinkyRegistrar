package domain;

import java.util.*;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    public List<EnrollmentRulesViolationException> enroll(Student student, List<Offering> offerings) throws EnrollmentRulesViolationException {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        violations.addAll(checkForAlreadyPassedCourses(student, offerings));
        violations.addAll(checkForPrerequisiteRequirements(student, offerings));
        violations.addAll(checkForExamTimeConflict(offerings));
        violations.addAll(checkForDuplicateEnrollRequest(offerings));
        violations.addAll(checkForGPALimit(student, offerings));

        for (Offering o : offerings)
            student.takeCourse(o.getCourse(), o.getSection());

        return violations;
    }

    private List<EnrollmentRulesViolationException> checkForAlreadyPassedCourses(Student student, List<Offering> offerings) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        for (Offering o : offerings) {
            if (student.hasPassed(o.getCourse())) {
                violations.add(new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName())));
            }
        }

        return violations;
    }

    private List<EnrollmentRulesViolationException> checkForPrerequisiteRequirements(Student student, List<Offering> offerings) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        for (Offering o : offerings) {
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if (!student.hasPassed(pre)) {
                    violations.add(new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName())));
                }
            }
        }

        return violations;
    }

    private List<EnrollmentRulesViolationException> checkForExamTimeConflict(List<Offering> offerings) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        for (Offering o : offerings) {
            for (Offering o2 : offerings) {
                if (o == o2)
                    continue;

                if (o.getExamTime().equals(o2.getExamTime()))
                    violations.add(new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2)));
            }
        }

        return violations;
    }

    private List<EnrollmentRulesViolationException> checkForDuplicateEnrollRequest(List<Offering> offerings) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        for (Offering o : offerings) {
            for (Offering o2 : offerings) {
                if (o == o2)
                    continue;

                if (o.getCourse().equals(o2.getCourse()))
                    violations.add(new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName())));
            }
        }

        return violations;
    }

    private List<EnrollmentRulesViolationException> checkForGPALimit(Student student, List<Offering> offerings) {
        List<EnrollmentRulesViolationException> violations = new ArrayList<EnrollmentRulesViolationException>();

        int unitsRequested = offerings.stream().mapToInt(o -> o.getCourse().getUnits()).sum();

        if ((student.getGpa() < 12 && unitsRequested > 14) ||
                (student.getGpa() < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            violations.add(new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, student.getGpa())));

        return violations;
    }
}