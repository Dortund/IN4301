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
		File files = new File("D:/Gebruikers/nomen/Documents/IN4301/IN4301/testSets");
		
		List<String> list = Arrays.asList(files.list());
		Collections.sort(list);
		
		for (String file : list) {
			String[] lines = file.split("#");
			if (Integer.parseInt(lines[1].substring(0, lines[1].indexOf("."))) == 5) {
				//try {
					System.out.println("Starting on: " + file);
					String str = files.getAbsolutePath() + "/" + file;
					
					ProblemInstance instance = readInstance(str);
					
					for (int i = 0; i< instance.getNumJobs(); i++) {
						System.out.print(instance.getJobs()[i][0] + ",");
					}
					System.out.println();
				
					System.out.println("Computing Greedy");
					Greedy greedy = new Greedy(instance);
					Schedule greedySchedule = greedy.getSchedule();
					//System.out.println(greedySchedule.getTardiness());
					int greedyVal = greedySchedule.getTardiness();
					
					System.out.println("Computing BestFirst");
					BestFirst bestFirst = new BestFirst(instance);
					Schedule bestFirstSchedule = bestFirst.getSchedule();
					//System.out.println(bestFirstSchedule.getTardiness());
					int bestVal = bestFirstSchedule.getTardiness();
					
					System.out.println("Computing Exact");
					JobList jL = Test.getJobList(str);
					ExactAlgorithm eA = new ExactAlgorithm(jL);
					int exact = eA.solve();
					
					System.out.println("Computing Brute Force");
					JobList jL2 = Test.getJobList(str);
					BruteForce bf = new BruteForce(jL2);
					int bfa = bf.solve();
					
					if (/*greedyVal == bestVal && greedyVal == exact*/ exact == bfa) {
						System.out.println("Correct: " + file);
					}
					else {
						System.err.println("Error: " + file);
						System.err.println("Exact: " + exact + ", BF: " + bfa + ", Greedy: " + greedyVal + ", Best: " + bestVal);
					}
					Thread.sleep(10);
				/*}
				catch (Exception e) {
					e.printStackTrace();
				}*/
			}
		}
		
	}
}
