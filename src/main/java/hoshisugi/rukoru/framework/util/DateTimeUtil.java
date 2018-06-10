package hoshisugi.rukoru.framework.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	public static ZonedDateTime toDateTime(final Date date) {
		return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date toDate(final ZonedDateTime dateTime) {
		return Date.from(dateTime.toInstant());
	}

	public static String toString(final Date date) {
		return formatter.format(toDateTime(date));
	}
}
