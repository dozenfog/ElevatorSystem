package by.issoft.storage.elevator;

import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevatorStatsTest {
    @Test
    public void testCreateInvalidStats() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new ElevatorStats(-3));
        assertEquals("Elevator number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidStats() {
        ElevatorStats elevatorStats = new ElevatorStats(3);
        assertEquals(elevatorStats.getElevatorNumber(), 3);
        assertEquals(elevatorStats.getNumberOfPeopleTakenDown().get(), 0);
        assertEquals(elevatorStats.getNumberOfPeopleTakenUp().get(), 0);
        assertEquals(elevatorStats.getWeightOfPeopleTakenDown().get(), 0.0);
        assertEquals(elevatorStats.getWeightOfPeopleTakenUp().get(), 0.0);
    }

    @Test
    public void testSetStats() {
        ElevatorStats elevatorStats = new ElevatorStats(3);
        elevatorStats.setNumberOfPeopleTakenDown(new AtomicInteger(2));
        elevatorStats.setNumberOfPeopleTakenUp(new AtomicInteger(3));
        elevatorStats.setWeightOfPeopleTakenDown(new AtomicDouble(4));
        elevatorStats.setWeightOfPeopleTakenUp(new AtomicDouble(5));

        assertEquals(elevatorStats.getWeightOfPeopleTakenUp().get(), 5);
        assertEquals(elevatorStats.getWeightOfPeopleTakenDown().get(), 4);
        assertEquals(elevatorStats.getNumberOfPeopleTakenUp().get(), 3);
        assertEquals(elevatorStats.getNumberOfPeopleTakenDown().get(), 2);
    }
}