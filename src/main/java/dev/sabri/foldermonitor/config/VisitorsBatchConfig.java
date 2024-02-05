package dev.sabri.foldermonitor.config;

import dev.sabri.foldermonitor.domain.Visitors;
import dev.sabri.foldermonitor.dto.VisitorsDto;
import dev.sabri.foldermonitor.mapper.VisitorsMapper;
import dev.sabri.foldermonitor.repositories.VisitorsRepository;
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
public class VisitorsBatchConfig {


    @Bean
    public Job importVistorsJob(final JobRepository jobRepository, final PlatformTransactionManager transactionManager, final VisitorsRepository visitorsRepository, final VisitorsMapper visitorsMapper) throws IOException {
        return new JobBuilder("importVisitorsJob", jobRepository)
                .start(importVisitorsStep(jobRepository, transactionManager, visitorsRepository, visitorsMapper))
                .build();
    }

    @Bean
    public Step importVisitorsStep(final JobRepository jobRepository, final PlatformTransactionManager transactionManager, final VisitorsRepository visitorsRepository, final VisitorsMapper visitorsMapper) throws IOException {
        return new StepBuilder("importVisitorsStep", jobRepository)
                .<VisitorsDto, Visitors>chunk(100, transactionManager)
                .reader(flatFileItemReader(null))
                .processor(itemProcessor(visitorsMapper))
                .writer(visitorsItemWriter(visitorsRepository))
                .build();
    }

    @Bean
    public ItemProcessor<VisitorsDto, Visitors> itemProcessor(final VisitorsMapper visitorsMapper) {
        return new VisitorsItemProcessor(visitorsMapper);
    }

    @Bean
    public VisitorsItemWriter visitorsItemWriter(final VisitorsRepository visitorsRepository) {
        return new VisitorsItemWriter(visitorsRepository);
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