package dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JobList {

	private List<Job> jobs;
	private float time;
	
	public JobList(List<Job> jobs, float time) {
		this.jobs = jobs;
		this.time = time;
	}
	
	/**
	 * Returns a new JobList, with all of the jobs sorted according to their due date and processing time
	 * @return A new sorted JobList
	 */
	public JobList getSortedJobs(){
		Collections.sort(jobs);
		List<Job> jobsFinal = new ArrayList<Job>(jobs.size());
		for (int i = 0; i < jobs.size(); i++) {
			Job j = jobs.get(i);
			jobsFinal.add(new Job(i, j.getProcessingTime(), j.getDueTime(), j.getWeight()));
		}
		return new JobList(jobsFinal, time);
	}
	
	public List<Job> getJobs() {
		return this.jobs;
	}
	
	public Job getJob(int index) {
		if (index >= 0 && index < jobs.size()) {
			return jobs.get(index);
		}
		else {
			return null;
		}
	}
	
	public int size() {
		return this.jobs.size();
	}
	
	/**
	 * Find the job with the longest processing time
	 * @return The job with the longest processing time
	 */
	public Job getLongestProcessingJob() {
		float max = Integer.MIN_VALUE;
		Job j = null;
		//for (Job job : this.jobs) {
		for (int i = this.jobs.size() -1; i >= 0; i--) {
			Job job = this.jobs.get(i);
			if (max < job.getProcessingTime()) {
				max = job.getProcessingTime();
				j = job;
			}
		}
		return j;
	}
	
	public float getTime() {
		return this.time;
	}
	
	/**
	 * Sets the time from which this set of jobs has to find an optimal schedule 
	 * @param time
	 */
	public void setTime(float time) {
		this.time = time;
	}
	
	/**
	 * Get a subset from this set of jobs. Find all jobs with their index in [i,j] and a processing time smaller or equal to that
	 * of jobK, but does not include jobK.
	 * @param i The lower index
	 * @param j The upper index
	 * @param jobK The job for which we want to compare processing times
	 * @return A new JobList containing the specified subset
	 */
	public JobList getSubset(int i, int j, Job jobK) {
		List<Job> jobs = new ArrayList<Job>(Math.max(0, j-i+1));
		for (Job job : this.jobs) {
			if (job.getIndex() >= i && job.getIndex() <= j 
					&& job.getProcessingTime() <= jobK.getProcessingTime()
					&& job.getIndex() != jobK.getIndex()) {
				jobs.add(job);
			}
		}
		return new JobList(jobs, 0);
	}
	
	/**
	 * Get a subset of jobs for which their due time is smaller then or equal to dTime.
	 * @param dTime The value to compare due time to
	 * @return A new JobList with the specified set
	 */
	public JobList getSubsetDelta(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		Iterator<Job> itt = this.jobs.iterator();
		Job job = null;
		while (itt.hasNext() && (job = itt.next()) != null && job.getDueTime() <= dTime) {
			jobs.add(job);
		}
		return new JobList(jobs, 0);
	}
	
	/**
	 * Get a subset of jobs for which their due time is greater then dTime.
	 * @param dTime The value to compare due time to
	 * @return A new JobList with the specified set
	 */
	public JobList getSubsetDeltaInverse(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		ListIterator<Job> itt = this.jobs.listIterator(this.jobs.size());
		Job job = null;
		while (itt.hasPrevious() && (job = itt.previous()) != null && job.getDueTime() > dTime) {
			jobs.add(job);
		}
		return new JobList(jobs, 0);
	}
	
	/**
	 * Get the total processing time needed to complete all jobs in this set.
	 * @return The total processing time
	 */
	public float getCompletionTime() {
		float sum = 0;
		for (Job job : this.jobs) {
			sum += job.getProcessingTime();
		}
		return sum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { 
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		JobList input = (JobList) obj;
		if (this.time != input.time) {
			return false;
		}
		if (this.jobs.size() != input.jobs.size()) {
			return false;
		}
		for (int i = 0; i < this.jobs.size(); i++) {
			if (this.jobs.get(i).getIndex() != input.jobs.get(i).getIndex()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.jobs.hashCode();
	}
	
	@Override
	public String toString() {
		String res = "";
		if (this.jobs.size() != 0) {
			for (Job job : this.jobs) {
				res += job.getIndex() + ", ";
			}
			res = res.substring(0, res.length()-2);
		}
		return "Time: " + this.time + "; Jobs: " + res;
	}
}
