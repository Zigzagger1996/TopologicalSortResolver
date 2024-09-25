package org.example.resolvers;

import org.example.entities.VulnerabilityScript;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptDependencyResolverTest {

    @Test
    void shouldReturnCorrectExecutionOrderWhenScriptsHaveNoDependencies() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, new ArrayList<>()),
                new VulnerabilityScript(2, new ArrayList<>()),
                new VulnerabilityScript(3, new ArrayList<>())
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.containsAll(Arrays.asList(1, 2, 3)), "executionOrder must contains all scripts");
        assertEquals(3, executionOrder.size(), "executionOrder must contains all scripts");
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenScriptsListContainsOnlyOneScript() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Collections.singletonList(new VulnerabilityScript(1, new ArrayList<>()));

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertEquals(Collections.singletonList(1), executionOrder, "executionOrder must contain only 1 script");
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenScriptsListHaveTwoIndependentChains() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(2)),
                new VulnerabilityScript(2, new ArrayList<>()),
                new VulnerabilityScript(3, Arrays.asList(4)),
                new VulnerabilityScript(4, new ArrayList<>())
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.indexOf(2) < executionOrder.indexOf(1), "script number 2 must come before script number 1");
        assertTrue(executionOrder.indexOf(4) < executionOrder.indexOf(3), "script number 4 must come before script number 3");
    }

    @Test
    void shouldReturnEmptyExecutionOrderWhenScriptsListIsEmpty() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = new ArrayList<>();

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.isEmpty(), "executionOrder must be empty");
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenAllScriptsDependsOnFirst() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(2, 3, 4)),
                new VulnerabilityScript(2, new ArrayList<>()),
                new VulnerabilityScript(3, new ArrayList<>()),
                new VulnerabilityScript(4, new ArrayList<>())
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.indexOf(2) < executionOrder.indexOf(1), "script number 2 must come before script number 1");
        assertTrue(executionOrder.indexOf(3) < executionOrder.indexOf(1), "script number 3 must come before script number 1");
        assertTrue(executionOrder.indexOf(4) < executionOrder.indexOf(1), "script number 4 must come before script number 1");
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenOneScriptDisconnectedFromOthers() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(2)),
                new VulnerabilityScript(2, new ArrayList<>()),
                new VulnerabilityScript(3, new ArrayList<>())
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.containsAll(Arrays.asList(1, 2, 3)), "executionOrder must contains all scripts");
        assertTrue(executionOrder.indexOf(2) < executionOrder.indexOf(1), "script number 2 must come before script number 1");
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenOneScriptHaveMultipleDependencies() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(2, 3)),
                new VulnerabilityScript(2, Arrays.asList(4)),
                new VulnerabilityScript(3, Arrays.asList(4)),
                new VulnerabilityScript(4, new ArrayList<>())
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.indexOf(4) < executionOrder.indexOf(2), "script number 4 must come before script number 2");
        assertTrue(executionOrder.indexOf(4) < executionOrder.indexOf(3), "script number 4 must come before script number 3");
        assertTrue(executionOrder.indexOf(2) < executionOrder.indexOf(1), "script number 2 must come before script number 1");
        assertTrue(executionOrder.indexOf(3) < executionOrder.indexOf(1), "script number 3 must come before script number 1");
    }

    @Test
    void shouldThrowAnExceptionWhenScriptsHaveCircularDependency() {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(2)),
                new VulnerabilityScript(2, Arrays.asList(3)),
                new VulnerabilityScript(3, Arrays.asList(1))
        );

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            ScriptDependencyResolver.resolveOrder(scripts);
        });

        assertEquals("There are circular dependencies in the scripts", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectExecutionOrderWhenMultipleScriptsHaveNoDependencies() throws Exception {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, new ArrayList<>()),
                new VulnerabilityScript(2, new ArrayList<>()),
                new VulnerabilityScript(3, Arrays.asList(1))
        );

        // When
        List<Integer> executionOrder = ScriptDependencyResolver.resolveOrder(scripts);

        // Then
        assertTrue(executionOrder.indexOf(1) < executionOrder.indexOf(3), "script number 3 must come before script number 1");
        assertTrue(executionOrder.contains(2), "executionOrder must contain script number 2");
    }

    @Test
    void shouldThrowAnExceptionWhenScriptsHaveSelfDependency() {
        // Given
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList(1))
        );

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            ScriptDependencyResolver.resolveOrder(scripts);
        });

        assertEquals("There are circular dependencies in the scripts", exception.getMessage());
    }
}
