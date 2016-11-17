package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import algorithms.ExactAlgorithm;
import dataStructures.Job;
import dataStructures.JobList;

public class Test {

	public static void main(String[] args) {
		/*String arg = "D:/Gebruikers/nomen/Documents/IN4301/IN4301/testSets/random_RDD=0.2_TF=0.2_#5.dat";
		JobList jList = getJobList(arg);
		ExactAlgorithm eA = new ExactAlgorithm(jList);
		int res = eA.solve();
		System.out.println(res);*/
		
		File files = new File("D:/Gebruikers/nomen/Documents/IN4301/IN4301/testSets");
		
		List<String> list = Arrays.asList(files.list());
		Collections.sort(list);
		
		for (String file : list) {
			String[] lines = file.split("#");
			if (Integer.parseInt(lines[1].substring(0, lines[1].indexOf("."))) <= 10
					&& lines[0].endsWith("random_RDD=0.2_TF=0.8_")) {
				try {
					System.out.println("Starting on: " + file);
					String str = files.getAbsolutePath() + "/" + file;
					boolean found = false;
					JobList jList = getJobList(str);
					for (int i = 0; i < jList.size(); i++) {
						for (int j = i+1; j < jList.size(); j++) {
							if (jList.getJob(i).getProcessingTime() == jList.getJob(j).getProcessingTime() && !found) {
								System.err.println(file);
								found = true;
							}
						}
					}
					ExactAlgorithm eA = new ExactAlgorithm(jList);
					int res = eA.solve();
					System.out.println(res);
				}
				catch (Exception e) {
					System.err.println(e.toString());
				}
			}
		}
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
