package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.Job;
import dataStructures.JobList;
import sampleCode.Schedule;

public class ExactAlgorithm {
	
	private Map<JobList, Schedule> cache;
	private JobList jobs;
	
	public ExactAlgorithm(JobList jobs) {
		/*if (jobs.size() > 32) {
			throw new IndexOutOfBoundsException();
		}*/
		this.jobs = jobs;
		//cache = new HashMap<JobList, Integer>(this.jobs.getIntegerRepresentation() * this.jobs.getMaximumCompletionTime());
		cache = new HashMap<JobList, Schedule>();
		//System.out.println("Things: " + this.jobs.getIntegerRepresentation() * this.jobs.getMaximumCompletionTime());
		//this.fixInput();
		/*for (Job job : this.jobs.getJobs()) {
			System.out.println(job);
		}*/
	}
	
	public float solve() {
		Schedule schedule = this.solve(this.jobs, 0, this.jobs.size()-1, 0,-1);
		/*for (Integer i : indices) {
			System.out.print(jobs.getJob(i).getIndex() + ",");
		}
		System.out.println();*/
		System.out.println("Ex: " + schedule.toString());
		return schedule.getTardiness();
	}
	
	private Schedule solve(JobList jobsIn, int i, int j, int depth, int delta) {
		//System.out.println("("+depth+","+delta+") "+jobsIn);
		if (depth == 1 && delta == 0) {
			int x = 9;
			int y = x;
		}
		
		if (jobsIn.size() == 0) {
			return null;
		}
		
		if (jobsIn.size() == 1) {
			Schedule schedule = new Schedule(null, jobsIn.getJob(0));
			schedule.updateStartTime(jobsIn.getTime());
			return schedule;
		}
		
		if (this.cache.containsKey(jobsIn)) {
			return this.cache.get(jobsIn);
		}
		
		Job jobK = jobsIn.getLongestProcessingJob();
		
		float res = Float.MAX_VALUE;
		Schedule opt1 = null;
		Schedule opt2 = null;
		
		List<Integer> deltas = getDeltas(jobsIn);
		for (Integer d : deltas) {
			JobList j1= this.jobs.getSubset(i, jobK.getIndex() + d, jobK);
			j1.setTime(jobsIn.getTime());
			JobList j2 = this.jobs.getSubset(jobK.getIndex() + d + 1, j, jobK);
			j2.setTime(jobsIn.getTime()+j1.getCompletionTime()+jobK.getProcessingTime());
			
			Schedule l1 = this.solve(j1, i, jobK.getIndex() + d, depth+1,d);
			Schedule l2 = this.solve(j2, jobK.getIndex() + d + 1, j, depth+1,d);
			
			float r1 = 0;
			float r2 = 0;
			if (l1 != null) {
				r1 = l1.getTardiness();
				//r1 = this.getTardines(l1, jobsIn.getTime());
			}
			if (l2 != null) {
				r2 = l2.getTardiness();
				//r2 = this.getTardines(l2, jobsIn.getTime() + j1.getCompletionTime() +jobK.getProcessingTime());
			}
			
			float kVal = jobK.getWeight()*Math.max(0, jobsIn.getTime() + j1.getCompletionTime() + jobK.getProcessingTime() - jobK.getDueTime());
			
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
		
		float check = getTardines(fin, jobsIn.getTime());
		
		if (check != fin.getTardiness() || check != res || res != fin.getTardiness()) {
			System.err.println(depth +"," + delta + ">, " + check + ", " + fin.getTardiness() + ", " + res);
		}
		
		this.cache.put(jobsIn, fin);
		
		return fin;
	}
	
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
			dueTime = j2.getJob(0).getDueTime();
		}
		
		return deltas;
	}
	
	private float getTardines(Schedule input, float startTime) {
		float res = 0;
		float runningTime = startTime;
		Schedule s = input.getFirst();
		while (s != null) {
			Job j = s.getJob();
			runningTime += j.getProcessingTime();
			res += j.getWeight() * Math.max(0, runningTime - j.getDueTime());
			s = s.next();
		}
		return res;
	}
}
