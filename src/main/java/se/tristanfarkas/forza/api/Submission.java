package se.tristanfarkas.forza.api;

import se.tristanfarkas.forza.api.forsete.ForseteFile;

/**
 * Describes a solution to an assignment.
 * @param course The course the solution belongs to
 * @param assignmentIndex The index of the assignment that the solution is meant for
 * @param file The file containing the solution.
 */
public record Submission(String course, int assignmentIndex, ForseteFile file) {
}
