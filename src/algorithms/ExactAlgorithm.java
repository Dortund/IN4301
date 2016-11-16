package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.Job;
import dataStructures.JobList;

public class ExactAlgorithm {
	
	private Map<JobList, List<Integer>> cache;
	private JobList jobs;
	private int count = 0;
	
	private static float SMALL_VALUE = 0.00001f;
	
	public ExactAlgorithm(JobList jobs) {
		/*if (jobs.size() > 32) {
			throw new IndexOutOfBoundsException();
		}*/
		this.jobs = jobs;
		//cache = new HashMap<JobList, Integer>(this.jobs.getIntegerRepresentation() * this.jobs.getMaximumCompletionTime());
		cache = new HashMap<JobList, List<Integer>>();
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
		List<Integer> indices = this.solve(this.jobs);
		for (Integer i : indices) {
			System.out.print(jobs.getJob(i).getProcessingTime() + ",");
		}
		System.out.println();
		return this.getTardines(indices);
	}
	
	private List<Integer> solve(JobList jobsIn) {
		
		/*if (!jobsIn.sanityCheck()) {
			int x = 9;
			int y = x;
		}*/
		
		if (jobsIn.size() == 0) {
			return new ArrayList<Integer>(0);
		}
		
		if (jobsIn.size() == 1) {
			List<Integer> l = new ArrayList<Integer>(1);
			l.add(jobsIn.getJob(0).getIndex());
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
		List<Integer> opt1 = null;
		List<Integer> opt2 = null;
		
		//List<Integer> deltas = getDeltas(jobsIn);
		//for (Integer d : deltas) {
		for (int d = 0; d < this.jobs.size() - jobK.getIndex(); d++) {
			JobList j1= jobs.getSubset(jobsIn.getJob(0).getIndex(), jobK.getIndex() + d, jobK.getProcessingTime());
			j1.setTime(0);
			JobList j2 = jobs.getSubset(jobK.getIndex() + d + 1, jobsIn.getJob(jobsIn.size()-1).getIndex(), jobK.getProcessingTime());
			j2.setTime((int) (jobK.getProcessingTime()+j1.getCompletionTime()));
			
			List<Integer> l1 = this.solve(j1);
			List<Integer> l2 = this.solve(j2);
			
			int r1 = this.getTardines(l1);
			int r2 = this.getTardines(l2);
			
			int kVal = jobK.getWeight()*Math.max(0, (int) (jobK.getProcessingTime() + j1.getCompletionTime() - jobK.getDueTime()));
			
			int tardiness = r1 + kVal + r2;
			if (tardiness < res) {
				opt1 = l1;
				opt2 = l2;
			}
			//res = Math.min(res, r1 + kVal + r2);
			if (res == 0) {
				break;
			}
		}
		
		List<Integer> indices = new ArrayList<Integer>(opt1.size()+opt2.size()+1);
		indices.addAll(opt1);
		indices.add(jobK.getIndex());
		indices.addAll(opt2);
		
		this.cache.put(jobsIn, indices);
		
		count++;
		if (count % 100000 == 0 ) {
			System.out.println("Count: " + count);
		}
		/*try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return indices;
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
