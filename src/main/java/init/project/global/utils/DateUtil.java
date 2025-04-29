package init.project.global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    /** 현재 날짜 반환 */
    public static String getCurrentDate(String code) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return format(currentDateTime, code);
    }

    /** 현재 날짜 기준으로 N일 전/후 반환 */
    public static String getDatePlusDays(int days, String code) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(days);
        return format(targetDate, code);
    }

    /** 두 날짜(LocalDateTime) 간 차이 (일 단위) */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toDays();
    }

    /** 현재 날짜가 특정 기간 사이에 있는지 */
    public static boolean isBetween(LocalDateTime target, LocalDateTime from, LocalDateTime to) {
        return !target.isBefore(from) && !target.isAfter(to);
    }

    /** 날짜 포맷 변환 */
    public static String format(LocalDateTime dateTime, String code) {
        DateTimeFormat format = DateTimeFormat.fromCode(code);
        return dateTime.format(format.getFormatter());
    }

    /** 문자열을 LocalDateTime으로 파싱 */
    public static LocalDateTime parse(String dateStr, String code) {
        DateTimeFormat format = DateTimeFormat.fromCode(code);
        return LocalDateTime.parse(dateStr, format.getFormatter());
    }

    @Getter
    @AllArgsConstructor
    public enum DateTimeFormat {

        YYYYMMDDHHMISS("yyyymmddhhmiss", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
        YYYYMMDD("yyyymmdd", DateTimeFormatter.ofPattern("yyyyMMdd")),
        HHMISS("hhmiss", DateTimeFormatter.ofPattern("HHmmss")),
        DEFAULT("default", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        ;

        private final String code;
        private final DateTimeFormatter formatter;

        public static DateTimeFormat fromCode(String code) {
            for (DateTimeFormat format : values()) {
                if (format.code.equalsIgnoreCase(code))
                    return format;
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }

}