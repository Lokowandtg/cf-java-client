/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.util;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.ClientV3Exception;
import org.cloudfoundry.client.v3.Resource;
//import org.cloudfoundry.client.v3.jobs.ErrorDetails;
import org.cloudfoundry.client.v3.jobs.GetJobRequest;
import org.cloudfoundry.client.v3.jobs.GetJobResponse;
//import org.cloudfoundry.client.v3.jobs.JobEntity;
//import org.cloudfoundry.client.v3.ClientV3Exception;
import org.cloudfoundry.client.v3.Error;
import org.cloudfoundry.client.v3.jobs.Job;
import org.cloudfoundry.client.v3.jobs.JobState;
import reactor.core.publisher.Mono;

/**
 * Utilities for Jobs
 */
public final class JobUtilsV3 {

    private static final Set<JobState> FINAL_STATES =
            EnumSet.of(JobState.COMPLETE, JobState.FAILED);

    private static final Integer STATUS_OK = 200;

    private JobUtilsV3() {}

    /**
     * Waits for a job to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param resource           the resource representing the job
     * @param <R>                the Job resource type
     * @return {@code onComplete} once job has completed
     */
    public static <R extends Resource> Mono<Void> waitForCompletion(
            CloudFoundryClient cloudFoundryClient, Duration completionTimeout, R resource) {
        return waitForCompletion(
                cloudFoundryClient, completionTimeout, resource);
    }

    /**
     * Waits for a job to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param jobEntity          the entity representing the job
     * @return {@code onComplete} once job has completed
     */
    public static Mono<Void> waitForCompletion(
            CloudFoundryClient cloudFoundryClient,
            Duration completionTimeout,
            Job jobEntity) {
        

        if (JobUtilsV3.isComplete(jobEntity)) {
        	Mono<Job> job;
            job = Mono.just(jobEntity);
            return job.filter(entity -> "failed".equals(entity.getState()))
                    .flatMap(JobUtilsV3::getError);
        } else {
        	Mono<GetJobResponse> job;
            job =
                    requestJobV3(cloudFoundryClient, jobEntity.getId())
  //                          .map(GetJobResponse::getState)
                            .filter(JobUtilsV3::isComplete)
                            .repeatWhenEmpty(
                                    exponentialBackOff(
                                            Duration.ofSeconds(1),
                                            Duration.ofSeconds(15),
                                            completionTimeout));
            return job.filter(entity -> "failed".equals(entity.getState()))
                    .flatMap(JobUtilsV3::getError);
        }

    }

    /**
     * Waits for a job V3 to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param jobId              the id of the job
     * @return {@code onComplete} once job has completed
     */
    public static Mono<Void> waitForCompletion(
            CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String jobId) {
        return requestJobV3(cloudFoundryClient, jobId)
                .filter(job -> FINAL_STATES.contains(job.getState()))
                .repeatWhenEmpty(
                        exponentialBackOff(
                                Duration.ofSeconds(1), Duration.ofSeconds(15), completionTimeout))
                .filter(job -> JobState.FAILED == job.getState())
                .flatMap(JobUtilsV3::getError);
    }

//    private static Mono<Void> getError(Job entity) {
//        ErrorDetails errorDetails = entity.getErrors();
//        return Mono.error(
//                new ClientV3Exception(
//                        null,
//                        errorDetails.getCode(),
//                        errorDetails.getDescription(),
//                        errorDetails.getErrorCode()));
//    }

    private static Mono<Void> getError(Job job) {
        List<Error> errors = job.getErrors();
        return Mono.error(new ClientV3Exception(STATUS_OK, errors));
    }

    private static boolean isComplete(Job entity) {
        String status = entity.getState().getValue();
        return "finished".equals(status) || "failed".equals(status);
    }

    private static boolean isComplete(GetJobResponse entity) {
        String status = entity.getState().getValue();
        return "finished".equals(status) || "failed".equals(status);
    }

//    private static Mono<GetJobResponse> requestJob(
//            CloudFoundryClient cloudFoundryClient, String jobId) {
//        return cloudFoundryClient.jobsV3().get(GetJobRequest.builder().jobId(jobId).build());
//    }

    private static Mono<GetJobResponse> requestJobV3(
            CloudFoundryClient cloudFoundryClient, String jobId) {
        return cloudFoundryClient
                .jobsV3()
                .get(GetJobRequest.builder().jobId(jobId).build());
    }
}
