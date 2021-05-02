package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.FindState;
import game.FleeState;
import game.Node;
import game.NodeStatus;
import game.SewerDiver;

public class DiverMin extends SewerDiver {

	/** Get to the ring in as few steps as possible. Once you get there, <br>
	 * you must return from this function in order to pick<br>
	 * it up. If you continue to move after finding the ring rather <br>
	 * than returning, it will not count.<br>
	 * If you return from this function while not standing on top of the ring, <br>
	 * it will count as a failure.
	 *
	 * There is no limit to how many steps you can take, but you will receive<br>
	 * a score bonus multiplier for finding the ring in fewer steps.
	 *
	 * At every step, you know only your current tile's ID and the ID of all<br>
	 * open neighbor tiles, as well as the distance to the ring at each of <br>
	 * these tiles (ignoring walls and obstacles).
	 *
	 * In order to get information about the current state, use functions<br>
	 * currentLocation(), neighbors(), and distanceToRing() in state.<br>
	 * You know you are standing on the ring when distanceToRing() is 0.
	 *
	 * Use function moveTo(long id) in state to move to a neighboring<br>
	 * tile by its ID. Doing this will change state to reflect your new position.
	 *
	 * A suggested first implementation that will always find the ring, but <br>
	 * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
	 * Some modification is necessary to make the search better, in general. */
	@Override
	public void find(FindState state) {
		// TODO : Find the ring and return.
		// DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
		// Instead, write your method elsewhere, with a good specification,
		// and call it from this one.

		HashSet<Long> visited= new HashSet<>();
		dfsWalk(state, visited);
	}

	/** Flee the sewer system before the steps are all used, trying to <br>
	 * collect as many coins as possible along the way. Your solution must ALWAYS <br>
	 * get out before the steps are all used, and this should be prioritized above<br>
	 * collecting coins.
	 *
	 * You now have access to the entire underlying graph, which can be accessed<br>
	 * through FleeState. currentNode() and getExit() will return Node objects<br>
	 * of interest, and getNodes() will return a collection of all nodes on the graph.
	 *
	 * You have to get out of the sewer system in the number of steps given by<br>
	 * getStepsRemaining(); for each move along an edge, this number is <br>
	 * decremented by the weight of the edge taken.
	 *
	 * Use moveTo(n) to move to a node n that is adjacent to the current node.<br>
	 * When n is moved-to, coins on node n are automatically picked up.
	 *
	 * You must return from this function while standing at the exit. Failing <br>
	 * to do so before steps run out or returning from the wrong node will be<br>
	 * considered a failed run.
	 *
	 * Initially, there are enough steps to get from the starting point to the<br>
	 * exit using the shortest path, although this will not collect many coins.<br>
	 * For this reason, a good starting solution is to use the shortest path to<br>
	 * the exit. */
	@Override
	public void flee(FleeState state) {
		// TODO: Get out of the sewer system before the steps are used up.
		// DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
		// with a good specification, and call it from this one.

		HashSet<Node> visited= new HashSet<>();
		fleePath(state, visited);
		move(state, state.getExit(), visited);
	}

	public static void dfsWalk(FindState state, HashSet<Long> visited) {
		if (state.distanceToRing() == 0) return;
		long u= state.currentLocation();
		visited.add(u);
		long x= u;
		int dist= Integer.MAX_VALUE;
		for (NodeStatus n : state.neighbors()) {
			long nId= n.getId();
			int nD= n.getDistanceToTarget();
			if (!visited.contains(nId)) {
				if (nD <= dist) {
					dist= nD;
					x= nId;
				} else {
					visited.add(nId);
				}
			}
		}
		state.moveTo(x);
		dfsWalk(state, visited);
		if (state.distanceToRing() == 0) return;
		state.moveTo(u);
	}

	public static void dfsWalk1(FindState state, HashSet<Long> visited) {
		if (state.distanceToRing() == 0) return;
		long u= state.currentLocation();
		visited.add(u);
		for (NodeStatus n : state.neighbors()) {
			long nId= n.getId();
			if (!visited.contains(nId)) {
				state.moveTo(nId);
				dfsWalk(state, visited);
				if (state.distanceToRing() == 0) return;
				state.moveTo(u);
			}
		}
	}

	public static void fleePath1(FleeState state, HashSet<Node> visited) {
		if (state.stepsLeft() == 0) return;

	}

	public static void fleePath2(FleeState state, HashSet<Node> visited) {
		move(state, state.getExit(), visited);
	}

	public static void fleePath(FleeState state, HashSet<Node> visited) {
		if (state.stepsLeft() == Paths.shortest(state.currentNode(), state.getExit()).size()) return;

		visited.add(state.currentNode());
		Node e= state.getExit();
		HashMap<Node, List<Node>> paths= Paths.shortestAll(e, state.allNodes());
		HashMap<Integer, Node> vals= coinVals(state);
		ArrayList<Integer> keys= keys(vals);
		for (int k : keys) {
			Node u= state.currentNode();
			Node n= vals.get(k);
			if (!visited.contains(n)) {
				if ((Paths.shortest(u, n).size() + paths.get(n).size()) - 4 < state.stepsLeft()) {
					visited.add(n);
					move(state, n, visited);
					// keys.remove(k);
					fleePath(state, visited);
				}

			}
			return;
		}
		return;
	}

	public static void move(FleeState state, Node dest, HashSet<Node> visited) {
		List<Node> path= Paths.shortest(state.currentNode(), dest);
		path.remove(0);
		for (Node n : path) {
			state.moveTo(n);
			// visited.add(n);
		}
	}

	public static HashMap<Integer, Node> coinVals(FleeState state) {
		HashMap<Integer, Node> vals= new HashMap<>();
		for (Node n : state.allNodes()) {
			vals.put(n.getTile().coins(), n);
		}
		return vals;
	}

	public static ArrayList<Integer> keys(HashMap<Integer, Node> vals) {
		Set<Integer> keySet= vals.keySet();
		ArrayList<Integer> keys= new ArrayList<Integer>(keySet);
		Collections.sort(keys);
		return keys;
	}

	public static HashMap<Node, Integer> coinVals1(FleeState state) {
		HashMap<Node, Integer> vals= new HashMap<>();
		for (Node n : state.allNodes()) {
			vals.put(n, n.getTile().coins());
		}
		return vals;
	}
}
