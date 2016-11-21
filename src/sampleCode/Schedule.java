package sampleCode;

import dataStructures.Job;

/**
 * 
 * @author IN4301
 * @author Wouter Groen
 * Basic code provided by the course on Blackboard. Updates done by Wouter Groen.
 */

public class Schedule implements Comparable<Schedule> {
	// A linked-list is a relatively efficient representation of a schedule
	// Feel free to modify it if you feel there exists a better one
	// The main advantage is that in a search-tree there is a lot of overlap
	// between schedules, this implementation stores this overlap only once
	private Schedule previous;
	private Schedule next;
	private Job job;
	
	// tardiness can be calculated instead of memorized
	// however, we need to calculate it a lot, so we memorize it
	// if memory is an issue, however, try calculating it
	private float tardiness;
	private float startTime;
	
	public Schedule(){
		this.previous = null;
		this.job = null;
		this.tardiness = 0;
		this.startTime = 0;
	}
	
	/**
	 * Add a new job to the end of the schedule
	 * @param s The preceding schedule
	 * @param jobID The job id
	 * @param jobLength The processing length
	 * @param jobDueTime The due time of the job
	 */
	public Schedule(Schedule s, int jobID, int jobLength, int jobDueTime){		
		this.job = new Job(jobID, jobLength, jobDueTime, 1);
		
		this.previous = s;
		if (this.previous != null) {
			this.previous.next = this;
		}
		
		this.startTime = 0;
		if (this.previous != null) {
			this.startTime = previous.getTotalTime();
		}
		
		this.tardiness = Math.max(0, this.getTotalTime() - this.job.getDueTime());
		if (this.previous != null) {
			this.tardiness += previous.getTardiness();
		}
	}
	
	/**
	 * Add a new job to the end of the schedule
	 * @param s The preceding schedule
	 * @param job The job to add
	 */
	public Schedule(Schedule s, Job job) {
		this.job = job;
		
		this.previous = s;
		if (this.previous != null) {
			this.previous.next = this;
		}
		
		this.startTime = 0;
		if (this.previous != null) {
			this.startTime = previous.getTotalTime();
		}
		
		this.tardiness = Math.max(0, this.getTotalTime() - this.job.getDueTime());
		if (this.previous != null) {
			this.tardiness += previous.getTardiness();
		}
	}
	
	// used by the best-first search
	// currently, schedules are traversed in smallest total tardiness order
	public int compareTo(Schedule o){
		return (int) (getTardiness() - o.getTardiness());
		
		// replace with the following to get a depth-first search
		// return get_depth() - o.get_depth();
	}
	
	public int getDepth(){
		int depth = 1;
		if(previous != null) depth += previous.getDepth();
		return depth;
	}
	
	/**
	 * Returns the total amount of processing time needed to complete all the jobs.
	 * @return The summation of processing times
	 */
	public float getTotalTime(){
		/*float time = job.getProcessingTime();
		if(previous != null)
			time += previous.getTotalTime();
		else
			time += this.startTime;
		return time;*/
		return this.startTime + this.job.getProcessingTime();
	}
	
	/**
	 * Get the total tardiness of this and the previous jobs
	 * @return The total tardiness of this schedule
	 */
	public float getTardiness(){
		return tardiness;
	}
	
	/**
	 * Updates the start time for itself and all schedules before it.
	 * @param time The new start time of the schedule
	 */
	public void updateStartTime(float time) {
		if (this.previous != null) {
			this.previous.updateStartTime(time);
			this.startTime = this.previous.getTotalTime();
			this.tardiness = this.previous.getTardiness() + Math.max(0, this.getTotalTime() - this.job.getDueTime());
		}
		else {
			this.startTime = time;
			this.tardiness = Math.max(0, this.getTotalTime() - this.job.getDueTime()); 
		}
	}
	
	public boolean containsJob(int job){
		return (this.job.getIndex() == job) || (previous != null && previous.containsJob(job));
	}
	
	public Job getJob() {
		return this.job;
	}
	
	/**
	 * When used by the ExactAlgorithm, the schedule is expected to be a double linked list. So we can request the next scheduled job.
	 * @return The scheduled job after this one
	 */
	public Schedule next() {
		return this.next;
	}
	
	/**
	 * Get the first scheduled job
	 * @return
	 */
	public Schedule getFirst() {
		if (this.previous == null)
			return this;
		else
			return this.previous.getFirst();
	}
	
	public String toString() {
		String res = "";
		if (this.previous != null)
			res += this.previous.toString();
		res += "(" + this.job.getIndex() + "," + this.job.getProcessingTime() + "),";
		return res;
	}
}
