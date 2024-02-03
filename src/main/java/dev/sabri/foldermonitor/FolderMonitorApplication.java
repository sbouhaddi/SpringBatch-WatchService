package dev.sabri.foldermonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.*;
import java.util.Calendar;

@SpringBootApplication
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class FolderMonitorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(FolderMonitorApplication.class)
                .web(WebApplicationType.SERVLET) // En cas où on ajoute un endpoint , sinon on peut mettre NONE
                .run(args)
                .registerShutdownHook();
    }

    private final JobLauncher jobLauncher;
    private final Job job;


    //@Scheduled(fixedRate = 2000)
    public void run() throws Exception {
        val jobParameters = new JobParametersBuilder()
                .addDate("timestamp", Calendar.getInstance().getTime())
                .toJobParameters();
        val jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()){
           log.info("..................");
        }
    }

    @Scheduled(fixedRate = 2000)
    public void runJob() {

        val path = Paths.get("/home/sabri/Work"); // à changer par votre répertoire
        WatchKey key;
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    log.info(
                            "Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                    if(event.kind().name().equals("ENTRY_CREATE")) {
                        // Ajouter la validation du pattern non de fichier ici
                        run();
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
