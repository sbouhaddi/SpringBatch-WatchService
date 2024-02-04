package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.dto.VisitorsDto;
import dev.sabri.foldermonitor.mapper.VisitorsMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class VisitorsBatchConfig {

    private final VisitorsItemWriter visitorsItemWriter;
    private final VisitorsMapper visitorsMapper;

    @Bean
    public Job importVistorsJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws IOException {
        return new JobBuilder("importVistorsJob", jobRepository)
                .start(importVistorsStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step importVistorsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws IOException {
        return new StepBuilder("importVistorsStep", jobRepository)
                .<VisitorsDto, Visitors>chunk(100, transactionManager)
                .reader(flatFileItemReader(null))
                .processor(itemProcessor())
                .writer(visitorsItemWriter)
                .build();
    }

    @Bean
    public ItemProcessor<VisitorsDto, Visitors> itemProcessor() {
        return new VisitorsItemProcessor(visitorsMapper);
    }


    @Bean
    @StepScope
    public FlatFileItemReader<VisitorsDto> flatFileItemReader(@Value("#{jobParameters['inputFile']}") final String visitorsFile) throws IOException {
        val flatFileItemReader = new FlatFileItemReader<VisitorsDto>();
        flatFileItemReader.setName("VISITORS_READER");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(linMapper());
        flatFileItemReader.setStrict(false);
        flatFileItemReader.setResource(new FileSystemResource(visitorsFile));
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<VisitorsDto> linMapper() {
        val defaultLineMapper = new DefaultLineMapper<VisitorsDto>();
        val lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("id", "firstName", "lastName", "emailAddress", "phoneNumber", "address", "visitDate");
        lineTokenizer.setStrict(false); // Set strict property to false
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(new VisitorsFieldSetMapper());
        return defaultLineMapper;

    }

}