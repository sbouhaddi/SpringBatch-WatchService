package dev.sabri.foldermonitor;


import dev.sabri.foldermonitor.config.VisitorsBatchConfig;
import dev.sabri.foldermonitor.repositories.VisitorsRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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

    public static final String INPUt_FILE = "src/test/resources/visitors.csv";
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
    void givenVisitorsFlatFile_whenJobExecuted_thenSuccess(@Autowired VisitorsRepository visitorsRepository) throws Exception {
        // when
        val jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        val actualJobInstance = jobExecution.getJobInstance();
        val actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo("importVisitorsJob");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
        assertThat(visitorsRepository.findAll()).hasSize(5);

    }

}
