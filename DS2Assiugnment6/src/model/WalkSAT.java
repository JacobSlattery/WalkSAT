package model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class WalkSAT {

	public static final String FILENAME = "SAT_Expression03.txt";

	private ArrayList<ArrayList<Integer>> clauses;
	private ArrayList<Integer> variables;
	private HashMap<Integer, Boolean> map;
	private Random random;
	private HashMap<ArrayList<Integer>, Boolean> satisfiedClauseMap;
	private ArrayList<Integer> finalVariables;
	private int flipsNotTaken;

	/**
	 * Initializes a WalkSAT object
	 */
	public WalkSAT() {
		this.random = new Random();
		this.satisfiedClauseMap = new HashMap<ArrayList<Integer>, Boolean>();
		this.finalVariables = new ArrayList<Integer>();
		this.flipsNotTaken = 0;
	}


	/**
	 * Runs the WalkSAT algorithm
	 * @Precondition none
	 * @Postcondition none 
	 * @return true, if successful
	 */
	public boolean run() {

		this.populateClauses(FILENAME);
		this.populateVariables();
		this.initializeMap();

		boolean end = false;
		int count = 1;

		while (!this.doesMapSatisfyClauses()) {
			int key;
			int index = this.random.nextInt(this.variables.size());
			key = this.variables.get(index);
			
			while(finalVariables.contains(key)) {
				index = this.random.nextInt(this.variables.size());
				key = this.variables.get(index);
			}
						
			this.tryFlip(key);
			
			if (count % 10000 == 0) {
				System.out.println(String.format("%,d", count));
				System.out.println("flips not taken = " + flipsNotTaken);
				this.initializeMap();
				flipsNotTaken = 0;
				count = 0;
			}

			count++;
		}

		System.out.println();
		System.out.println("Clauses: " + this.clauses);
		System.out.println("Variables: " + this.variables);
		System.out.println("Map: " + this.map);
		System.out.println("Count: " + (String.format("%,d", count)));
		System.out.println(this.doesMapSatisfyClauses());
		return end;

	}

	private void tryFlip(Integer key) {

		int lastCount = this.getSatisfiedCount();
		
		this.flipValueAt(key);
		doesMapSatisfyClauses();
		
		if (lastCount > this.getSatisfiedCount()) {
			float successChance = (float)1 - ((float)this.getSatisfiedCount() / (float)lastCount);
			float successRoll = (float)(this.random.nextInt(100)) / (float)100;
			
			if(successRoll > successChance) {
				this.flipValueAt(key);
				flipsNotTaken++;
			}
		}

	}

	private int getSatisfiedCount() {
		Collection<Boolean> lastList = (Collection<Boolean>) this.satisfiedClauseMap.values();
		return (int) lastList.stream().filter(t -> t == true).count();
	}

	private void flipValueAt(Integer key) {
		Boolean currentValue = this.map.get(key);
		this.map.put(key, !currentValue);
	}

	private boolean doesMapSatisfyClauses() {
		boolean pass = true;
		for (ArrayList<Integer> clause : this.clauses) {
			Boolean current = this.clauseValue(clause);
			this.satisfiedClauseMap.put(clause, current);
			pass = pass && current;
		}
		return pass;
	}

	private boolean clauseValue(ArrayList<Integer> clause) {
		for (Integer number : clause) {
			boolean mapBool = this.map.get(Math.abs(number));
			if (number < 0) {
				mapBool = !mapBool;
			}
			if (mapBool == true) {
				return true;
			}
		}

		return false;
	}

	private void initializeMap() {
		this.map = new HashMap<Integer, Boolean>();
		Random random = new Random();
		for (Integer variable : this.variables) {
			boolean randomValue = random.nextBoolean();
			this.map.put(variable, randomValue);
		}
		setFinalVariables();
		
	}
	
	private void setFinalVariables() {
		for(ArrayList<Integer> eachClause : this.clauses) {
			if(eachClause.size() == 1) {
				Integer thisVariable = eachClause.get(0);
				this.finalVariables.add(Math.abs(thisVariable));
				if(thisVariable < 0) {
					this.map.put(Math.abs(thisVariable), false);
				} else {
					this.map.put(Math.abs(thisVariable), true);
				}
			}
		}
	}
	
	private void populateVariables() {
		this.variables = new ArrayList<Integer>();
		for (ArrayList<Integer> clause : clauses) {
			for (Integer number : clause) {
				int absValue = Math.abs(number);
				if (!this.variables.contains(absValue)) {
					this.variables.add(absValue);
				}
			}
		}
	}

	private void populateClauses(String filename) {
		this.clauses = new ArrayList<ArrayList<Integer>>();

		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(filename));
			while (scanner.hasNextLine()) {
				ArrayList<Integer> clause = new ArrayList<Integer>();
				String[] splitLine = scanner.nextLine().split(" ");

				for (String integer : splitLine) {
					clause.add(Integer.parseInt(integer));
				}
				this.clauses.add(clause);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
