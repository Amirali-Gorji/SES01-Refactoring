package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    private final float MINIMUM_SCORE = 10;
    private String id;
    private String name;

    private Map<Term, Map<Course, Double>> transcript;
    private List<CourseSection> currentTerm;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.transcript = new HashMap<>();
        this.currentTerm = new ArrayList<>();
    }

    public void takeCourse(Course c, int section) {
        currentTerm.add(new CourseSection(c, section));
    }

    public Map<Term, Map<Course, Double>> getTranscript() {
        return transcript;
    }

    public void addTranscriptRecord(Course course, Term term, double grade) {
        if (!transcript.containsKey(term))
            transcript.put(term, new HashMap<>());
        transcript.get(term).put(course, grade);
    }

    public List<CourseSection> getCurrentTerm() {
        return currentTerm;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public double calculateGPA() {
        double points = 0;
        int totalUnits = 0;
        for (Map.Entry<Term, Map<Course, Double>> tr : this.transcript.entrySet()) {
            for (Map.Entry<Course, Double> r : tr.getValue().entrySet()) {
                points += r.getValue() * r.getKey().getUnits();
                totalUnits += r.getKey().getUnits();
            }
        }
        return points / totalUnits;
    }

    public boolean isPassed(Term term, Course course) {
        double grade = this.transcript.get(term).get(course);
        return grade >= this.MINIMUM_SCORE;
    }

    public boolean isPassed(Course course) {
        for (Map.Entry<Term, Map<Course, Double>> transcript : this.transcript.entrySet()) {
            for (Map.Entry<Course, Double> record : transcript.getValue().entrySet()) {
                if (record.getKey().equals(course) && this.isPassed(transcript.getKey(), record.getKey()))
                    return true;
            }
        }
        return false;
    }


}
