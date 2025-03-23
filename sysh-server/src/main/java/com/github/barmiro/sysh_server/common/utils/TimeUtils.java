package com.github.barmiro.sysh_server.common.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.github.barmiro.sysh_server.common.records.TimestampRange;

public class TimeUtils {
	
	
	
	public static List<TimestampRange> generateDateRangeSeries(LocalDateTime start, LocalDateTime end, String step) {
		

	    ChronoUnit unit = getChronoUnit(step);
	     
	    List<TimestampRange> dateRanges = new ArrayList<>();
	    LocalDateTime currentStart = start;
	    
	    while (currentStart.isBefore(end)) {
	    	LocalDateTime nextStart = currentStart.plus(1, unit);
	    	dateRanges.add(new TimestampRange(
	    		Timestamp.valueOf(currentStart),
	    		Timestamp.valueOf(nextStart.minusSeconds(1))
	    		)
	    	);
	    	
	    	currentStart = nextStart;
	    }
	    
	    return dateRanges; 
	     
	     
	}
	
	
//	TODO: determine whether sub-day steps should be considered
	public static ChronoUnit getChronoUnit(String input) {
		return switch(input.toLowerCase()) {
			case "day" -> ChronoUnit.DAYS;
			case "week" -> ChronoUnit.WEEKS;
			case "month" -> ChronoUnit.MONTHS;
			case "year" -> ChronoUnit.YEARS;
			default -> throw new IllegalArgumentException("Invalid time unit: " + input);
		};
	}

}
