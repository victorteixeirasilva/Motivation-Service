package tech.inovasoft.inevolving.ms.motivation.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.AgendamentoInfoDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseAgendamentosDTO;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@EnableScheduling
@Configuration
public class SchedulingConfig {

    @Autowired
    private MotivationService motivationService;

    private ScheduledExecutorService executor;
    private final ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private final Runnable sendEmailRunnable = this::sendEmailMotivacionalForUsers;

    private final List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
    private final List<ZonedDateTime> currentScheduledTimes = new ArrayList<>();

    @Scheduled(cron = "0 0 8 1 * *", zone = "America/Sao_Paulo")
    public void sendEmailForUsersWithLateTasks() {
        motivationService.sendEmailForUsersWithLateTasks();
    }

    @Scheduled(cron = "0 0 6 * * *", zone = "America/Sao_Paulo")
    public void sendEmailForUsersDisconnected() {
        motivationService.sendEmailForUsersDisconnected();
    }

    @Scheduled(cron = "0 50 23 * * *", zone = "America/Sao_Paulo")
    public void postponeTasksForAllUsers() {
        motivationService.postponeTasksForAllUsers();
    }

    public void sendEmailMotivacionalForUsers() {
        motivationService.sendEmailForUsersDisconnected();
    }

    @PostConstruct
    public void initScheduler() {
        executor = Executors.newScheduledThreadPool(5);
        scheduleTodaysEmails();
        scheduleDailyRefresh();
    }

    public synchronized ResponseAgendamentosDTO forcarReagendamento() {
        scheduledFutures.forEach(f -> f.cancel(false));
        scheduledFutures.clear();
        currentScheduledTimes.clear();

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime windowEnd = now.toLocalDate().atTime(19, 0).atZone(zoneId);

        LocalDate targetDate = now.isAfter(windowEnd) ? now.toLocalDate().plusDays(1) : now.toLocalDate();
        ZonedDateTime windowStart = targetDate.atTime(7, 0).atZone(zoneId);
        ZonedDateTime effectiveAfter = now.isAfter(windowEnd) ? windowStart : now;

        List<ZonedDateTime> times = generateRandomTimes(targetDate, effectiveAfter);
        long nowMs = now.toInstant().toEpochMilli();

        for (ZonedDateTime time : times) {
            long delay = time.toInstant().toEpochMilli() - nowMs;
            if (delay > 0) {
                ScheduledFuture<?> future = executor.schedule(sendEmailRunnable, delay, TimeUnit.MILLISECONDS);
                scheduledFutures.add(future);
                currentScheduledTimes.add(time);
            }
        }

        return buildResponseAgendamentos();
    }

    private synchronized void scheduleTodaysEmails() {
        scheduledFutures.forEach(f -> f.cancel(false));
        scheduledFutures.clear();
        currentScheduledTimes.clear();

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        List<ZonedDateTime> times = generateRandomTimes(today, now);
        long nowMs = now.toInstant().toEpochMilli();

        for (ZonedDateTime time : times) {
            long delay = time.toInstant().toEpochMilli() - nowMs;
            if (delay > 0) {
                ScheduledFuture<?> future = executor.schedule(sendEmailRunnable, delay, TimeUnit.MILLISECONDS);
                scheduledFutures.add(future);
                currentScheduledTimes.add(time);
            }
        }
    }

    private ResponseAgendamentosDTO buildResponseAgendamentos() {
        List<AgendamentoInfoDTO> agendamentos = new ArrayList<>();
        for (ZonedDateTime time : currentScheduledTimes) {
            String cron = String.format("0 %d %d * * ?", time.getMinute(), time.getHour());
            String proximaExecucao = time.truncatedTo(ChronoUnit.SECONDS).format(formatter);
            agendamentos.add(new AgendamentoInfoDTO(cron, proximaExecucao));
        }
        int ativos = (int) scheduledFutures.stream().filter(f -> !f.isDone()).count();
        return new ResponseAgendamentosDTO(currentScheduledTimes.size(), agendamentos, ativos);
    }

    private void scheduleDailyRefresh() {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.with(LocalTime.of(6, 0));
        if (nextRun.isBefore(now) || nextRun.equals(now)) {
            nextRun = nextRun.plusDays(1);
        }
        long initialDelayMs = Duration.between(now, nextRun).toMillis();
        executor.scheduleAtFixedRate(this::scheduleTodaysEmails,
                initialDelayMs, 24L * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private List<ZonedDateTime> generateRandomTimes(LocalDate date, ZonedDateTime afterTime) {
        List<ZonedDateTime> times = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ZonedDateTime windowStart = date.atTime(7, 0).atZone(zoneId);
        ZonedDateTime windowEnd = date.atTime(19, 0).atZone(zoneId);
        ZonedDateTime effectiveStart = windowStart.isAfter(afterTime) ? windowStart : afterTime;
        if (effectiveStart.isAfter(windowEnd)) {
            return times;
        }
        long startMs = effectiveStart.toInstant().toEpochMilli();
        long endMs = windowEnd.toInstant().toEpochMilli();
        long durationMs = endMs - startMs;
        for (int i = 0; i < 10; i++) {
            double randFraction = random.nextDouble();
            long randomDelayMs = (long) (randFraction * durationMs);
            ZonedDateTime randomTime = effectiveStart.plus(randomDelayMs, ChronoUnit.MILLIS);
            times.add(randomTime);
        }
        Collections.sort(times);
        return times;
    }

    @PreDestroy
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();
            }
        }
    }

}
