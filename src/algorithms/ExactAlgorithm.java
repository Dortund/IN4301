package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.Job;
import dataStructures.JobList;
import sampleCode.Schedule;

public class ExactAlgorithm {
	
	private Map<Float, Map<JobList, Schedule>> cache;
	private JobList jobs;
	
	public ExactAlgorithm(JobList jobs) {
		this.jobs = jobs;
		cache = new HashMap<Float, Map<JobList, Schedule>>();
	}
	
	/**
	 * Returns one of the possible optimal schedules for the problem instance.
	 * @return The optimal schedule
	 */
	public Schedule solve() {
		Schedule schedule = this.solve(this.jobs, 0, this.jobs.size()-1);
		return schedule;
	}

	/**
	 * The recursive part of our algorithm.
	 * @param jobsIn Current subset of jobs
	 * @param i lower Index of the subset
	 * @param j higher Index of the subset
	 * @return Optimal schedule for the given subset of jobs
	 */
	private Schedule solve(JobList jobsIn, int i, int j) {

		// If empty, return null
		if (jobsIn.size() == 0) {
			return null;
		}
		
		// If there is one job, the optimal schedule is trival
		if (jobsIn.size() == 1) {
			Schedule schedule = new Schedule(null, jobsIn.getJob(0));
			schedule.updateStartTime(jobsIn.getTime());
			return schedule;
		}
		
		// Try and find the given subset of jobs for the current starting time in our cache.
		// We do not call 'containsKey(...)' to reduce running time
		Map<JobList, Schedule> jobLists = this.cache.get(jobsIn.getTime());
		if (jobLists != null) {
			Schedule cachedSchedule = jobLists.get(jobsIn);
			if (cachedSchedule != null) {
				return cachedSchedule;
			}
		}
		
		// Get the job with the longest running time in this subset
		Job jobK = jobsIn.getLongestProcessingJob();
		
		float res = Float.MAX_VALUE;
		Schedule opt1 = null;
		Schedule opt2 = null;
		
		// Calculate optimal values for delta 
		List<Integer> deltas = getDeltas(jobsIn);
		
		// Check every subproblem for each delta
		for (Integer d : deltas) {
			// Get the lower subset
			JobList j1= jobsIn.getSubset(i, jobK.getIndex() + d, jobK);
			j1.setTime(jobsIn.getTime());
			
			// Get the upper subset
			JobList j2 = jobsIn.getSubset(jobK.getIndex() + d + 1, j, jobK);
			j2.setTime(jobsIn.getTime()+j1.getCompletionTime()+jobK.getProcessingTime());

			// Get optimal schedule for subset
			Schedule l1 = this.solve(j1, i, jobK.getIndex() + d);
			Schedule l2 = this.solve(j2, jobK.getIndex() + d + 1, j);
			
			// Calculate tardiness
			float r1 = 0;
			float r2 = 0;
			if (l1 != null) {
				r1 = l1.getTardiness();
			}
			if (l2 != null) {
				r2 = l2.getTardiness();
			}
			float kVal = jobK.getWeight()*Math.max(0, jobsIn.getTime() + j1.getCompletionTime() + jobK.getProcessingTime() - jobK.getDueTime());
			
			// Check if better then current schedules
			float tardiness = r1 + kVal + r2;
			if (tardiness < res) {
				opt1 = l1;
				opt2 = l2;
				res = tardiness;
			}
			if (res == 0) {
				break;
			}
		}
		
		// Construct the optimal schedule from the sub-subsets
		Schedule fin = null;
		
		if (opt1 != null) {
			Schedule s = opt1.getFirst();
			while (s != null) {
				fin = new Schedule(fin, s.getJob());
				s = s.next();
			}
		}
		
		fin = new Schedule(fin, jobK);
		
		if (opt2 != null) {
			Schedule s = opt2.getFirst();
			while (s != null) {
				fin = new Schedule(fin, s.getJob());
				s = s.next();
			}
		}
		
		fin.updateStartTime(jobsIn.getTime());
		
		// Store the optimal schedule for this subset of jobs and start time
		Map<JobList, Schedule> c = this.cache.get(jobsIn.getTime());
		if (c == null) {
			c = new HashMap<JobList, Schedule>();
			c.put(jobsIn, fin);
			this.cache.put(jobsIn.getTime(), c);
		}
		else {
			c.put(jobsIn, fin);
		}
		
		// Return optimal schedule
		return fin;
	}
	
	/**
	 * Get the delta values d 0 >= d < n - k, for which the algorithm could find a possible optimal schedule 
	 * @param jobsIn The subset of jobs for which to find the deltas
	 * @return A list of integer values, representing the deltas
	 */
	private List<Integer> getDeltas(JobList jobsIn) {
		List<Integer> deltas = new ArrayList<Integer>();
		Job jobK = jobsIn.getLongestProcessingJob();

		float dueTime = jobK.getDueTime();
		
		while (true) {
			JobList j1 = jobsIn.getSubsetDelta(dueTime);
			float dueNew = jobsIn.getTime() + j1.getCompletionTime();
			if (dueNew > dueTime) {
				dueTime = dueNew;
				continue;
			}
			deltas.add(j1.getJob(j1.size()-1).getIndex() - jobK.getIndex());
			JobList j2 = jobsIn.getSubsetDeltaInverse(dueTime);
			if (j2.size() == 0) {
				break;
			}
			dueTime = j2.getJob(j2.size()-1).getDueTime();
		}
		
		return deltas;
	}
}
