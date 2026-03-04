package org.velvet.model.service;

import org.velvet.model.issue.Issue;
import org.velvet.model.issue.IssueStatus;
import org.velvet.util.DateTimeUtil;
import org.velvet.util.FileHandler;
import org.velvet.util.IdGenerator;
import org.velvet.util.ValidationUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class IssueService {
    private static final Path ISSUES_FILE = Path.of("src/main/resources/data/issues.txt");

    public IssueService() {
        FileHandler.ensureFile(ISSUES_FILE);
    }

    public Issue raiseIssue(String bookingId, String customerId, String hallId, String description) {
        ValidationUtil.requireNotBlank(bookingId, "Booking ID");
        ValidationUtil.requireNotBlank(customerId, "Customer ID");
        ValidationUtil.requireNotBlank(hallId, "Hall ID");
        ValidationUtil.requireNotBlank(description, "Issue Description");
        ValidationUtil.requireNoPipe(description, "Issue Description");

        Issue issue = new Issue(
                IdGenerator.generate("ISS"),
                bookingId.trim(),
                customerId.trim(),
                hallId.trim(),
                description.trim(),
                IssueStatus.IN_PROGRESS,
                "",
                "",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<Issue> issues = getAllIssues();
        issues.add(issue);
        saveAll(issues);
        return issue;
    }

    public void assignScheduler(String issueId, String schedulerId, String managerResponse) {
        Issue issue = findById(issueId);
        if (issue == null) {
            throw new IllegalArgumentException("Issue not found.");
        }
        ValidationUtil.requireNotBlank(schedulerId, "Scheduler ID");

        issue.setAssignedSchedulerId(schedulerId.trim());
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setManagerResponse(managerResponse == null ? "" : managerResponse.trim());
        issue.setUpdatedAt(LocalDateTime.now());
        updateIssue(issue);
    }

    public void updateStatus(String issueId, IssueStatus status, String managerResponse) {
        Issue issue = findById(issueId);
        if (issue == null) {
            throw new IllegalArgumentException("Issue not found.");
        }

        issue.setStatus(status);
        if (managerResponse != null && !managerResponse.isBlank()) {
            issue.setManagerResponse(managerResponse.trim());
        }
        issue.setUpdatedAt(LocalDateTime.now());
        updateIssue(issue);
    }

    public Issue findById(String issueId) {
        return getAllIssues().stream().filter(issue -> issue.getId().equals(issueId)).findFirst().orElse(null);
    }

    public List<Issue> getAllIssues() {
        List<String> lines = FileHandler.readAllLines(ISSUES_FILE);
        List<Issue> issues = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            issues.add(parseIssue(line));
        }
        issues.sort(Comparator.comparing(Issue::getCreatedAt).reversed());
        return issues;
    }

    public List<Issue> getIssuesByCustomer(String customerId) {
        return getAllIssues().stream()
                .filter(issue -> issue.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Issue> searchIssues(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return getAllIssues().stream()
                .filter(issue -> key.isEmpty() ||
                        issue.getId().toLowerCase(Locale.ROOT).contains(key) ||
                        issue.getDescription().toLowerCase(Locale.ROOT).contains(key) ||
                        issue.getStatus().name().toLowerCase(Locale.ROOT).contains(key))
                .collect(Collectors.toList());
    }

    private void updateIssue(Issue updatedIssue) {
        List<Issue> issues = getAllIssues();
        for (int i = 0; i < issues.size(); i++) {
            if (issues.get(i).getId().equals(updatedIssue.getId())) {
                issues.set(i, updatedIssue);
                break;
            }
        }
        saveAll(issues);
    }

    private Issue parseIssue(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 10) {
            throw new IllegalArgumentException("Corrupted issue record: " + line);
        }

        return new Issue(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parseIssueStatus(parts[5]),
                parts[6],
                parts[7],
                DateTimeUtil.parseDateTime(parts[8]),
                DateTimeUtil.parseDateTime(parts[9])
        );
    }

    private IssueStatus parseIssueStatus(String value) {
        if ("OPEN".equalsIgnoreCase(value)) {
            return IssueStatus.IN_PROGRESS;
        }
        return IssueStatus.valueOf(value);
    }

    private void saveAll(List<Issue> issues) {
        List<String> lines = issues.stream().map(Issue::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(ISSUES_FILE, lines);
    }
}
