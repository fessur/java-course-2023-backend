package edu.java.client.retry;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.backoff.SleepingBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;

@Slf4j
public class LinearBackOffPolicy implements SleepingBackOffPolicy<LinearBackOffPolicy> {
    public static final long DEFAULT_INITIAL_INTERVAL = 100L;

    public static final long DEFAULT_MAX_INTERVAL = 10000L;

    public static final long DEFAULT_INCREMENT = 2;

    private long initialInterval = DEFAULT_INITIAL_INTERVAL;

    private long maxInterval = DEFAULT_MAX_INTERVAL;

    private long increment = DEFAULT_INCREMENT;

    @Setter
    private Sleeper sleeper = new ThreadWaitSleeper();

    @Override
    public LinearBackOffPolicy withSleeper(Sleeper sleeper) {
        LinearBackOffPolicy res = new LinearBackOffPolicy();
        cloneValues(res);
        res.setSleeper(sleeper);
        return res;
    }

    protected void cloneValues(LinearBackOffPolicy target) {
        target.setInitialInterval(this.initialInterval);
        target.setMaxInterval(this.maxInterval);
        target.setIncrement(this.increment);
        target.setSleeper(this.sleeper);
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval > 1 ? initialInterval : 1;
    }

    public void setIncrement(long increment) {
        this.increment = increment > 0 ? increment : 0;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval > 0 ? maxInterval : 1;
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffContext(this.initialInterval, this.increment, this.maxInterval);
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffContext context = (LinearBackOffContext) backOffContext;
        try {
            long sleepTime = context.getSleepAndIncrement();
            if (log.isDebugEnabled()) {
                log.debug("Sleeping for {}", sleepTime);
            }
            this.sleeper.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }
    }

    private static class LinearBackOffContext implements BackOffContext {

        private final long increment;

        private long interval;

        private final long maxInterval;

        LinearBackOffContext(long interval, long increment, long maxInterval) {
            this.interval = interval;
            this.increment = increment;
            this.maxInterval = maxInterval;
        }

        public synchronized long getSleepAndIncrement() {
            long sleep = interval;
            if (sleep > maxInterval) {
                sleep = maxInterval;
            } else {
                this.interval = getNextInterval();
            }
            return sleep;
        }

        protected long getNextInterval() {
            return interval + increment;
        }
    }
}
