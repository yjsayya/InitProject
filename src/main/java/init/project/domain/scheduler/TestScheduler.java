package init.project.domain.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestScheduler {

    @PostConstruct
    public void init() {
        log.info("INIT...");
    }

    @PreDestroy
    public void destroy() {
        log.info("DESTROY...");
    }

//    @Scheduled(fixedRate = 5_000)
    public void scheduleJob() {
        try {
            log.info("SCHEDULING...");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}