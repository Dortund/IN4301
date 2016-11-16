package algorithms;

import java.util.ArrayList;
import java.util.List;

import dataStructures.Job;
import dataStructures.JobList;

public class BruteForce {

	private JobList jobs;
	
	public BruteForce(JobList jobs) {
		this.jobs = jobs;
	}
	
	public int solve() {
		if (jobs.size() >= 30) {
			throw new IndexOutOfBoundsException("Too Many Jobs");
		}
		List<Integer> indices = new ArrayList<Integer>(jobs.size());
		for (int i = 0; i < jobs.size(); i++) {
			indices.add(i);
		}
		List<Integer> res = solveRec(indices);
		for (Integer i : res) {
			System.out.print(jobs.getJob(i).getProcessingTime() + ",");
		}
		System.out.println();
		return getTardines(res);
	}
	
	private List<Integer> solveRec(List<Integer> indices) {
		int tardiness = Integer.MAX_VALUE;
		List<Integer> opt = new ArrayList<Integer>(0);
		
		for (Integer i : indices) {
			List<Integer> copy = new ArrayList<Integer>(indices);
			copy.remove(copy.indexOf(i));
			
			List<Integer> l = solveRec(copy);
			l.add(i);
			
			int tard = getTardines(l);
			if (tard < tardiness) {
				tardiness = tard;
				opt = l;
			}
		}
		
		return opt;
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
	
	/*public solveAll() {
		List<Integer[]> perms = new ArrayList<Integer[]>(factorial(jobs.size()));
		
		for (int n = 0; n < jobs.size(); n++) {
			for
		}
	}*/
	
	/*private int factorial(int x) {
		int res = 1;
		for (int i = 1; i <= x; i++) {
			res *= i;
		}
		return res;
	}*/
}
