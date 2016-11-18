package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.*;
import dataStructures.Job;
import dataStructures.JobList;
import sampleCode.*;

import java.io.PrintWriter;

public class Test {

	public static void main(String[] args){
		float epsilon = 2;

		System.out.println("Starting with tests, epsilon = " + epsilon);
		runTests(epsilon);
		System.out.println("Done running test with epsilon = " + epsilon);
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
	
	public static void fixInput(List<Job> jobs) {
		Map<Integer, Integer> doubles = new HashMap<Integer,Integer>();
		for (int i = 0; i < jobs.size(); i++) {
			for (int j = i+1; j < jobs.size(); j++) {
				if (jobs.get(i).getProcessingTime() == jobs.get(j).getProcessingTime()) {
					int count = 1;
					if (doubles.containsKey(jobs.get(i).getProcessingTime())) {
						count = doubles.get(jobs.get(i).getProcessingTime()) + 1;
					}
					jobs.get(j).offsetProcessintTime(count * 0.0001f);
					doubles.put((int) jobs.get(i).getProcessingTime(), count);
				}
			}
		}
	}

	public static void runTests(float epsilon){
		File folder = new File("./testSets");
		File[] listOfFiles = folder.listFiles();
		
		try{
		    PrintWriter writer = new PrintWriter("testresults.txt", "UTF-8");
		    writer.println("RDD, TF, n, Tardiness BestFirst, Runtime BestFirst, Tardiness Greedy, Runtime Greedy, Tardiness Exact, Runtime Exact, Tardiness Approx, Runtime Approx");
		    for (int i = 0; i < listOfFiles.length; i++) {
		    	System.out.println("Running test " + i);
		    	String res = runTest(epsilon, listOfFiles[i].getAbsolutePath());
		    	System.out.println("\tResult: " + res);
		    	writer.println();
		    }
		    writer.close();
		} catch (Exception e) {
		   e.printStackTrace();
		   System.exit(1);
		}
		
		//System.out.println("RDD, TF, n, Tardiness BestFirst, Runtime BestFirst, Tardiness Greedy, Runtime Greedy, Tardiness Exact, Runtime Exact, Tardiness Approx, Runtime Approx");
	}
	
	private static String runTest(float epsilon, String fileLoc){
		//Extract the RDD, TF and N values from the filename.
		//Assumes filename is structured as <text>RDD=<RDD value>_<text>TF=<TF value>_<text>#<N value>.dat
		String RDD = "Unknown";
		String TF = "Unknown";
		String n = "Unknown";
		System.out.println("\tRunning test " + fileLoc);
		if (fileLoc.startsWith("random")){
			int start = fileLoc.indexOf("RDD=", 0);
			int end = fileLoc.indexOf("_", start);
			RDD = fileLoc.substring(start+4, end);
			
			start = fileLoc.indexOf("TF=", end);
			end = fileLoc.indexOf("_", start);
			TF = fileLoc.substring(start+3, end);
			
			start = fileLoc.indexOf("#");
			end = fileLoc.indexOf(".dat", start);
			n = fileLoc.substring(start+1, end);
		}
		
		//Read the file, creating a JobList and ProblemInstance
		JobList jobs = Test.getJobList(fileLoc);
		ProblemInstance pi = ComputeTardiness.readInstance(fileLoc);
		
		//Create instances of each algorithm class
		BestFirst bestFirst = new BestFirst(pi);
		Greedy greedy = new Greedy(pi);
		ExactAlgorithm exact = new ExactAlgorithm(jobs);
		ApproxAlgorithm approx = new ApproxAlgorithm(jobs);
		
		//Run and time BestFirst
		System.out.println("\tRunning BestFirst");
		long startTime = System.nanoTime();
		String bfTardiness = "";
		try{
			bfTardiness = "" + bestFirst.getSchedule().getTardiness();
		} catch (Exception e)
		{
			System.out.println("Unable to finish Best First");
			bfTardiness = "Failed";
		}
		//String bfTardiness = "Very long";
		long endTime = System.nanoTime();
		String bfTime = "" + (endTime-startTime);
		
		//Run and time Greedy
		System.out.println("\tRunning Greedy");
		startTime = System.nanoTime();
		String greedyTardiness = "" + greedy.getSchedule().getTardiness();
		endTime = System.nanoTime();
		String greedyTime = "" + (endTime-startTime);
		
		//run and time the Exact Algorithm
		System.out.println("\tSkipping Exact");
		startTime = System.nanoTime();
		String exactTardiness = "" + exact.solve().getTardiness();
		endTime = System.nanoTime();
		String exactTime = "" + (endTime-startTime);
		
		//run and time the Approximation Algorithm
		System.out.println("\tSkipping Approx");
		startTime = System.nanoTime();
		String approxTardiness = "" + approx.solve(epsilon);
		endTime = System.nanoTime();
		String approxTime = "" + (endTime-startTime);
		
		String[] results = {RDD, TF, n, bfTardiness, bfTime, greedyTardiness, greedyTime, exactTardiness, exactTime, approxTardiness, approxTime};
		return String.join(", ", results);
	}
}
