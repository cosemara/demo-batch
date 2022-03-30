package com.example.batch.demo;

import com.example.batch.demo.common.utils.TestJobUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AssetsTxStatBatchTest {

    @Autowired
    private TestJobUtil testJobUtil;

    @Test
    void 자산_거래_통계_테스트() throws Exception {
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020,9,1, 0,0,0);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020,9,2, 0,0,0);
        JobParameters jobParameters = new JobParametersBuilder()
                                        .addDate("startDate", startDate.getTime())
                                        .addDate("endDate", endDate.getTime())
                                        .toJobParameters();
        JobExecution jobExecution = testJobUtil.getJobTester("assetsTxStat").launchJob(jobParameters);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}