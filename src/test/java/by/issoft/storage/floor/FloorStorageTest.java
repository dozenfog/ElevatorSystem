package by.issoft.storage.floor;

import by.issoft.storage.elevator.ElevatorStorage;
import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class FloorStorageTest {
    @Test
    public void testCreateInvalidStorage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new FloorStorage(-3));
        assertEquals("Floor number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidStorage() {
        FloorStorage floorStorage = new FloorStorage(3);
        assertTrue(floorStorage.getFile().isFile());
        assertEquals(floorStorage.getFloorStats().size(), 3);
    }

    @Test
    public void testWriteToElevatorStorage() {
        FloorStorage floorStorage = new FloorStorage(3);
        floorStorage.getFloorStats().get(1).setPeopleLeftForUpperFloors(new AtomicInteger(3));
        floorStorage.getFloorStats().get(1).setPeopleLeftForLowerFloors(new AtomicInteger(3));
        floorStorage.getFloorStats().get(1).setPeopleArrivedFromUpperFloors(new AtomicInteger(3));
        floorStorage.getFloorStats().get(1).setPeopleArrivedFromLowerFloors(new AtomicInteger(3));

        assertEquals(floorStorage.getFloorStats().get(1).getPeopleArrivedFromLowerFloors().get(), 3);
        assertEquals(floorStorage.getFloorStats().get(1).getPeopleArrivedFromUpperFloors().get(), 3);
        assertEquals(floorStorage.getFloorStats().get(1).getPeopleLeftForLowerFloors().get(), 3);
        assertEquals(floorStorage.getFloorStats().get(1).getPeopleLeftForUpperFloors().get(), 3);
    }

    @Test
    public void testWriteToStats() {
        FloorStorage floorStorage = new FloorStorage(3);
        floorStorage.writeToStats();
        final String filename = "\\src\\main\\java\\by\\issoft\\stats\\floorStats";
        File file = new File(System.getProperty("user.dir") + filename + ".txt");
        assertTrue(file.length() > 0);
    }
}