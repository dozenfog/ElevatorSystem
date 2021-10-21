package by.issoft.model.elevator;

import by.issoft.enums.Direction;
import by.issoft.enums.ElevatorCondition;
import by.issoft.model.people.Person;
import by.issoft.storage.elevator.ElevatorStorage;
import by.issoft.storage.floor.FloorStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {
    private Elevator elevator;

    @Before
    public void setup() {
        elevator = new Elevator(1, 500, 1, new LinkedBlockingQueue<>(),
                new ElevatorStorage(3), new FloorStorage(5));
        elevator.setGateOpeningTimeInSeconds(0);
        elevator.setManagingPeopleTimeInSeconds(0);
    }

    @Test
    public void testCreateInvalidElevatorNumber() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Elevator(-3, 2, 2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(3), new FloorStorage(1)));
        assertEquals("Elevator number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateInvalidElevatorLiftingCapacity() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Elevator(3, -2, 2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(3), new FloorStorage(1)));
        assertEquals("Elevator lifting capacity should be positive!", e.getMessage());
    }

    @Test
    public void testCreateInvalidElevatorSpeed() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Elevator(3, 2, -2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(3), new FloorStorage(1)));
        assertEquals("Elevator speed should be positive!", e.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateInvalidQueue() {
        new Elevator(3, 2, 2, null,
                new ElevatorStorage(3), new FloorStorage(1));
    }

    private List<ElevatorTask> createUpperTasksForElevator() {
        Person p = new Person(23, 1, 2);
        ElevatorTask task1 = new ElevatorTask(1);
        task1.getListOnEnter().add(p);
        ElevatorTask task2 = new ElevatorTask(2);
        task2.getListOnExit().add(p);
        return Arrays.asList(task1, task2);
    }

    @Test
    public void testSetOpeningTimeInSeconds() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.setGateOpeningTimeInSeconds(2);
        elevator.start();
        try {
            Thread.sleep(1000);
            assertEquals(2, elevator.getGateOpeningTimeInSeconds());
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testSetPeopleManagingInSeconds() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.setManagingPeopleTimeInSeconds(2);
        elevator.start();
        try {
            Thread.sleep(1000);
            assertEquals(2, elevator.getManagingPeopleTimeInSeconds());
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testTakenWeightInKg() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(1000);
            assertEquals(23, elevator.getTakenWeightInKg());
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetCurTask() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(1000);
            assertEquals(elevator.getCurTask().getNeededFloor(), 2);
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testButtonSwitchQueue() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(1000);
            assertEquals(1, elevator.getButtonSwitchQueue().size());
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testRunUp() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(2000);
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertTrue(elevator.isExit());
        assertSame(elevator.getDirection(), Direction.UP);
        assertSame(elevator.getCondition(), ElevatorCondition.WAITING_EMPTY);
        assertTrue(elevator.getTaskQueue().isEmpty());
        assertTrue(elevator.getPeopleRiding().isEmpty());
        assertEquals(elevator.getPositionOfLastCheckpoint().get(), 2);
    }

    private List<ElevatorTask> createLowerTasksForElevator() {
        Person p = new Person(23, 3, 2);
        ElevatorTask task1 = new ElevatorTask(3);
        task1.getListOnEnter().add(p);
        ElevatorTask task2 = new ElevatorTask(2);
        task2.getListOnExit().add(p);
        return Arrays.asList(task1, task2);
    }

    @Test
    public void testRunDown() {
        Elevator elevator1 = new Elevator(2, 300, 2, new LinkedBlockingQueue<>(),
                new ElevatorStorage(2), new FloorStorage(5));
        createLowerTasksForElevator().forEach(elevatorTask -> elevator1.getTaskQueue().add(elevatorTask));
        elevator1.start();
        try {
            Thread.sleep(2000);
            elevator1.stopElevator();
            elevator1.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertTrue(elevator1.isExit());
        assertSame(elevator1.getDirection(), Direction.DOWN);
        assertSame(elevator1.getCondition(), ElevatorCondition.WAITING_EMPTY);
        assertTrue(elevator1.getTaskQueue().isEmpty());
        assertTrue(elevator1.getPeopleRiding().isEmpty());
        assertEquals(elevator1.getPositionOfLastCheckpoint().get(), 2);
    }

    @Test
    public void testUpdateElevatorStatsUp() {
        createUpperTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(2000);
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertTrue(elevator.isExit());
        assertEquals(1, elevator.getElevatorStorage().getElevatorStats().get(0).getNumberOfPeopleTakenUp().get());
        assertEquals(23, elevator.getElevatorStorage().getElevatorStats().get(0).getWeightOfPeopleTakenUp().get());
    }

    @Test
    public void testUpdateElevatorStatsDown() {
        createLowerTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(2000);
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertTrue(elevator.isExit());
        assertEquals(1, elevator.getElevatorStorage().getElevatorStats().get(0).getNumberOfPeopleTakenDown().get());
        assertEquals(23, elevator.getElevatorStorage().getElevatorStats().get(0).getWeightOfPeopleTakenDown().get());
    }

    @Test
    public void testUpdateFloorStats() {
        createLowerTasksForElevator().forEach(elevatorTask -> elevator.getTaskQueue().add(elevatorTask));
        elevator.start();
        try {
            Thread.sleep(2000);
            elevator.stopElevator();
            elevator.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(elevator.getFloorStorage().getFloorStats().get(2).getPeopleLeftForLowerFloors().get(), 1);
        assertEquals(elevator.getFloorStorage().getFloorStats().get(1).getPeopleArrivedFromUpperFloors().get(), 1);
    }
}