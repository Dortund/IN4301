package dataStructures;

public class JobListDelta extends JobList {

	private int index;
	private float totalTime;
	
	public JobListDelta(JobList joblist) {
		super(joblist);
		
		this.index = -1;
		this.totalTime = 0;
	}
	
	public float findNewIndex(float dTime) {
		for (int i = this.index + 1; i < super.getJobs().size(); i++) {
			if (this.jobs.get(i).getDueTime() <= dTime) {
				this.index++;
				this.totalTime += this.jobs.get(i).getProcessingTime();
			}
			else {
				break;
			}
		}
		return this.totalTime;
	}
	
	public Job getDeltaJob() {
		return this.jobs.get(this.index);
	}
	
	public Job getNext() {
		if (!this.isFinished()) {
			return this.jobs.get(this.index+1);
		}
		return null;
	}
	
	public float getTotalTime() {
		return this.totalTime;
	}
	
	public boolean isFinished() {
		return this.index == this.size() - 1;
	}
}
