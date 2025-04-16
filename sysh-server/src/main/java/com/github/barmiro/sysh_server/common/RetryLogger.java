package com.github.barmiro.sysh_server.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class RetryLogger implements RetryListener {
	
	 private static final Logger log = LoggerFactory.getLogger(RetryLogger.class);
	 
	 @Override
	 public <T, E extends Throwable> boolean open(
			 RetryContext context,
			 RetryCallback<T, E> callback)
	 {
		 return true;
	 }
	 
	 
	@Override
    public <T, E extends Throwable> void close(
    		RetryContext context,
    		RetryCallback<T, E> callback,
    		Throwable throwable)
	{
	    if (throwable != null) {
	        log.error(
	        		"{}: All retry attempts failed. Final exception: {}",
	        		context.getAttribute("label"),
	        		throwable.toString());
	    } else {
	        log.info(
	        		"{}: Retry process completed successfully after {} attempt(s).",
	        		context.getAttribute("label"),
	        		context.getRetryCount());
	    }
	}

	
    @Override
    public <T, E extends Throwable> void onError(
    		RetryContext context,
    		RetryCallback<T, E> callback,
    		Throwable throwable)
    {
        log.warn(
        		"{}: Retry attempt #{} failed due to: {}",
        		context.getAttribute("label"),
        		context.getRetryCount(),
        		throwable.toString());
    }
}
