package org.example;

import org.example.entities.VulnerabilityScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.resolvers.ScriptDependencyResolver.resolveOrder;

public class App
{
    public static void main( String[] args )
    {
        try {
            // Define scripts with their dependencies (scriptId, list of dependency scriptIds)
            List<VulnerabilityScript> scripts = Arrays.asList(
                    new VulnerabilityScript(1, Arrays.asList(2, 3)),
                    new VulnerabilityScript(2, Arrays.asList(3)),
                    new VulnerabilityScript(3, new ArrayList<>()),
                    new VulnerabilityScript(4, Arrays.asList(1, 2))
            );

            // Resolve the execution order
            List<Integer> executionOrder = resolveOrder(scripts);

            // Print the valid execution order of scripts
            System.out.println("Valid script execution order: " + executionOrder);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
