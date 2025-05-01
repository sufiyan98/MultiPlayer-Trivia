package com.hackathon.adminservice.config;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
 import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.hackathon.adminservice.entity.Question;
import com.hackathon.adminservice.repo.AdminRepository;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {


    @Autowired
    private AdminRepository adminRepository;
 

    @Bean
    @StepScope
    public FlatFileItemReader<Question> reader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        FlatFileItemReader<Question> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(new File(pathToFile)));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Question> lineMapper() {
        DefaultLineMapper<Question> linemapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("category", "correct_answer", "level", "opt1", "opt2", "opt3", "opt4", "question_description");
        BeanWrapperFieldSetMapper<Question> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Question.class);
        linemapper.setLineTokenizer(lineTokenizer);
        linemapper.setFieldSetMapper(fieldSetMapper);
        return linemapper;
    }

    @Bean
    public RepositoryItemWriter<Question> writer() {
        RepositoryItemWriter<Question> writer = new RepositoryItemWriter<>();
        writer.setRepository(adminRepository);
        writer.setMethodName("save");
        return writer;
    }
 
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public Step myStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,FlatFileItemReader<Question> reader) {
        return new StepBuilder("myStep", jobRepository)
        		.<Question, Question>chunk(10, transactionManager)
         		.reader(reader)
                .writer(writer())
                 .build();
    }
    

    @Bean
    public Job myJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("myJob", jobRepository)
                .start(step)
                .build();
    }
    
 
}
