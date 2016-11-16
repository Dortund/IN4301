import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithms.ExactAlgorithm;
import dataStructures.Job;
import dataStructures.JobList;

public class Main {

	public static void main(String[] args) {
		//int epsilon = Integer.parseInt(args[0]);
		String path = args[1];
		
		JobList jList = getJobList(path);
		ExactAlgorithm eA = new ExactAlgorithm(jList);
		int res = eA.solve();
		System.out.println(res + " " + 1);
	}
	
	public static JobList getJobList(String arg) {
		File file = new File(arg);
		List<Job> jobs = new ArrayList<Job>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    boolean first = true;
		    while ((line = br.readLine()) != null) {
		       if (first) {
		    	   //System.out.println("Input size: " + line);
		    	   first = false;
		       }
		       else {
		    	   String[] lines = line.split(" ");
		    	   if (lines.length == 2) {
		    		   jobs.add(new Job(0, Integer.parseInt(lines[0]), Integer.parseInt(lines[1]), 1));
		    	   }
		       }
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(jobs);
		List<Job> jobsFinal = new ArrayList<Job>(jobs.size());
		for (int i = 0; i < jobs.size(); i++) {
			Job j = jobs.get(i);
			jobsFinal.add(new Job(i, j.getProcessingTime(), j.getDueTime(), j.getWeight()));
		}
		return new JobList(jobsFinal, 0);
	}
}
