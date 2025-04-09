package net.wirelabs.eventbus.common;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.NoArgsConstructor;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.eventbus.EventBusClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;

@NoArgsConstructor
public abstract class BaseTest {
    protected LogVerifier logVerifier = new LogVerifier();

    @AfterEach
    void after() {
        shutdownAndAssertFinishedClients();
    }

    protected void shutdownAndAssertFinishedClients(EventBusClient... clients) {
        EventBus.shutdown();
        for (EventBusClient c: clients) {
            Assertions.assertThat(c.getThreadHandle().isDone()).isTrue();
            Assertions.assertThat(EventBus.getUniqueClients()).isEmpty();
            Assertions.assertThat(EventBus.getDeadEvents()).isEmpty();
            Assertions.assertThat(EventBus.getSubscribersByEventType()).isEmpty();
        }
    }

    protected  void shutdownAndAssertFinishedClients() {
        shutdownAndAssertFinishedClients(EventBus.getUniqueClients().toArray(new EventBusClient[0]));
    }

    public class LogVerifier {

        private final ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
        private final List<ILoggingEvent> logMessages = loggingEventListAppender.list;
        private final Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        public LogVerifier() {
            loggingEventListAppender.start();
            logger.addAppender(loggingEventListAppender);
            waitAtMost(Duration.ofSeconds(1)).untilAsserted(loggingEventListAppender::isStarted);
        }

        public  void verifyNeverLogged(String message) {
            assertThat(getCurrentLogStream().noneMatch(s -> s.contains(message))).isTrue();
        }

        public  void verifyLogged(String message) {
            assertThat(getCurrentLogStream().anyMatch(s -> s.contains(message))).isTrue();
        }

        public  void verifyLoggedTimes(int times, String message) {
            assertThat((int) getCurrentLogStream().filter(s -> s.contains(message)).count()).isEqualTo(times);
        }

        private  Stream<String> getCurrentLogStream() {
            return logMessages.stream().map(ILoggingEvent::getFormattedMessage);
        }

        public  void clearLogs() {
            logMessages.clear();
        }
    }
}
