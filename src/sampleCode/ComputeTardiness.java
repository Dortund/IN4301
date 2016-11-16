package sampleCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import algorithms.ExactAlgorithm;
import dataStructures.JobList;
import testing.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
	
	public static void main (String args[]) {
		File files = new File("D:/Gebruikers/nomen/Documents/IN4301/IN4301/testSets");
		
		List<String> list = Arrays.asList(files.list());
		Collections.sort(list);
		
		for (String file : list) {
			String[] lines = file.split("#");
			if (Integer.parseInt(lines[1].substring(0, lines[1].indexOf("."))) <= 4) {
				try {
					System.out.println("Starting on: " + file);
					String str = files.getAbsolutePath() + "/" + file;
					
					ProblemInstance instance = readInstance(str);
				
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
					
					if (greedyVal == bestVal && greedyVal == exact) {
						System.out.println("Correct: " + file);
					}
					else {
						System.err.println("Error: " + file);
					}
				}
				catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		}
		
	}
}
