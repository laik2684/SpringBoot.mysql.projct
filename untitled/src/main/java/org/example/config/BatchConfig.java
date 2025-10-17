/*
package com.batch.springBatch.config;

import com.batch.springBatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    //@Bean
    @Bean
    public FlatFileItemReader<User> csvFileReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userReader")
                .resource(new ClassPathResource("users.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName")
                .targetType(User.class)
                .build();
    }

    */
/*  public FlatFileItemReader<User> csvFileReader(@Value("classpath:/users.csv") Resource inputFile){
        return new FlatFileItemReaderBuilder<User>()
                .name("userReader")
                .resource(inputFile)
                .delimited()
                .names("id", "firstName", "lastName")
                .targetType(User.class)
                .build();
    }*//*

    @Bean
    public ItemProcessor<User, User> userProcessor(){
        return user -> {
            System.out.println("pocessing user: " + user);
            return user;
        };
    }
    @Bean
    public FlatFileItemWriter<User> csvItemWriter() {
        FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource("output.csv"));
        writer.setAppendAllowed(false); // Overwrite file on each run

        // Optional: write CSV header
        writer.setHeaderCallback(headerWriter -> headerWriter.write("id,firstName,lastName"));

        // Configure how to write each User line
        writer.setLineAggregator(new DelimitedLineAggregator<User>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<User>() {{
                setNames(new String[] { "id,firstName,lastName" });
            }});
        }});

        return writer;
    }
    @Bean
    public MongoItemWriter<User> mongoItemWriter(MongoTemplate mongoTemplate){
        MongoItemWriter<User> writer= new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("users");
        return writer;
    }

   */
/* @Bean
    public Step csvToMongoStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<User> csvFileItemReader, ItemProcessor<User, User> userProcessor, MongoItemWriter<User> mongoItemWriter){
        return  new StepBuilder("csvToMongoStep", jobRepository)
        .<User, User> chunk(10, transactionManager)
                .reader(csvFileItemReader)
                .processor(userProcessor)
                .writer(mongoItemWriter)
                .build();
    }*//*

   @Bean
   public Step csvToMongoStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              FlatFileItemReader<User> csvFileItemReader,
                              ItemProcessor<User, User> userProcessor,
                              MongoItemWriter<User> mongoItemWriter) {
       return new StepBuilder("csvToMongoStep", jobRepository)
               .<User, User>chunk(10, transactionManager)
               .reader(csvFileItemReader)
               .processor(userProcessor)
               .writer(mongoItemWriter)
               .build();
   }
    @Bean
    public Step csvToFileStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              FlatFileItemReader<User> csvFileItemReader,
                              ItemProcessor<User, User> userProcessor,
                              FlatFileItemWriter<User> csvItemWriter) {
        return new StepBuilder("csvToFileStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(csvFileItemReader)
                .processor(userProcessor)
                .writer(csvItemWriter)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step csvToMongoStep){
        return new JobBuilder("importUserJob", jobRepository)
                .start(csvToMongoStep)
                .build();
    }
}
*/
package org.example.config;


import com.batch.springBatch.entity.User;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // Reader for Mongo step
    @Bean(name = "mongoReader")
    public FlatFileItemReader<User> mongoReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("mongoReader")
                .resource(new ClassPathResource("users.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName")
                .targetType(User.class)
                .build();
    }

    // Reader for file writer step
    @Bean(name = "fileReader")
    public FlatFileItemReader<User> fileReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("fileReader")
                .resource(new ClassPathResource("users.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName")
                .targetType(User.class)
                .build();
    }

    @Bean
    public ItemProcessor<User, User> userProcessor() {
        return user -> {
            System.out.println("Processing user: " + user);
            return user;
        };
    }

    @Bean
    public FlatFileItemWriter<User> csvItemWriter() {
        FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("target/output.csv"));
        writer.setAppendAllowed(false); // Overwrite each time

        //writer.setHeaderCallback(writer1 -> writer1.write("id","firstName","lastName"));
        writer.setHeaderCallback(writer1 -> writer1.write("id,firstName,lastName"));


        writer.setLineAggregator(new DelimitedLineAggregator<User>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<User>() {{
                setNames(new String[]{"id", "firstName", "lastName"});
            }});
        }});

        return writer;
    }

    @Bean
    public MongoItemWriter<User> mongoItemWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<User> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("users");
        return writer;
    }

    // Step to write to MongoDB
    @Bean
    public Step csvToMongoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               @Qualifier("mongoReader") FlatFileItemReader<User> reader,
                               ItemProcessor<User, User> processor,
                               MongoItemWriter<User> mongoWriter) {
        return new StepBuilder("csvToMongoStep", jobRepository)
                .<User, User>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(mongoWriter)
                .build();
    }

    // Step to write to output.csv
    @Bean
    public Step csvToFileStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              @Qualifier("fileReader") FlatFileItemReader<User> reader,
                              ItemProcessor<User, User> processor,
                              FlatFileItemWriter<User> writer) {
        return new StepBuilder("csvToFileStep", jobRepository)
                .<User, User>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // Job definition that runs both steps
    @Bean
    public Job importUserJob(JobRepository jobRepository,
                             Step csvToMongoStep,
                             Step csvToFileStep) {
        return new JobBuilder("importUserJob", jobRepository)
                .start(csvToMongoStep)
                .next(csvToFileStep)
                .build();
    }
}

