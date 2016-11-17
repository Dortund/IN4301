package dataStructures;

public class Job  implements Comparable<Job>{

	private int index;
	private float processingTime;
	private float dueTime;
	private float weight;
	
	public Job(int index, float processingTime, float dueTime, float weight) {
		this.index = index;
		this.processingTime = processingTime;
		this.dueTime = dueTime;
		this.weight = weight;
	}

	public int getIndex() {
		return this.index;
	}

	public float getProcessingTime() {
		return this.processingTime;
	}
	
	public void offsetProcessintTime(float value) {
		this.processingTime += value;
	}

	public float getDueTime() {
		return this.dueTime;
	}
	
	public float getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(Job other) {
		// Check due time
		if (this.getDueTime() < other.getDueTime()) {
			return -1;
		}
		if (this.getDueTime() > other.getDueTime()) {
			return 1;
		}
		
		// Due time is equal, check processing time
		if (this.getProcessingTime() < other.getProcessingTime()) {
			return -1;
		}
		if (this.getProcessingTime() > other.getProcessingTime()) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return "Job: " + this.index + ", " + this.getProcessingTime() + ", " + this.getDueTime();
	}
}
