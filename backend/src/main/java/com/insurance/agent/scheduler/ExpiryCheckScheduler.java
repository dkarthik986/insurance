package com.insurance.agent.scheduler;
import com.insurance.agent.notification.NotificationService;
import com.insurance.agent.policy.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
@Slf4j @Component @RequiredArgsConstructor
public class ExpiryCheckScheduler {
    private final PolicyRepository policies; private final NotificationService notifications; private final PremiumScheduleRepository schedules;
    @Scheduled(cron="${app.scheduler.expiry-cron}",zone="${app.time-zone}")
    public void runDailyExpiryCheck(){
        log.info("Running daily expiry check");
        for(int days:new int[]{30,15,7,1}){var list=policies.expiringOn(LocalDate.now().plusDays(days));list.forEach(p->notifications.expiryReminder(p,days));log.info("Queued {} reminder(s) for {} days",list.size(),days);}
    }
    @Scheduled(cron="0 5 9 * * *",zone="${app.time-zone}")
    public void checkOverduePremiums(){var overdue=schedules.findOverdue(LocalDate.now());overdue.forEach(x->x.setStatus(com.insurance.agent.common.enums.PremiumInstalment.OVERDUE));schedules.saveAll(overdue);log.info("Marked {} premium instalment(s) overdue",overdue.size());}
}

