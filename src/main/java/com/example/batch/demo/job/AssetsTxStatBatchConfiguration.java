package com.example.batch.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AssetsTxStatBatchConfiguration {

    public static final String JOB_NAME = "assetsTxStat";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean("assetsTxStat")
    public Job assetsTxStatJob() {
        return jobBuilderFactory.get("assetsTxStat")
                .preventRestart()               // JOB 재실행 방지
                //.listener(testJobListener)
                .start(assetsTxStatStep(null))
                .build();
    }

    @Bean(BEAN_PREFIX + "Step")
    @JobScope
    public Step assetsTxStatStep(@Value("#{jobParameters[baseDate]}") String baseDate) {
        return stepBuilderFactory.get(BEAN_PREFIX + "Step")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Assets Tx statistics start");
                    return assetsTxStatProcess(baseDate);
                }).build();
    }

    public RepeatStatus assetsTxStatProcess(String baseDate) {
        LocalDate startDt = LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime start = LocalDateTime.of(startDt, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(startDt.plusDays(1), LocalTime.MIN);

        StoredProcedureQuery storedProcedureQuery = entityManagerFactory.createEntityManager()
                                                    .createStoredProcedureQuery("assets_tx_stat_procedure")
                                                    .registerStoredProcedureParameter("START_DATE", LocalDateTime.class, ParameterMode.IN)
                                                    .registerStoredProcedureParameter("END_DATE", LocalDateTime.class, ParameterMode.IN);

        storedProcedureQuery.setParameter("START_DATE", start);
        storedProcedureQuery.setParameter("END_DATE", end);
        storedProcedureQuery.execute();

        int returnValue = (int) storedProcedureQuery.getSingleResult();
        log.debug("returnValue: {}", returnValue);

        return RepeatStatus.FINISHED;
    }
}
