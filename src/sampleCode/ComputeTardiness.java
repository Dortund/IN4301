package sampleCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import algorithms.BruteForce;
import algorithms.ExactAlgorithm;
import dataStructures.JobList;
import testing.Test;


public class ComputeTardiness {	
	public static ProblemInstance readInstance(String filename){
		ProblemInstance instance = null;
		
		try {
			int numJobs = 0;
			int[][] jobs = null;
			
			Scanner sc = new Scanner(new BufferedReader(new FileReader(filename)));
			if(sc.hasNextInt()){
				numJobs = sc.nextInt();
				jobs = new int[numJobs][2];
				int nextJobID = 0;
			
				while (sc.hasNextInt() && nextJobID < numJobs) {
					jobs[nextJobID][0] = sc.nextInt();
					jobs[nextJobID][1] = sc.nextInt();
					nextJobID++;
				}
			}
			sc.close();
			
			instance = new ProblemInstance(numJobs, jobs);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return instance;
	}

	// reads a problem, and outputs the result of both greedy and best-first
    /*public static void main (String args[]) {
		ProblemInstance instance = readInstance(args[0]);
		
		Greedy greedy = new Greedy(instance);
		Schedule greedySchedule = greedy.getSchedule();
		//System.out.println(greedySchedule.getTardiness());
		int greedyVal = greedySchedule.getTardiness();
		
		BestFirst bestFirst = new BestFirst(instance);
		Schedule bestFirstSchedule = bestFirst.getSchedule();
		//System.out.println(bestFirstSchedule.getTardiness());
		int bestVal = bestFirstSchedule.getTardiness();
		
		JobList jL = Test.getJobList(args[0]);
		ExactAlgorithm eA = new ExactAlgorithm(jL);
		int exact = eA.solve();
		
		if (greedyVal == bestVal && greedyVal == exact) {
			System.out.println("Correct");
		}
		
	}*/
	
	public static void main (String args[]) throws InterruptedException {
		File files = new File("./testSets");
		
		List<String> list = Arrays.asList(files.list());
		Collections.sort(list);
		
		/*String a = "01";
		String b = "10";
		String c = "001";
		String d = "100";
		String e = "010";
		String f = "101";
		System.out.println(a.hashCode());
		System.out.println(b.hashCode());
		System.out.println(c.hashCode());
		System.out.println(d.hashCode());
		System.out.println(e.hashCode());
		System.out.println(f.hashCode());*/

		for (String file : list) {
			String[] lines = file.split("#");
			if (
					!file.startsWith("custom") &&
					Integer.parseInt(lines[1].substring(0, lines[1].indexOf("."))) <= 101
					//&& lines[0].endsWith("random_RDD=0.2_TF=0.2_")
					//file.startsWith("custom")
					) {
				//try {
					System.out.println("Starting on: " + file);
					String str = files.getAbsolutePath() + "/" + file;
					
					ProblemInstance instance = readInstance(str);
					
					for (int i = 0; i< instance.getNumJobs(); i++) {
						//System.out.print(instance.getJobs()[i][0] + ",");
					}
					//System.out.println();
				
					//System.out.println("Computing Greedy");
					Greedy greedy = new Greedy(instance);
					Schedule greedySchedule = greedy.getSchedule();
					//System.out.println(greedySchedule.getTardiness());
					float greedyVal = greedySchedule.getTardiness();
					
					/*System.out.println("Computing BestFirst");
					BestFirst bestFirst = new BestFirst(instance);
					Schedule bestFirstSchedule = bestFirst.getSchedule();
					//System.out.println(bestFirstSchedule.getTardiness());
					int bestVal = bestFirstSchedule.getTardiness();*/
					
					/*boolean found = false;
					JobList jList = Test.getJobList(str);
					for (int i = 0; i < jList.size(); i++) {
						for (int j = i+1; j < jList.size(); j++) {
							if (jList.getJob(i).getProcessingTime() == jList.getJob(j).getProcessingTime() && !found) {
								System.err.println("Doubles: " + file);
								found = true;
							}
						}
					}*/
					long start = System.nanoTime();
					//System.out.println("Computing Exact");
					JobList jL = Test.getJobList(str);
					ExactAlgorithm eA = new ExactAlgorithm(jL);
					Schedule sc = eA.solve();
					int exact = (int)sc.getTardiness();
					
					//System.out.println((sc.getTotalTime()) + "--" + (sc.getTardiness()));
					
					//System.out.println(exact);
					//System.err.println((System.nanoTime() - start) / 1000000);
					
					/*for (int i = 0; i < 1000; i++) {
						int x = 9;
						int y = x;
					}*/
					
					if (Integer.parseInt(lines[1].substring(0, lines[1].indexOf("."))) <= 10) {
						System.out.println("Computing Brute Force");
						JobList jL2 = Test.getJobList(str);
						BruteForce bf = new BruteForce(jL2);
						int bfa = bf.solve();
						
						if (/*greedyVal == bestVal && greedyVal == exact &&*/ exact == bfa) {
							System.out.println("Correct: " + file);
							System.out.println("Exact: " + exact + ", BF: " + bfa/* + ", Greedy: " + greedyVal/* + ", Best: " + bestVal*/);
						}
						else {
							System.err.println("Error: " + file);
							System.err.println("Exact: " + exact + ", BF: " + bfa/* + ", Greedy: " + greedyVal/* + ", Best: " + bestVal*/);
						}
						Thread.sleep(100);
					}
					else {
						if (exact > greedyVal) {
							System.err.println("stress");
						}
					}
				/*}
				catch (Exception e) {
					e.printStackTrace();
				}*/
			}
		}
		
	}
}
