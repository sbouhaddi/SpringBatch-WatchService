package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.repositories.VisitorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class VisitorsBatchConfig {


    private final VisitorsRepository visitorsRepository;
    private final DbWriter dbWriter;

    @Bean
    public Job importVistorsJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws IOException {
        return new JobBuilder("importVistorsJob", jobRepository)
                .start(importVistorsStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step importVistorsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws IOException {
        return new StepBuilder("importVistorsStep", jobRepository)
                .<Visitors, Visitors>chunk(100, transactionManager)
                .reader(multiResourceItemReader())
                .processor(itemProcessor())
                .writer(dbWriter)
                .build();
    }

    @Bean
    public ItemProcessor<Visitors, Visitors> itemProcessor() {
        return new VisitorsItemProcessor();
    }


    @Bean
    public ItemReader<Visitors> multiResourceItemReader() throws IOException {
        val reader = new MultiResourceItemReader<Visitors>();
        val resources = new PathMatchingResourcePatternResolver().getResources("file:/home/sabri/Work/*.csv"); // à changer par votre répertoire
        reader.setResources(resources);
        reader.setDelegate(flatFileItemReader());
        return reader;
    }

    @Bean
    public FlatFileItemReader<Visitors> flatFileItemReader() {
        val flatFileItemReader = new FlatFileItemReader<Visitors>();
        flatFileItemReader.setName("VISITORS_READER");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(linMapper());
        flatFileItemReader.setStrict(false);
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<Visitors> linMapper() {
        val defaultLineMapper = new DefaultLineMapper<Visitors>();
        val lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("id", "firstName", "lastName", "emailAddress", "phoneNumber", "address", "strVisitDate");
        lineTokenizer.setStrict(false); // Set strict property to false
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        val fieldSetMapper = new BeanWrapperFieldSetMapper<Visitors>();
        fieldSetMapper.setTargetType(Visitors.class);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;

    }

}