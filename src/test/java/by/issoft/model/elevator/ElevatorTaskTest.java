package by.issoft.model.elevator;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevatorTaskTest {
    @Test
    public void testInvalidNeededFloor() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new ElevatorTask(-3));
        assertEquals("Needed floor should be positive!", e.getMessage());
    }
}