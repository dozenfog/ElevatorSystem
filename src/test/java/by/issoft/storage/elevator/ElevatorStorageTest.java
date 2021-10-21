package by.issoft.storage.elevator;

import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorStorageTest {
    @Test
    public void testCreateInvalidStorage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new ElevatorStorage(-3));
        assertEquals("Elevator amount should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidStorage() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(3);
        assertTrue(elevatorStorage.getFile().isFile());
        assertEquals(elevatorStorage.getElevatorStats().size(), 3);
    }

    @Test
    public void testWriteToElevatorStorage() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(3);
        elevatorStorage.getElevatorStats().get(1).setWeightOfPeopleTakenUp(new AtomicDouble(3));
        elevatorStorage.getElevatorStats().get(1).setWeightOfPeopleTakenDown(new AtomicDouble(3));
        elevatorStorage.getElevatorStats().get(1).setNumberOfPeopleTakenUp(new AtomicInteger(3));
        elevatorStorage.getElevatorStats().get(1).setNumberOfPeopleTakenDown(new AtomicInteger(3));

        assertEquals(elevatorStorage.getElevatorStats().get(1).getNumberOfPeopleTakenDown().get(), 3);
        assertEquals(elevatorStorage.getElevatorStats().get(1).getNumberOfPeopleTakenUp().get(), 3);
        assertEquals(elevatorStorage.getElevatorStats().get(1).getWeightOfPeopleTakenDown().get(), 3);
        assertEquals(elevatorStorage.getElevatorStats().get(1).getWeightOfPeopleTakenUp().get(), 3);
    }

    @Test
    public void testWriteToStats() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(3);
        elevatorStorage.writeToStats();
        final String filename = "\\src\\main\\java\\by\\issoft\\stats\\elevatorStats";
        File file = new File(System.getProperty("user.dir") + filename + ".txt");
        assertTrue(file.length() > 0);
    }
}