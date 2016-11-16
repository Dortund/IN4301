package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class JobList {

	private List<Job> jobs;
	private int time;
	
	public JobList(List<Job> jobs, int time) {
		this.jobs = jobs;
		this.time = time;
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
	
	public int getLongestProcessingTimeIndex() {
		float max = 0;
		int index = -1;
		for (Job job : this.jobs) {
			if (max < job.getProcessingTime()) {
				max = job.getProcessingTime();
				index = job.getIndex();
			}
		}
		return index;
	}
	
	public int getMaximumCompletionTime() {
		return (int) Math.ceil(jobs.size()*this.getLongestProcessingTime());
	}
	
	/*public int getIntegerRepresentation() {
		int res = 0;
		for (Job job : this.jobs) {
			res += Math.pow(2, job.getIndex());
		}
		return res;
	}*/
	
	public int getTime() {
		return this.time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public JobList getSubset(int i, int j, float pTime) {
		List<Job> jobs = new ArrayList<Job>();
		for (Job job : this.jobs) {
			if (job.getIndex() >= i && job.getIndex() <= j && job.getProcessingTime() < pTime) {
				jobs.add(job);
			}
		}
		return new JobList(jobs, 0);
	}
	
	public JobList getSubsetDelta(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		for (Job job : this.jobs) {
			if (job.getDueTime() < dTime) {
				jobs.add(job);
			}
		}
		return new JobList(jobs, 0);
	}
	
	public JobList getSubsetDeltaInverse(float dTime) {
		List<Job> jobs = new ArrayList<Job>();
		for (Job job : this.jobs) {
			if (job.getDueTime() > dTime) {
				jobs.add(job);
			}
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
}
