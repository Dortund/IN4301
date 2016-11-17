package algorithms;

import java.util.ArrayList;
import java.util.List;

import sampleCode.Schedule;
import dataStructures.Job;
import dataStructures.JobList;
import algorithms.ExactAlgorithm;

public class ApproxAlgorithm {
	
	private JobList original_jobs;
	private JobList scaled_jobs;
	private float k = 1;
	
	public ApproxAlgorithm(JobList jobs)
	{
		original_jobs = jobs.getSortedJobs();
	}
	
	public float solve(float epsilon){
		//first, check if the EDD schedule achieves 0 tardiness
		float t = getTMax();
		System.out.println("Tmax = " + t);
		if (t == 0)
			return t;
		//If not, determine K by the formula given by (Lawler 1982)
		int n = original_jobs.size();
		k = 2*epsilon*t/(float)(n*(n+1));
		System.out.println("k = " + k);
		//scale all processing times and due dates by 1/K, rounding down only the processing times
		scaled_jobs = new JobList(scaleJobs(), 0);
		//execute the exact algorithm on the scaled jobs
		ExactAlgorithm eA = new ExactAlgorithm(scaled_jobs);
		Schedule schedule = eA.solve();
		
		//return the tardiness in the orignal scale
		schedule = rescale(schedule);
		return schedule.getTardiness();
	}
	
	private Schedule rescale(Schedule scaled){
		int size = scaled.getDepth();
		scaled = scaled.getFirst();
		Schedule unscaled = null;
		for(int i = 0; i < size; i++){
			Job scaledJob = scaled.getJob();
			int id = scaledJob.getIndex();
			unscaled = new Schedule(unscaled, original_jobs.getJob(id));
			scaled = scaled.next();
		}
		return unscaled;
	}
	
	private float getTMax(){
		float max = 0;
		int time = 0;
		for (int i = 0; i < original_jobs.size(); i++){
			time += original_jobs.getJob(i).getProcessingTime();
			max = Math.max(max, time-original_jobs.getJob(i).getDueTime());
		}
		return max;
	}
	
	private List<Job> scaleJobs(){
		List<Job> scaledJobs = new ArrayList<Job>(original_jobs.size());
		for (int i = 0; i < original_jobs.size(); i++) {
			Job j = original_jobs.getJob(i);
			scaledJobs.add(new Job(i, (float)Math.floor(j.getProcessingTime()/k), j.getDueTime()/k, j.getWeight()));
		}
		return scaledJobs;
	}

	public float getK() {
		return k;
	}

}
