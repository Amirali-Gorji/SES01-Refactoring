package domain;

import domain.exceptions.EnrollmentRulesViolationException;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EnrollCtrl {
    public void enroll(Student s, List<CSE> courses) throws EnrollmentRulesViolationException {
        Map<Term, Map<Course, Double>> transcript = s.getTranscript();
        for (CSE o : courses) {
            // check if student passed the course
            if (s.isPassed(o.getCourse())){
                throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
            }
            // check if student passed all prerequisites
            List<Course> prereqs = o.getCourse().getPrerequisites();
            nextPre:
            for (Course pre : prereqs) {
                for (Map.Entry<Term, Map<Course, Double>> tr : transcript.entrySet()) {
                    for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                        if (r.getKey().equals(pre) && s.isPassed(tr.getKey(), r.getKey()))
                            continue nextPre;
                    }
                }
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
            }

            // check if the course has exam time conflict with other course
            // or taken twice at same time
            for (CSE o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
        }

        // check gpa violations
        int unitsRequested = courses.stream().flatMapToInt(cse -> IntStream.of(cse.getCourse().getUnits())).sum();
        double gpa = s.calculateGPA();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
        for (CSE o : courses)
            s.takeCourse(o.getCourse(), o.getSection());
    }
}
