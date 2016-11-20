package dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JobList {

	protected List<Job> jobs;
	protected float time;
	
	public JobList(List<Job> jobs, float time) {
		this.jobs = jobs;
		this.time = time;
	}
	
	public JobList(JobList joblist) {
		this.jobs = joblist.getJobs();
		this.time = joblist.getTime();
	}

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
	
	public float getLongestProcessingTime() {
		float max = 0;
		for (Job job : this.jobs) {
			max = Math.max(max, job.getProcessingTime());
		}
		return max;
	}
	
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
	
	public float getMaximumCompletionTime() {
		return jobs.size()*this.getLongestProcessingTime();
	}
	
	public float getTime() {
		return this.time;
	}
	
	public void setTime(float time) {
		this.time = time;
	}
	
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
	
	public JobList getSubsetDelta(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		Iterator<Job> itt = this.jobs.iterator();
		Job job = null;
		while (itt.hasNext() && (job = itt.next()) != null && job.getDueTime() <= dTime) {
			jobs.add(job);
		}
		return new JobList(jobs, 0);
	}
	
	public JobList getSubsetDeltaInverse(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		ListIterator<Job> itt = this.jobs.listIterator(this.jobs.size());
		Job job = null;
		while (itt.hasPrevious() && (job = itt.previous()) != null && job.getDueTime() > dTime) {
			jobs.add(job);
		}
		return new JobList(jobs, 0);
	}
	
	public int getCompletionTime() {
		int sum = 0;
		for (Job job : this.jobs) {
			sum += job.getProcessingTime();
		}
		return sum;
	}
	
	public boolean sanityCheck() {
		float due = 0;
		for (Job job : this.jobs) {
			if (job.getDueTime()< due)
				return false;
			else
				due = job.getDueTime();
		}
		return true;
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
