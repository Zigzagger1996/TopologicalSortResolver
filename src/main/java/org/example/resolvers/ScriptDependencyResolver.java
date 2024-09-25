package org.example.resolvers;

import org.example.entities.VulnerabilityScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;


public class ScriptDependencyResolver {

    /*
    * Task with resolving each script dependencies and defining in which order we have to run our scrips - is default Topological Sort.
    * To resolve this problem I use Kahn's Algorithm.
    * For each Script (which is representing a single Node in graph) we apply a degree (which is representing a number of dependencies)
    * If Scrip have 0 degree it means that it have no dependencies, so we execute this script first.
    * After we execute our script, we decrease degree of Scripts which was dependent on the Script.
    * In this way, we handle all Scripts one by one.
    * */

    public static List<Integer> resolveOrder(List<VulnerabilityScript> scripts) throws Exception {
        Map<Integer, List<Integer>> graph = new HashMap<>();   // Adjacency list representation of the graph
        Map<Integer, Integer> inDegree = new HashMap<>();      // Stores in-degree of each node (number of dependencies)

        initializeGraphOfScripts(scripts, graph, inDegree);

        List<Integer> sortedOrder = getSortedScriptOrder(graph, inDegree);

        if (sortedOrder.size() != scripts.size()) {
            throw new Exception("There are circular dependencies in the scripts");
        }

        return sortedOrder;
    }

    private static List<Integer> getSortedScriptOrder(Map<Integer, List<Integer>> graph, Map<Integer, Integer> inDegree) {
        // Kahn's Algorithm (based topological sort)
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> sortedOrder = new ArrayList<>();

        // Add all nodes with in-degree 0 (no dependencies) to the queue
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Process nodes in topological order
        while (!queue.isEmpty()) {
            int current = queue.poll();
            sortedOrder.add(current);

            // Decrease the in-degree of neighboring nodes
            for (int neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        return sortedOrder;
    }

    private static void initializeGraphOfScripts(List<VulnerabilityScript> scripts, Map<Integer, List<Integer>> graph, Map<Integer, Integer> inDegree) {

        for (VulnerabilityScript script : scripts) {
            graph.put(script.getScriptId(), new ArrayList<>());
            inDegree.put(script.getScriptId(), 0);  // Initially, every node has in-degree 0
        }

        updateGraphWithDependencies(scripts, graph, inDegree);
    }

    private static void updateGraphWithDependencies(List<VulnerabilityScript> scripts, Map<Integer, List<Integer>> graph, Map<Integer, Integer> inDegree) {
        for (VulnerabilityScript script : scripts) {
            for (int dep : script.getDependencies()) {
                graph.get(dep).add(script.getScriptId());  // Add directed edge from dep to scriptId
                inDegree.put(script.getScriptId(), inDegree.get(script.getScriptId()) + 1);  // Increment in-degree for scriptId
            }
        }
    }
}