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
	
	private static float[] epsilons = {0.1f, 0.5f, 1.0f, 2.0f};

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
		    writer.print("RDD, TF, n, Tardiness BestFirst, Runtime BestFirst, Tardiness Greedy, Runtime Greedy, Tardiness Exact, Runtime Exact");
		    for (int i = 0; i < epsilons.length; i++){
		    	writer.print(", Epsilon, Tardiness Approx, Runtime Approx");
		    }
		    writer.println();
		    for (int i = 0; i < listOfFiles.length; i++) {
		    	System.out.println("Running test " + i);
		    	ArrayList<String> res = runTest(listOfFiles[i].getAbsolutePath());
		    	String results = String.join(", ", res);
		    	System.out.println("Results " + results);
		    	writer.println(results);
		    }
		    writer.close();
		} catch (Exception e) {
		   e.printStackTrace();
		   System.exit(1);
		}
		
		//System.out.println("RDD, TF, n, Tardiness BestFirst, Runtime BestFirst, Tardiness Greedy, Runtime Greedy, Tardiness Exact, Runtime Exact, Tardiness Approx, Runtime Approx");
	}
	
	private static ArrayList<String> runTest(String fileLoc){
		//Extract the RDD, TF and N values from the filename.
		//Assumes filename is structured as <text>RDD=<RDD value>_<text>TF=<TF value>_<text>#<N value>.dat
		ArrayList<String> results = new ArrayList<String>();
		String RDD = "Unknown";
		String TF = "Unknown";
		String n = "Unknown";
		System.out.println("\tRunning test " + fileLoc);
		if (fileLoc.contains("random")){
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
		results.add(RDD);
		results.add(TF);
		results.add(n);
		
		//Read the file, creating a JobList and ProblemInstance
		JobList jobs = Test.getJobList(fileLoc);
		ProblemInstance pi = ComputeTardiness.readInstance(fileLoc);
		
		//Create instances of each algorithm class
		BestFirst bestFirst = new BestFirst(pi);
		Greedy greedy = new Greedy(pi);
		ExactAlgorithm exact = new ExactAlgorithm(jobs);
		ApproxAlgorithm approx = new ApproxAlgorithm(jobs);
		
		//Run and time BestFirst
		System.out.println("\tSkipping BestFirst");
		long startTime = System.nanoTime();
		String bfTardiness = "";
//		try{
//			bfTardiness = "" + bestFirst.getSchedule().getTardiness();
//		} catch (Exception e)
//		{
//			System.out.println("Unable to finish Best First");
//			bfTardiness = "Failed";
//		}
		bfTardiness = "N/A";
		long endTime = System.nanoTime();
		String bfTime = "" + (endTime-startTime);
		bfTime = "N/A";
		results.add(bfTardiness);
		results.add(bfTime);
		
		//Run and time Greedy
		System.out.println("\tRunning Greedy");
		startTime = System.nanoTime();
		String greedyTardiness = "" + greedy.getSchedule().getTardiness();
		endTime = System.nanoTime();
		String greedyTime = "" + (endTime-startTime);
		results.add(greedyTardiness);
		results.add(greedyTime);
		
		//run and time the Exact Algorithm
		System.out.println("\tRunning Exact 10x");
		long time = 0;
		String exactTardiness = "";
		for (int i = 0; i < 10; i++)
		{
			startTime = System.nanoTime();
			exactTardiness = "" + exact.solve().getTardiness();
			endTime = System.nanoTime();
			time += (endTime-startTime);
		}
		String exactTime = "" + (time/10);
		results.add(exactTardiness);
		results.add(exactTime);
		
		//run and time the Approximation Algorithm
		String approxTardiness = "";
		String approxTime = "";
		System.out.println("\tRunning Approx 10x for all epsilons");
		for (int i = 0; i < epsilons.length; i++){
			time = 0;
			for (int j = 0; j < 10; j++)
			{
				startTime = System.nanoTime();
				approxTardiness = "" + approx.solve(epsilons[i]);
				endTime = System.nanoTime();
				time += (endTime-startTime);
			}
			approxTime = "" + (time/10);
			results.add(Float.toString(epsilons[i]));
			results.add(approxTardiness);
			results.add(approxTime);
		}
		
		return results;
	}
}
