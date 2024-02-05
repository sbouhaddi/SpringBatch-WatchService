package dev.sabri.foldermonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.file.*;
import java.util.Calendar;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class FolderMonitorApplication {

    private final JobLauncher jobLauncher;
    private final Job job;

    public FolderMonitorApplication(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(FolderMonitorApplication.class)
                .web(WebApplicationType.SERVLET) // En cas où on ajoute un endpoint , sinon on peut mettre NONE
                .run(args)
                .registerShutdownHook();
    }

    public void run(final String inputFile) throws Exception {
        val jobParameters = new JobParametersBuilder()
                .addDate("timestamp", Calendar.getInstance().getTime())
                .addString("inputFile", inputFile)
                .toJobParameters();
        val jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()) {
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
                    if (event.kind().name().equals("ENTRY_CREATE")) {
                        // Ajouter la validation du pattern non de fichier ici
                        run(path + "/" + event.context().toString());
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
