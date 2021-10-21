package by.issoft.model.people;

import by.issoft.enums.Direction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrowdTest {
    @Test(expected = NullPointerException.class)
    public void testCreateNullCrowd() {
        new Crowd(null, Direction.UP);
    }

    @Test
    public void testCreateEmptyCrowd() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new Crowd(new ArrayList<>(), Direction.UP));
        assertEquals("Crowd cannot be empty!", e.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullDirection() {
        new Crowd(List.of(new Person(23, 4, 5)), null);
    }

    @Test
    public void testCreateInvalidDirection() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new Crowd(List.of(new Person(23, 5, 3)), Direction.UP));
        assertEquals("Given direction does not match the direction of people!", e.getMessage());
    }

    @Test
    public void testCalculateOverallWeight() {
        Crowd crowd = new Crowd(List.of(
                new Person(23, 4, 5),
                new Person(34, 5, 6)),
                Direction.UP);
        assertEquals(57, crowd.calculateOverallWeight());
    }

    @Test
    public void testGetCrowdParams() {
        Crowd crowd = new Crowd(List.of(
                new Person(23, 1, 3),
                new Person(45, 1, 3)), Direction.UP);
        assertEquals(crowd.getDirection(), Direction.UP);
        assertEquals(crowd.getOverallWeight(), 68);
        assertEquals(crowd.getFloor(), 1);
        assertEquals(crowd.getPeople().size(), 2);
    }

    @Test
    public void testEqualsHashCode() {
        Crowd crowd = new Crowd(List.of(
                new Person(23, 1, 3),
                new Person(45, 1, 3)), Direction.UP);
        Crowd crowd1 = new Crowd(List.of(
                new Person(23, 1, 3),
                new Person(45, 1, 3)), Direction.UP);
        assertEquals(crowd1, crowd);
        assertEquals(crowd1.hashCode(), crowd.hashCode());
    }
}