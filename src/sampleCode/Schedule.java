package sampleCode;

import dataStructures.Job;

public class Schedule
  implements Comparable<Schedule>
{
  private Schedule previous;
  private Schedule next;
  private Job job;
  private float tardiness;
  private float startTime;
  
  public Schedule()
  {
    this.previous = null;
    this.job = null;
    this.tardiness = 0.0F;
    this.startTime = 0.0F;
  }
  
  public Schedule(Schedule s, int jobID, int jobLength, int jobDueTime)
  {
    this.previous = s;
    if (this.previous != null) {
      this.previous.next = this;
    }
    Job j = new Job(jobID, jobLength, jobDueTime, 1.0F);
    this.job = j;
    this.tardiness = Math.max(0.0F, getTotalTime() - jobDueTime);
    this.startTime = 0.0F;
    if (this.previous != null) {
      this.tardiness += this.previous.getTardiness();
    }
  }
  
  public Schedule(Schedule s, Job job)
  {
    this.previous = s;
    if (this.previous != null) {
      this.previous.next = this;
    }
    this.job = job;
    this.tardiness = Math.max(0.0F, getTotalTime() - this.job.getDueTime());
    this.startTime = 0.0F;
    if (this.previous != null) {
      this.tardiness += this.previous.getTardiness();
    }
  }
  
  public int compareTo(Schedule o)
  {
    return (int)(getTardiness() - o.getTardiness());
  }
  
  public int getDepth()
  {
    int depth = 1;
    if (this.previous != null) {
      depth += this.previous.getDepth();
    }
    return depth;
  }
  
  public float getTotalTime()
  {
    float time = this.job.getProcessingTime();
    if (this.previous != null) {
      time += this.previous.getTotalTime();
    } else {
      time += this.startTime;
    }
    return time;
  }
  
  public float getTardiness()
  {
    return this.tardiness;
  }
  
  public void updateStartTime(float time)
  {
    this.startTime = time;
    if (this.previous != null)
    {
      this.previous.updateStartTime(time);
      this.tardiness = (Math.max(0.0F, getTotalTime() - this.job.getDueTime()) + this.previous.tardiness);
    }
    else
    {
      this.tardiness = Math.max(0.0F, getTotalTime() - this.job.getDueTime());
    }
  }
  
  public boolean containsJob(int job)
  {
    return (this.job.getIndex() == job) || ((this.previous != null) && (this.previous.containsJob(job)));
  }
  
  public Job getJob()
  {
    return this.job;
  }
  
  public Schedule next()
  {
    return this.next;
  }
  
  public Schedule getFirst()
  {
    if (this.previous == null) {
      return this;
    }
    return this.previous.getFirst();
  }
  
  public String toString()
  {
    String res = "";
    if (this.previous != null) {
      res = res + this.previous.toString();
    }
    res = res + "(" + this.job.getIndex() + "," + this.job.getProcessingTime() + "),";
    return res;
  }
}
