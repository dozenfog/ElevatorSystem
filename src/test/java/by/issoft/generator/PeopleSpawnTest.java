package by.issoft.generator;

import by.issoft.model.people.Crowd;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeopleSpawnTest {
    private final PeopleSpawn peopleSpawn = new PeopleSpawn(5);

    @Test
    public void testCreatePeopleSpawnInvalidFloor() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new PeopleSpawn(-3));
        assertEquals("Max floor should be positive!", e.getMessage());
    }

    @Test
    public void testGenerateInvalidUpperFloorQueue() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> peopleSpawn.generateUpperFloorQueue(-3));
        assertEquals("Floor for generated upper crowd should be positive!", e.getMessage());
    }

    @Test
    public void testGenerateInvalidLowerFloorQueue() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> peopleSpawn.generateLowerFloorQueue(-3));
        assertEquals("Floor for generated lower crowd should be positive!", e.getMessage());
    }

    @Test
    public void testGenerateUpperFloorQueueFinalFloor() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> peopleSpawn.generateUpperFloorQueue(5));
        assertEquals("Impossible to generate upper queue for final floor!", e.getMessage());
    }

    @Test
    public void testGenerateLowerFloorQueueFirstFloor() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> peopleSpawn.generateLowerFloorQueue(1));
        assertEquals("Impossible to generate lower queue for first floor!", e.getMessage());
    }

    @Test
    public void testGenerateValidUpperQueue() {
        Crowd people = peopleSpawn.generateUpperFloorQueue(3);
        assertNotNull(people);
        assertFalse(people.getPeople().isEmpty());
        people.getPeople().forEach(person -> {
            assertTrue(person.getWeightInKg() <= PeopleSpawn.MAX_PEOPLE_WEIGHT);
            assertTrue(person.getWeightInKg() >= PeopleSpawn.MIN_PEOPLE_WEIGHT);
            assertEquals(person.getInitialFloor(), 3);
            assertTrue(person.getNeededFloor() > 3 && person.getNeededFloor() <= 5);
        });
    }

    @Test
    public void testGenerateValidLowerQueue() {
        Crowd people = peopleSpawn.generateLowerFloorQueue(3);
        assertNotNull(people);
        assertFalse(people.getPeople().isEmpty());
        people.getPeople().forEach(person -> {
            assertTrue(person.getWeightInKg() <= PeopleSpawn.MAX_PEOPLE_WEIGHT);
            assertTrue(person.getWeightInKg() >= PeopleSpawn.MIN_PEOPLE_WEIGHT);
            assertEquals(person.getInitialFloor(), 3);
            assertTrue(person.getNeededFloor() < 3 && person.getNeededFloor() >= 1);
        });
    }
}