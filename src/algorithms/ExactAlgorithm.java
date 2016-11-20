package algorithms;

import dataStructures.Job;
import dataStructures.JobList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sampleCode.Schedule;

public class ExactAlgorithm
{
  private Map<Float, Map<JobList, Schedule>> cache;
  private JobList jobs;
  
  public ExactAlgorithm(JobList jobs)
  {
    this.jobs = jobs;
    this.cache = new HashMap<Float, Map<JobList, Schedule>>();
  }
  
  public Schedule solve()
  {
    Schedule schedule = solve(this.jobs, 0, this.jobs.size() - 1, 0, -1);
    return schedule;
  }
  int count = 0;
  private Schedule solve(JobList jobsIn, int i, int j, int depth, int delta)
  {
    if (jobsIn.size() == 0) {
      return null;
    }
    if (jobsIn.size() == 1)
    {
      Schedule schedule = new Schedule(null, jobsIn.getJob(0));
      schedule.updateStartTime(jobsIn.getTime());
      return schedule;
    }
    /*if (this.cache.containsKey(jobsIn)) {
      return (Schedule)this.cache.get(jobsIn);
    }*/
    Map<JobList, Schedule> jobLists = this.cache.get(jobsIn.getTime());
	if (jobLists != null) {
		Schedule cachedSchedule = jobLists.get(jobsIn);
		if (cachedSchedule != null) {
			count++;
			return cachedSchedule;
		}
	}
    Job jobK = jobsIn.getLongestProcessingJob();
    
    float res = Float.MAX_VALUE;
    Schedule opt1 = null;
    Schedule opt2 = null;
    
    List<Integer> deltas = getDeltas(jobsIn);
    for (Integer d : deltas)
    {
      JobList j1 = jobsIn.getSubset(i, jobK.getIndex() + d.intValue(), jobK);
      j1.setTime(jobsIn.getTime());
      JobList j2 = jobsIn.getSubset(jobK.getIndex() + d.intValue() + 1, j, jobK);
      j2.setTime(jobsIn.getTime() + j1.getCompletionTime() + jobK.getProcessingTime());
      
      Schedule l1 = solve(j1, i, jobK.getIndex() + d.intValue(), depth + 1, d.intValue());
      Schedule l2 = solve(j2, jobK.getIndex() + d.intValue() + 1, j, depth + 1, d.intValue());
      
      float r1 = 0.0F;
      float r2 = 0.0F;
      if (l1 != null) {
        r1 = l1.getTardiness();
      }
      if (l2 != null) {
        r2 = l2.getTardiness();
      }
      float kVal = jobK.getWeight() * Math.max(0.0F, jobsIn.getTime() + j1.getCompletionTime() + jobK.getProcessingTime() - jobK.getDueTime());
      
      float tardiness = r1 + kVal + r2;
      if (tardiness < res)
      {
        opt1 = l1;
        opt2 = l2;
        res = tardiness;
      }
      if (res == 0.0F) {
        break;
      }
    }
    Schedule fin = null;
    if (opt1 != null)
    {
      Schedule s = opt1.getFirst();
      while (s != null)
      {
        fin = new Schedule(fin, s.getJob());
        s = s.next();
      }
    }
    fin = new Schedule(fin, jobK);
    if (opt2 != null)
    {
      Schedule s = opt2.getFirst();
      while (s != null)
      {
        fin = new Schedule(fin, s.getJob());
        s = s.next();
      }
    }
    fin.updateStartTime(jobsIn.getTime());
    
    //this.cache.put(jobsIn, fin);
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
  
  private List<Integer> getDeltas(JobList jobsIn)
  {
    List<Integer> deltas = new ArrayList<Integer>();
    Job jobK = jobsIn.getLongestProcessingJob();

    float dueTime = jobK.getDueTime();
    for (;;)
    {
      JobList j1 = jobsIn.getSubsetDelta(dueTime);
      float dueNew = jobsIn.getTime() + j1.getCompletionTime();
      if (dueNew > dueTime)
      {
        dueTime = dueNew;
      }
      else
      {
        deltas.add(Integer.valueOf(j1.getJob(j1.size() - 1).getIndex() - jobK.getIndex()));
        JobList j2 = jobsIn.getSubsetDeltaInverse(dueTime);
        if (j2.size() == 0) {
          break;
        }
        dueTime = j2.getJob(0).getDueTime();
      }
    }
    return deltas;
  }
}
