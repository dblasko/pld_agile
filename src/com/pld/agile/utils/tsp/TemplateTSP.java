/*
 * TemplateTSP
 *
 * Copyright (c) 2021. Hexanomnom
 */

package com.pld.agile.utils.tsp;

import com.pld.agile.utils.observer.Observable;
import com.pld.agile.utils.observer.UpdateType;
import javafx.application.Platform;

import java.util.*;

public abstract class TemplateTSP extends Observable implements TSP {
	private Integer[] bestSol;
	protected Graph g;
	private double bestSolCost;
	private int timeLimit;
	private long startTime;

	public void searchSolution(int timeLimit, Graph g) {

		if (timeLimit <= 0) return;
		startTime = System.currentTimeMillis();
		this.timeLimit = timeLimit;
		this.g = g;
		bestSol = new Integer[g.getNbVertices()];
		Collection<Integer> unvisited = new ArrayList<Integer>(g.getNbVertices()-1);
		for (int i = 1; i < g.getNbVertices(); i++) {
			unvisited.add(i);
		}
		Collection<Integer> visited = new ArrayList<Integer>(g.getNbVertices());
		visited.add(0); // The first visited vertex is 0
		bestSolCost = Integer.MAX_VALUE;
		branchAndBound(0, unvisited, visited, 0);
	}

	public Integer getSolution(int i) {
		if (g != null && i >= 0 && i < g.getNbVertices())
			return bestSol[i];
		return -1;
	}

	public double getSolutionCost() {
		if (g != null)
			return bestSolCost;
		return -1;
	}

	/**
	 * Method that must be defined in TemplateTSP subclasses
	 * @param currentVertex
	 * @param unvisited
	 * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
	 * every vertex in <code>unvisited</code> exactly once, and returning back to vertex <code>0</code>.
	 */
	protected abstract double bound(Integer currentVertex, Collection<Integer> unvisited);

	/**
	 * Method that must be defined in TemplateTSP subclasses
	 * @param currentVertex
	 * @param unvisited
	 * @param g
	 * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
	 */
	protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g);

	/**
	 * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
	 * @param currentVertex the last visited vertex
	 * @param unvisited the set of vertex that have not yet been visited
	 * @param visited the sequence of vertices that have been already visited (including currentVertex)
	 * @param currentCost the cost of the path corresponding to <code>visited</code>
	 */
	private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
			Collection<Integer> visited, double currentCost){
		if (System.currentTimeMillis() - startTime > timeLimit) return;
	    if (unvisited.size() == 0){
	    	if (g.isArc(currentVertex,0)){
	    		if (currentCost+g.getPath(currentVertex,0).getLength() < bestSolCost){
	    			visited.toArray(bestSol);
	    			bestSolCost = currentCost+g.getPath(currentVertex,0).getLength();
					Platform.runLater(() -> notifyObservers(UpdateType.INTERMEDIARY_TSP));
	    		}
	    	}
	    } else if (currentCost+bound(currentVertex,unvisited) < bestSolCost){
	        Iterator<Integer> it = iterator(currentVertex, unvisited, g);
	        while (it.hasNext()){
	        	Integer nextVertex = it.next();
	        	visited.add(nextVertex);
	            unvisited.remove(nextVertex);
	            branchAndBound(nextVertex, unvisited, visited,
	            		currentCost+g.getPath(currentVertex, nextVertex).getLength());
	            visited.remove(nextVertex);
	            unvisited.add(nextVertex);

	        }
	    }
	}
}
