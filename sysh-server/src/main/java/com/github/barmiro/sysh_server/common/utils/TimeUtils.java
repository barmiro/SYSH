package com.github.barmiro.sysh_server.common.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.github.barmiro.sysh_server.common.records.OffsetDateTimeRange;
import com.github.barmiro.sysh_server.common.records.TimeSeriesStep;
import com.github.barmiro.sysh_server.common.records.TimestampRange;

public class TimeUtils {
	
	
	
	public static List<TimestampRange> generateDateRangeSeries(LocalDateTime start, LocalDateTime end, String step) {
		

	    TimeSeriesStep timeStep = getTimeStep(step);
	     
	    List<TimestampRange> dateRanges = new ArrayList<>();
	    LocalDateTime currentStart = start;
	    
	    while (currentStart.isBefore(end)) {
	    	LocalDateTime nextStart = currentStart.plus(timeStep.count(), timeStep.unit());
	    	dateRanges.add(new TimestampRange(
	    		Timestamp.valueOf(currentStart),
	    		Timestamp.valueOf(nextStart.minusSeconds(1))
	    		)
	    	);
	    	
	    	currentStart = nextStart;
	    }
	    
	    return dateRanges; 
	     
	     
	}
	
	
public static List<OffsetDateTimeRange> generateOffsetDateTimeRangeSeries(ZonedDateTime start, ZonedDateTime end, String step) {
		

	    TimeSeriesStep timeStep = getTimeStep(step);
	     
	    List<OffsetDateTimeRange> dateRanges = new ArrayList<>();
	    ZonedDateTime currentStart = start;
	    
	    while (currentStart.isBefore(end)) {
	    	ZonedDateTime nextStart = currentStart.plus(timeStep.count(), timeStep.unit());
	    	dateRanges.add(new OffsetDateTimeRange(
	    		currentStart.toOffsetDateTime(),
	    		nextStart.minusSeconds(1).toOffsetDateTime()
	    		)
	    	);
	    	
	    	currentStart = nextStart;
	    }
	    
	    return dateRanges; 
	     
	     
	}
	
	
//	TODO: consider dynamic timestep generation; I'm trying this to get pre-determined options only
	public static TimeSeriesStep getTimeStep(String input) {
		return switch(input.toLowerCase()) {
				case "hour" -> new TimeSeriesStep(ChronoUnit.HOURS, 1);
				case "day" -> new TimeSeriesStep(ChronoUnit.DAYS, 1);
				case "week" -> new TimeSeriesStep(ChronoUnit.WEEKS, 1);
				case "month" -> new TimeSeriesStep(ChronoUnit.MONTHS, 1);
				case "quarter" -> new TimeSeriesStep(ChronoUnit.MONTHS, 3);
				case "year" -> new TimeSeriesStep(ChronoUnit.YEARS, 1);
				default -> throw new IllegalArgumentException("Invalid time unit: " + input);
		};
	}

}
