package org.example.controller;


/*

@RestController
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;
    */
/*@Autowired
    public JobController(JobLauncher jobLauncher, Job importUserJob) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
    }*//*

    @Autowired
    public JobController(JobLauncher jobLauncher, @Qualifier("importUserJob") Job importUserJob) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
    }
    @Autowired
    FlatFileItemReader<User> reader;


    @PostMapping("/test-reader")
    public String testReader() throws Exception {
        reader.open(new ExecutionContext());
        User user;
        while ((user = reader.read()) != null) {
            System.out.println("Read user: " + user);
        }
        reader.close();
        return "Done reading";
    }


    @PostMapping("/run-import-job")
    public String runJob(){
        try{
            JobParameters jobParameters=new JobParametersBuilder()
                    .addDate("runTime", new Date(), true)
                    .toJobParameters();
            jobLauncher.run(importUserJob,jobParameters);
            return "Batch job importUserJob startd successfully";
        }catch (Exception e){
            e.printStackTrace();
            return "Error starting" + e.getMessage();
        }
    }
}
*/
/*

@RestController
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;

    @Autowired
    public JobController(JobLauncher jobLauncher, @Qualifier("importUserJob") Job importUserJob) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
    }

    @Autowired
    @Qualifier("fileReader")
    private FlatFileItemReader<User> reader;

    @PostMapping("/test-reader")
    public String testReader() throws Exception {
        reader.open(new ExecutionContext());
        User user;
        while ((user = reader.read()) != null) {
            System.out.println("Read user: " + user);
        }
        reader.close();
        return "Done reading";
    }

    @PostMapping("/run-import-job")
    public String runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("runTime", new Date(), true)
                    .toJobParameters();
            jobLauncher.run(importUserJob, jobParameters);
            return "Batch job importUserJob started successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error starting: " + e.getMessage();
        }
    }
}

*/

import com.batch.springBatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;

    @Autowired
    public JobController(JobLauncher jobLauncher, @Qualifier("importUserJob") Job importUserJob) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
    }

    @Autowired
    @Qualifier("fileReader")  // or whichever reader bean you want
    FlatFileItemReader<User> reader;

    @Autowired
    @Qualifier("csvItemWriter")
    FlatFileItemWriter<User> writer;

    @PostMapping("/test-reader")
    public String testReader() throws Exception {
        reader.open(new ExecutionContext());
        User user;
        while ((user = reader.read()) != null) {
            System.out.println("Read user: " + user);
        }
        reader.close();
        return "Done reading";
    }
    @PostMapping("/test-writer")
    public String testWriter() throws Exception {
        reader.open(new ExecutionContext());
        writer.open(new ExecutionContext());

        User user;
        List<User> usersBatch = new ArrayList<>();

        int chunkSize = 2; // write in chunks of 10
        while ((user = reader.read()) != null) {
            usersBatch.add(user);
            if (usersBatch.size() == chunkSize) {
                Chunk<User> chunk = new Chunk<>(usersBatch);
                writer.write(chunk);
                usersBatch.clear();
            }
        }

        // write remaining users if any
        if (!usersBatch.isEmpty()) {
            Chunk<User> chunk = new Chunk<>(usersBatch);
            writer.write(chunk);
        }

        reader.close();
        writer.close();

        return "Data read from input and written to output.csv successfully!";
    }

/*
    @PostMapping("/test-writer")
    public String testWriter() throws Exception {
        *//*List<User> users = Arrays.asList(
                new User(1, "John", "Doe"),
                new User(2, "Jane", "Smith")
        );*//*

        writer.open(new ExecutionContext());
        for (User user : users) {
            Chunk<User> chunk = new Chunk<>(Collections.singletonList(user));
            writer.write(chunk);
        }
        writer.close();

        return "Users written to CSV";
    }*/

    @PostMapping("/run-import-job")
    public String runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("runTime", new Date(), true)
                    .toJobParameters();
            jobLauncher.run(importUserJob, jobParameters);
            return "Batch job importUserJob started successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error starting job: " + e.getMessage();
        }
    }
}

