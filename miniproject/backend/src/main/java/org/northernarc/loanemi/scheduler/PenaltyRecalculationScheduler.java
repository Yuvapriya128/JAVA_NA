package org.northernarc.loanemi.scheduler;

import java.time.LocalDate;
import org.northernarc.loanemi.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PenaltyRecalculationScheduler {
    private static final Logger log = LoggerFactory.getLogger(PenaltyRecalculationScheduler.class);
    private final LoanService loanService;

    public PenaltyRecalculationScheduler(LoanService loanService) {
        this.loanService = loanService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyPenaltyRecalculation() {
        LocalDate currentDate = LocalDate.now();
        log.info("Running daily overdue and penalty recalculation for date={}", currentDate);
        loanService.recalculateOverduePenalties(currentDate);
    }
}
