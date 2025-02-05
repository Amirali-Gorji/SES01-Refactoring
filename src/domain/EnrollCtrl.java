package domain;

import domain.exceptions.EnrollmentRulesViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EnrollCtrl {
    public void enroll(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        List<String> errorMessages = new ArrayList<>();
        for (CSE o : courses) {
            // check if student passed the course
            if (s.isPassed(o.getCourse())) {
                errorMessages.add(String.format("The student has already passed %s", o.getCourse().getName()));
            }
            // check if student passed all prerequisites
            List<Course> prereqs = o.getCourse().getPrerequisites();
            for (Course pre : prereqs) {
                if (!s.isPassed(pre)) {
                    errorMessages.add(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
                }
            }

            // check if the course has exam time conflict with other course
            // or taken twice at same time
            for (CSE o2 : courses) {
                if (o.hasTimeConflict(o2))
                    errorMessages.add(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.hasCommonCourse(o2))
                    errorMessages.add(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }

        // check gpa violations
        int unitsRequested = courses.stream().flatMapToInt(cse -> IntStream.of(cse.getCourse().getUnits())).sum();
        double gpa = s.calculateGPA();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            errorMessages.add(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
        if (!errorMessages.isEmpty()) {
            throw new EnrollmentRulesViolationException(String.join("\n", errorMessages));
        }
        for (CSE o : courses)
            s.takeCourse(o.getCourse(), o.getSection());
    }
}
