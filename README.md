### Hall Booking Management System

Desktop GUI application for managing hall bookings with 4 roles:
- `Customer`
- `Scheduler`
- `Administrator`
- `Manager`

### Tech
- Java (Swing)
- Maven
- Text-file persistence (`src/main/resources/data/*.txt`)

### Run
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=org.velvet.Main
```

If `exec-maven-plugin` is not available in your environment, run `org.velvet.Main` directly from your IDE.

### Default Login Credentials
- Administrator: `admin` / `admin123`
- Scheduler: `scheduler` / `scheduler123`
- Manager: `manager` / `manager123`
- Customer: `customer` / `customer123`

### Date/Time Input Format
- `yyyy-MM-dd HH:mm` (example: `2026-03-10 09:00`)
