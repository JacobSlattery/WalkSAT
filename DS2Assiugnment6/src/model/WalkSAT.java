package model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class WalkSAT {
	
	
	public static final String FILENAME = "SAT_Expression01.txt";
	
	private ArrayList<ArrayList<Integer>> clauses;
	private ArrayList<Integer> variables;
	private HashMap<Integer, Boolean> map;
	private Random random;
	private HashMap<ArrayList<Integer>, Boolean> satisfiedClauseMap;
	
	
	public WalkSAT() {
		this.random = new Random();
		this.satisfiedClauseMap = new HashMap<ArrayList<Integer>, Boolean>();
	}
	
	public boolean run() {
		
		this.populateClauses(FILENAME);
		this.populateVariables();
		this.initializeMap();
		
		boolean end = false;
		int count = 1;
		
		while(!this.doesMapSatisfyClauses()) {
//			System.out.println("Satisfied: " + this.satisfiedClauseMap);
//			System.out.println("Map: " + this.map);
			int key;
			int index = this.random.nextInt(this.variables.size());
			key = this.variables.get(index);
			this.tryFlip(key);
			if (count % 1000000 == 0) {
				System.out.println(String.format("%,d", count));
			}
			if (count % 10000 == 0) {
				this.initializeMap();
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
		
		if (lastCount > this.getSatisfiedCount()) {
			int successAmount = this.satisfiedClauseMap.size() - lastCount;
			if (successAmount > this.random.nextInt(this.map.keySet().size() + 1)) {
				this.flipValueAt(key);
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
		for(ArrayList<Integer> clause : this.clauses) {
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
