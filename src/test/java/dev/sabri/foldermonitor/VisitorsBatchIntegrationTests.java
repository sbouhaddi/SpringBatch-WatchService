package dev.sabri.foldermonitor;


import dev.sabri.foldermonitor.config.VisitorsBatchConfig;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBatchTest
@SpringJUnitConfig(classes = {VisitorsBatchConfig.class, VisitorsBatchTestConfig.class})
class VisitorsBatchIntegrationTests {

    public static final String INPUt_FILE = "visitors.csv";
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    private JobParameters defaultJobParameters() {
        val paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("inputFile", INPUt_FILE);
        paramsBuilder.addDate("timestamp", Calendar.getInstance().getTime());
        return paramsBuilder.toJobParameters();
    }


    @Test
    void givenVisitorsFlatFile_whenJobExecuted_thenSuccess(@Autowired Job job) throws Exception {
        // given
        this.jobLauncherTestUtils.setJob(job);
        // when
        val jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        val actualJobInstance = jobExecution.getJobInstance();
        val actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo("importVisitorsJob");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    }

}
