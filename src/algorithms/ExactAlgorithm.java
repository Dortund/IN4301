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
	private int count = 0;
	
	private static float SMALL_VALUE = 0.00001f;
	
	public ExactAlgorithm(JobList jobs) {
		/*if (jobs.size() > 32) {
			throw new IndexOutOfBoundsException();
		}*/
		this.jobs = jobs;
		//cache = new HashMap<JobList, Integer>(this.jobs.getIntegerRepresentation() * this.jobs.getMaximumCompletionTime());
		cache = new HashMap<JobList, Schedule>();
		//System.out.println("Things: " + this.jobs.getIntegerRepresentation() * this.jobs.getMaximumCompletionTime());
		this.fixInput();
		/*for (Job job : this.jobs.getJobs()) {
			System.out.println(job);
		}*/
	}
	
	private void fixInput() {
		Map<Integer, Integer> doubles = new HashMap<Integer,Integer>();
		for (int i = 0; i < this.jobs.size(); i++) {
			for (int j = i+1; j < this.jobs.size(); j++) {
				if (this.jobs.getJob(i).getProcessingTime() == this.jobs.getJob(j).getProcessingTime()) {
					int count = 1;
					if (doubles.containsKey(this.jobs.getJob(i).getProcessingTime())) {
						count = doubles.get(this.jobs.getJob(i).getProcessingTime()) + 1;
					}
					this.jobs.getJob(j).offsetProcessintTime(count * SMALL_VALUE);
					doubles.put((int) this.jobs.getJob(i).getProcessingTime(), count);
				}
			}
		}
	}
	
	public int solve() {
		Schedule schedule = this.solve(this.jobs);
		/*for (Integer i : indices) {
			System.out.print(jobs.getJob(i).getIndex() + ",");
		}
		System.out.println();*/
		//System.out.println(schedule.toString());
		return schedule.getTardiness();
	}
	
	private Schedule solve(JobList jobsIn) {
		
		/*if (!jobsIn.sanityCheck()) {
			int x = 9;
			int y = x;
		}*/
		
		if (jobsIn.size() == 0) {
			return null;
		}
		
		if (jobsIn.size() == 1) {
			Schedule schedule = new Schedule(null, jobsIn.getJob(0));
			schedule.updateStartTime(jobsIn.getTime());
			return schedule;
			/*Job job = jobsIn.getJob(0);
			return (int) (job.getWeight() * Math.max(0, (int) (jobsIn.getTime() + job.getProcessingTime() - job.getDueTime())));*/
		}
		
		if (this.cache.containsKey(jobsIn)) {
			//System.err.println(jobsIn);
			return this.cache.get(jobsIn);
		}
		
		Job jobK = jobsIn.getLongestProcessingJob();
		//System.out.println(jobsIn);
		int res = Integer.MAX_VALUE;
		Schedule opt1 = null;
		Schedule opt2 = null;
		
		List<Integer> deltas = getDeltas(jobsIn);
		for (Integer d : deltas) {
		//for (int d = 0; d < this.jobs.size() - jobK.getIndex(); d++) {
			// 1e d = 9
			// 2e d = 3
			
			JobList j1= jobs.getSubset(jobsIn.getJob(0).getIndex(), jobK.getIndex() + d, jobK.getProcessingTime());
			j1.setTime(0);
			JobList j2 = jobs.getSubset(jobK.getIndex() + d + 1, jobsIn.getJob(jobsIn.size()-1).getIndex(), jobK.getProcessingTime());
			j2.setTime((int) (jobK.getProcessingTime()+j1.getCompletionTime()));
			
			Schedule l1 = this.solve(j1);
			Schedule l2 = this.solve(j2);
			
			int r1 = 0;
			int r2 = 0;
			if (l1 != null)
				r1 = l1.getTardiness();
			if (l2 != null)
				r2 = l2.getTardiness();
			
			int kVal = jobK.getWeight()*Math.max(0, (int) (jobK.getProcessingTime() + j1.getCompletionTime() - jobK.getDueTime()));
			
			int tardiness = r1 + kVal + r2;
			if (tardiness < res) {
				opt1 = l1;
				opt2 = l2;
				res = tardiness;
			}
			//res = Math.min(res, r1 + kVal + r2);
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
		
		this.cache.put(jobsIn, fin);
		
		count++;
		if (count % 100000 == 0 ) {
			//System.out.println("Count: " + count);
		}
		/*try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	
	private int getTardines(List<Integer> indices) {
		int res = 0;
		int runningTime = 0;
		for (Integer i : indices) {
			Job j = this.jobs.getJob(i);
			runningTime += j.getProcessingTime();
			res += j.getWeight() * Math.max(0, runningTime - j.getDueTime());
		}
		return res;
	}
}
