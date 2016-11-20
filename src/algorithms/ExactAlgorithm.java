package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.Job;
import dataStructures.JobList;
import dataStructures.JobListDelta;
import sampleCode.Schedule;

public class ExactAlgorithm {
	
	private Map<Float, Map<JobList, Schedule>> cache;
	private JobList jobs;
	
	public ExactAlgorithm(JobList jobs) {
		this.jobs = jobs;
		cache = new HashMap<Float, Map<JobList, Schedule>>();
	}
	
	public Schedule solve() {
		Schedule schedule = this.solve(this.jobs, 0, this.jobs.size()-1);
		return schedule;
	}

	private Schedule solve(JobList jobsIn, int i, int j) {
		//System.out.println(jobsIn.toString());
		if (jobsIn.size() == 0) {
			return null;
		}
		
		if (jobsIn.size() == 1) {
			Schedule schedule = new Schedule(null, jobsIn.getJob(0));
			schedule.updateStartTime(jobsIn.getTime());
			return schedule;
		}
		
		Map<JobList, Schedule> jobLists = this.cache.get(jobsIn.getTime());
		if (jobLists != null) {
			Schedule cachedSchedule = jobLists.get(jobsIn);
			if (cachedSchedule != null) {
				return cachedSchedule;
			}
		}
		
		Job jobK = jobsIn.getLongestProcessingJob();
		
		float res = Float.MAX_VALUE;
		Schedule opt1 = null;
		Schedule opt2 = null;
		
		List<Integer> deltas = getDeltas(jobsIn);
		for (Integer d : deltas) {
			JobList j1= jobsIn.getSubset(i, jobK.getIndex() + d, jobK);
			j1.setTime(jobsIn.getTime());
			JobList j2 = jobsIn.getSubset(jobK.getIndex() + d + 1, j, jobK);
			j2.setTime(jobsIn.getTime()+j1.getCompletionTime()+jobK.getProcessingTime());

			Schedule l1 = this.solve(j1, i, jobK.getIndex() + d);
			Schedule l2 = this.solve(j2, jobK.getIndex() + d + 1, j);
			
			float r1 = 0;
			float r2 = 0;
			if (l1 != null) {
				r1 = l1.getTardiness();
			}
			if (l2 != null) {
				r2 = l2.getTardiness();
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
		
		Map<JobList, Schedule> c = this.cache.get(jobsIn.getTime());
		if (c == null) {
			c = new HashMap<JobList, Schedule>();
			c.put(jobsIn, fin);
			this.cache.put(jobsIn.getTime(), c);
		}
		else {
			c.put(jobsIn, fin);
		}
		
		return fin;
	}
	
	private List<Integer> getDeltas(JobList jobsIn) {
		List<Integer> deltas = new ArrayList<Integer>();
		Job jobK = jobsIn.getLongestProcessingJob();

		float dueTime = jobK.getDueTime();
		
		JobListDelta jd = new JobListDelta(jobsIn);
		
		while (true) {
			//JobList j1 = jobsIn.getSubsetDelta(dueTime);
			//float dueNew = jobsIn.getTime() + j1.getCompletionTime();
			float dueNew = jobsIn.getTime() + jd.findNewIndex(dueTime);
			if (dueNew > dueTime) {
				dueTime = dueNew;
				continue;
			}
			//deltas.add(j1.getJob(j1.size()-1).getIndex() - jobK.getIndex());
			deltas.add(jd.getDeltaJob().getIndex() - jobK.getIndex());
			/*JobList j2 = jobsIn.getSubsetDeltaInverse(dueTime);
			if (j2.size() == 0) {
				break;
			}*/
			if (jd.isFinished()) {
				break;
			}
			//dueTime = j2.getJob(j2.size()-1).getDueTime();
			dueTime = jd.getNext().getDueTime();
		}
		
		return deltas;
	}
}
