package org.velvet.model.service;

import org.velvet.model.hall.Auditorium;
import org.velvet.model.hall.BanquetHall;
import org.velvet.model.hall.Hall;
import org.velvet.model.hall.MeetingRoom;
import org.velvet.model.issue.MaintenanceTask;
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

public class HallService {
    private static final Path HALLS_FILE = Path.of("src/main/resources/data/halls.txt");
    private static final Path MAINTENANCE_FILE = Path.of("src/main/resources/data/maintenance.txt");

    public HallService() {
        FileHandler.ensureFile(HALLS_FILE);
        FileHandler.ensureFile(MAINTENANCE_FILE);
        seedDefaultHallsIfEmpty();
    }

    public Hall addHall(String type, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        ValidationUtil.requireNotBlank(type, "Hall Type");
        ValidationUtil.requireNotBlank(name, "Hall Name");
        ValidationUtil.requireNoPipe(name, "Hall Name");
        ValidationUtil.requireNoPipe(remarks, "Remarks");

        if (!DateTimeUtil.isWithinBusinessHours(availableFrom, availableTo)) {
            throw new IllegalArgumentException("Availability must be between 08:00 and 18:00 and end after start.");
        }

        Hall hall = createHallByType(type.trim(), IdGenerator.generate("HAL"), name.trim(), availableFrom, availableTo, remarks == null ? "" : remarks.trim());
        List<Hall> halls = getAllHalls();
        halls.add(hall);
        saveHalls(halls);
        return hall;
    }

    public void updateHall(String hallId, String type, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        Hall existing = findHallById(hallId);
        if (existing == null) {
            throw new IllegalArgumentException("Hall not found.");
        }

        ValidationUtil.requireNotBlank(type, "Hall Type");
        ValidationUtil.requireNotBlank(name, "Hall Name");
        ValidationUtil.requireNoPipe(name, "Hall Name");
        ValidationUtil.requireNoPipe(remarks, "Remarks");

        if (!DateTimeUtil.isWithinBusinessHours(availableFrom, availableTo)) {
            throw new IllegalArgumentException("Availability must be between 08:00 and 18:00 and end after start.");
        }

        Hall updated = createHallByType(type.trim(), existing.getId(), name.trim(), availableFrom, availableTo, remarks == null ? "" : remarks.trim());

        List<Hall> halls = getAllHalls();
        for (int i = 0; i < halls.size(); i++) {
            if (halls.get(i).getId().equals(hallId)) {
                halls.set(i, updated);
                break;
            }
        }
        saveHalls(halls);
    }

    public void deleteHall(String hallId) {
        List<Hall> halls = getAllHalls();
        boolean removed = halls.removeIf(h -> h.getId().equals(hallId));
        if (!removed) {
            throw new IllegalArgumentException("Hall not found.");
        }
        saveHalls(halls);

        List<MaintenanceTask> tasks = getAllMaintenanceTasks();
        tasks.removeIf(task -> task.getHallId().equals(hallId));
        saveMaintenance(tasks);
    }

    public void setAvailability(String hallId, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        Hall hall = findHallById(hallId);
        if (hall == null) {
            throw new IllegalArgumentException("Hall not found.");
        }
        if (!DateTimeUtil.isWithinBusinessHours(availableFrom, availableTo)) {
            throw new IllegalArgumentException("Availability must be between 08:00 and 18:00 and end after start.");
        }

        hall.setAvailableFrom(availableFrom);
        hall.setAvailableTo(availableTo);
        hall.setRemarks(remarks == null ? "" : remarks.trim());

        List<Hall> halls = getAllHalls();
        for (int i = 0; i < halls.size(); i++) {
            if (halls.get(i).getId().equals(hallId)) {
                halls.set(i, hall);
                break;
            }
        }
        saveHalls(halls);
    }

    public Hall findHallById(String hallId) {
        return getAllHalls().stream().filter(h -> h.getId().equals(hallId)).findFirst().orElse(null);
    }

    public List<Hall> getAllHalls() {
        List<String> lines = FileHandler.readAllLines(HALLS_FILE);
        List<Hall> halls = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            halls.add(parseHall(line));
        }
        halls.sort(Comparator.comparing(Hall::getId));
        return halls;
    }

    public List<Hall> searchHalls(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return getAllHalls().stream()
                .filter(hall -> key.isEmpty() ||
                        hall.getId().toLowerCase(Locale.ROOT).contains(key) ||
                        hall.getName().toLowerCase(Locale.ROOT).contains(key) ||
                        hall.getType().toLowerCase(Locale.ROOT).contains(key))
                .collect(Collectors.toList());
    }

    public MaintenanceTask addMaintenanceTask(String hallId, LocalDateTime start, LocalDateTime end, String remarks, String schedulerId) {
        Hall hall = findHallById(hallId);
        if (hall == null) {
            throw new IllegalArgumentException("Hall not found.");
        }
        if (!DateTimeUtil.isWithinBusinessHours(start, end)) {
            throw new IllegalArgumentException("Maintenance time must be between 08:00 and 18:00 and end after start.");
        }
        ValidationUtil.requireNoPipe(remarks, "Remarks");

        MaintenanceTask task = new MaintenanceTask(
                IdGenerator.generate("MTN"),
                hallId,
                hall.getName(),
                start,
                end,
                remarks == null ? "" : remarks.trim(),
                schedulerId
        );

        List<MaintenanceTask> tasks = getAllMaintenanceTasks();
        tasks.add(task);
        saveMaintenance(tasks);
        return task;
    }

    public void updateMaintenanceTask(String taskId, String hallId, LocalDateTime start, LocalDateTime end, String remarks) {
        Hall hall = findHallById(hallId);
        if (hall == null) {
            throw new IllegalArgumentException("Hall not found.");
        }
        if (!DateTimeUtil.isWithinBusinessHours(start, end)) {
            throw new IllegalArgumentException("Maintenance time must be between 08:00 and 18:00 and end after start.");
        }

        List<MaintenanceTask> tasks = getAllMaintenanceTasks();
        MaintenanceTask task = tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Maintenance task not found."));

        task.setHallId(hallId);
        task.setHallName(hall.getName());
        task.setStart(start);
        task.setEnd(end);
        task.setRemarks(remarks == null ? "" : remarks.trim());
        saveMaintenance(tasks);
    }

    public void deleteMaintenanceTask(String taskId) {
        List<MaintenanceTask> tasks = getAllMaintenanceTasks();
        boolean removed = tasks.removeIf(task -> task.getId().equals(taskId));
        if (!removed) {
            throw new IllegalArgumentException("Maintenance task not found.");
        }
        saveMaintenance(tasks);
    }

    public List<MaintenanceTask> getAllMaintenanceTasks() {
        List<String> lines = FileHandler.readAllLines(MAINTENANCE_FILE);
        List<MaintenanceTask> tasks = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            tasks.add(parseTask(line));
        }
        tasks.sort(Comparator.comparing(MaintenanceTask::getStart));
        return tasks;
    }

    public List<MaintenanceTask> searchMaintenanceTasks(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return getAllMaintenanceTasks().stream()
                .filter(task -> key.isEmpty() ||
                        task.getId().toLowerCase(Locale.ROOT).contains(key) ||
                        task.getHallName().toLowerCase(Locale.ROOT).contains(key) ||
                        task.getRemarks().toLowerCase(Locale.ROOT).contains(key))
                .collect(Collectors.toList());
    }

    public boolean isHallUnderMaintenance(String hallId, LocalDateTime start, LocalDateTime end) {
        return getAllMaintenanceTasks().stream()
                .filter(task -> task.getHallId().equals(hallId))
                .anyMatch(task -> task.overlaps(start, end));
    }

    private Hall parseHall(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Corrupted hall record: " + line);
        }
        String id = parts[0];
        String type = parts[1];
        String name = parts[2];
        int capacity = Integer.parseInt(parts[3]);
        double rate = Double.parseDouble(parts[4]);
        LocalDateTime availableFrom = DateTimeUtil.parseDateTime(parts[5]);
        LocalDateTime availableTo = DateTimeUtil.parseDateTime(parts[6]);
        String remarks = parts[7];
        String status = parts.length > 8 ? parts[8] : "ACTIVE";

        Hall hall = createHallByType(type, id, name, availableFrom, availableTo, remarks);
        hall.setCapacity(capacity);
        hall.setRatePerHour(rate);
        hall.setStatus(status == null || status.isBlank() ? "ACTIVE" : status);
        return hall;
    }

    private MaintenanceTask parseTask(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) {
            throw new IllegalArgumentException("Corrupted maintenance record: " + line);
        }
        return new MaintenanceTask(
                parts[0],
                parts[1],
                parts[2],
                DateTimeUtil.parseDateTime(parts[3]),
                DateTimeUtil.parseDateTime(parts[4]),
                parts[5],
                parts[6]
        );
    }

    private Hall createHallByType(String type, String id, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "auditorium" -> new Auditorium(id, name, availableFrom, availableTo, remarks);
            case "banquet hall", "banquet" -> new BanquetHall(id, name, availableFrom, availableTo, remarks);
            case "meeting room", "meeting" -> new MeetingRoom(id, name, availableFrom, availableTo, remarks);
            default -> throw new IllegalArgumentException("Unknown hall type: " + type);
        };
    }

    private void saveHalls(List<Hall> halls) {
        List<String> lines = halls.stream().map(Hall::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(HALLS_FILE, lines);
    }

    private void saveMaintenance(List<MaintenanceTask> tasks) {
        List<String> lines = tasks.stream().map(MaintenanceTask::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(MAINTENANCE_FILE, lines);
    }

    private void seedDefaultHallsIfEmpty() {
        if (!FileHandler.readAllLines(HALLS_FILE).isEmpty()) {
            return;
        }

        LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime to = from.withHour(18);

        List<Hall> halls = new ArrayList<>();
        halls.add(new Auditorium("HAL-001", "Grand Auditorium", from, to, "Available for conferences."));
        halls.add(new BanquetHall("HAL-002", "Royal Banquet", from, to, "Available for weddings."));
        halls.add(new MeetingRoom("HAL-003", "Meeting Room A", from, to, "Available for meetings."));

        saveHalls(halls);
    }
}
