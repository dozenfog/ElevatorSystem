package by.issoft.model.people;

import by.issoft.enums.Direction;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {
    @Test
    public void testCreateInvalidPersonWeight() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Person(-3, 2, 3));
        assertEquals("Person weight should be positive!", e.getMessage());
    }

    @Test
    public void testCreateInvalidPersonInitialFloor() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Person(3, -2, 3));
        assertEquals("Floor should be positive!", e.getMessage());
    }

    @Test
    public void testCreateInvalidPersonNeededFloor() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Person(3, 2, -3));
        assertEquals("Floor should be positive!", e.getMessage());
    }

    @Test
    public void testCreateInvalidPersonSameFloors() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Person(3, 2, 2));
        assertEquals("Person cannot stay on the same floor!", e.getMessage());
    }

    @Test
    public void testCalculateDirection() {
        Person p1 = new Person(23, 3, 4);
        Person p2 = new Person(33, 5, 1);
        assertEquals(Direction.UP, p1.getDirection());
        assertEquals(Direction.DOWN, p2.getDirection());
    }
}