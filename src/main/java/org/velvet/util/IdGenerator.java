package org.velvet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class IdGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private IdGenerator() {
    }

    public static String generate(String prefix) {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + "-" + LocalDateTime.now().format(FORMATTER) + "-" + suffix;
    }
}
