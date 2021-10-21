package by.issoft.model.building;

import by.issoft.storage.ButtonStorage;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class FloorTest {
    @Test
    public void testCreateFloorInvalidNumber() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Floor(-3, 5, new LinkedBlockingQueue<>(), new ButtonStorage(5)));
        assertEquals("Floor number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateFloorInvalidOverallNumber() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Floor(3, -5, new LinkedBlockingQueue<>(), new ButtonStorage(5)));
        assertEquals("Number of floors should be positive!", e.getMessage());
    }

    @Test
    public void testCreateFloorOutOfBoundsNumber() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Floor(13, 5, new LinkedBlockingQueue<>(), new ButtonStorage(5)));
        assertEquals("Floor number is out of bounds!", e.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateInvalidQueue() {
        new Floor(3, 4, null, new ButtonStorage(1));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullStorage() {
        new Floor(3, 4, new LinkedBlockingQueue<>(), null);
    }

    @Test
    public void testCreateInvalidStorage() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Floor(3, 5, new LinkedBlockingQueue<>(), new ButtonStorage(2)));
        assertEquals("Invalid number of floors!", e.getMessage());
    }

    @Test
    public void testRunFirstFloor() {
        Floor floor = new Floor(1, 3, new LinkedBlockingQueue<>(), new ButtonStorage(3));
        floor.start();
        assertNotNull(floor.getPeopleSpawn());
        assertNotNull(floor.getButtonStorage());
        try {
            Thread.sleep(2000);
            floor.stopFloor();
            floor.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertTrue(floor.getButtonUp().isPushed());
        assertFalse(floor.getButtonDown().isPushed());
        assertFalse(floor.getUpperCrowd().isEmpty());
        assertTrue(floor.getLowerCrowd().isEmpty());
        assertEquals(floor.getSharedCrowdsQueue().size(), 1);
        assertTrue(floor.isExit());
    }

    @Test
    public void testRunLastFloor() {
        Floor floor = new Floor(3, 3, new LinkedBlockingQueue<>(), new ButtonStorage(3));
        floor.start();
        assertNotNull(floor.getPeopleSpawn());
        assertNotNull(floor.getButtonStorage());
        try {
            Thread.sleep(2000);
            floor.stopFloor();
            floor.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertTrue(floor.getButtonDown().isPushed());
        assertFalse(floor.getButtonUp().isPushed());
        assertFalse(floor.getLowerCrowd().isEmpty());
        assertTrue(floor.getUpperCrowd().isEmpty());
        assertEquals(floor.getSharedCrowdsQueue().size(), 1);
        assertTrue(floor.isExit());
    }

    @Test
    public void testRunBothFloors() {
        Floor floor = new Floor(2, 3, new LinkedBlockingQueue<>(), new ButtonStorage(3));
        floor.start();
        assertNotNull(floor.getPeopleSpawn());
        assertNotNull(floor.getButtonStorage());
        try {
            Thread.sleep(2000);
            floor.stopFloor();
            floor.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertTrue(floor.getButtonUp().isPushed());
        assertTrue(floor.getButtonDown().isPushed());
        assertFalse(floor.getUpperCrowd().isEmpty());
        assertFalse(floor.getLowerCrowd().isEmpty());
        assertEquals(floor.getSharedCrowdsQueue().size(), 2);
        assertTrue(floor.isExit());
    }
}