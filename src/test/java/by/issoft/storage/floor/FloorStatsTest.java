package by.issoft.storage.floor;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class FloorStatsTest {
    @Test
    public void testCreateInvalidStats() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new FloorStats(-3));
        assertEquals("Elevator number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidStats() {
        FloorStats floorStats = new FloorStats(3);
        assertEquals(floorStats.getFloorNumber(), 3);
        assertEquals(floorStats.getPeopleArrivedFromLowerFloors().get(), 0);
        assertEquals(floorStats.getPeopleArrivedFromUpperFloors().get(), 0);
        assertEquals(floorStats.getPeopleLeftForLowerFloors().get(), 0);
        assertEquals(floorStats.getPeopleLeftForUpperFloors().get(), 0);
    }

    @Test
    public void testSetStats() {
        FloorStats floorStats = new FloorStats(3);
        floorStats.setPeopleArrivedFromLowerFloors(new AtomicInteger(2));
        floorStats.setPeopleArrivedFromUpperFloors(new AtomicInteger(3));
        floorStats.setPeopleLeftForLowerFloors(new AtomicInteger(4));
        floorStats.setPeopleLeftForUpperFloors(new AtomicInteger(5));

        assertEquals(floorStats.getPeopleLeftForUpperFloors().get(), 5);
        assertEquals(floorStats.getPeopleLeftForLowerFloors().get(), 4);
        assertEquals(floorStats.getPeopleArrivedFromUpperFloors().get(), 3);
        assertEquals(floorStats.getPeopleArrivedFromLowerFloors().get(), 2);
    }
}