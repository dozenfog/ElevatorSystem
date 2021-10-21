package by.issoft.controller;

import by.issoft.enums.Direction;
import by.issoft.enums.ElevatorCondition;
import by.issoft.model.elevator.Elevator;
import by.issoft.model.people.Crowd;
import by.issoft.model.people.Person;
import by.issoft.storage.elevator.ElevatorStorage;
import by.issoft.storage.floor.FloorStorage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorControllerTest {
    @Test(expected = NullPointerException.class)
    public void createInvalidControllerQueue() {
        new ElevatorController(null,
                List.of(new Elevator(1, 3, 2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(3), new FloorStorage(1))));
    }

    @Test(expected = NullPointerException.class)
    public void createInvalidControllerNullElevators() {
        new ElevatorController(new LinkedBlockingQueue<>(), null);
    }

    @Test
    public void createInvalidControllerNoElevators() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new ElevatorController(new LinkedBlockingQueue<>(), new ArrayList<>()));
        assertEquals("There should be at least 1 elevator!", e.getMessage());
    }

    @Test
    public void testCreateControllerInvalidGateOpeningTime() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new ElevatorController(new LinkedBlockingQueue<>(),
                        List.of(new Elevator(1, 3, 2,
                                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1))),
                        -1, 1));
        assertEquals("Gate opening time should be positive!", e.getMessage());
    }

    @Test
    public void testCreateControllerInvalidManagingPeopleTime() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new ElevatorController(new LinkedBlockingQueue<>(),
                        List.of(new Elevator(1, 3, 2,
                                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1))),
                        1, -1));
        assertEquals("Managing people time should be positive!", e.getMessage());
    }

    @Test
    public void testCreateElevatorCustomParams() {
        ElevatorController elevatorController = new ElevatorController(new LinkedBlockingQueue<>(),
                List.of(new Elevator(1, 3, 2,
                        new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1))),
                1, 1);

        assertTrue(elevatorController.getElevators().stream()
                .allMatch(elevator ->
                        elevator.getGateOpeningTimeInSeconds() == 1 && elevator.getManagingPeopleTimeInSeconds() == 1));
    }

    @Test
    public void testRunOnlyOneElevatorOneTask() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(23, 1, 2));
        people.add(new Person(24, 1, 2));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Elevator> elevators = List.of(new Elevator(1, 300, 2,
                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1)));
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        assertEquals(1, elevatorController.getSharedCrowdsQueue().size());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(1, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        elevatorController.stopController();
        assertTrue(elevatorController.isExit());
    }

    @Test
    public void testRunOnlyOneElevatorOneTaskTerminate() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(23, 1, 2));
        people.add(new Person(24, 1, 2));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Elevator> elevators = List.of(new Elevator(1, 300, 2,
                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1)));
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(1, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        elevatorController.stopController();
        assertTrue(elevatorController.isExit());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(0, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertEquals(ElevatorCondition.MANAGING_PEOPLE, elevatorController.getElevators().get(0).getCondition());
    }

    @Test
    public void testRunOnlyOneElevatorMultipleTasks() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(23, 3, 4));
        people.add(new Person(73, 3, 5));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Elevator> elevators = List.of(new Elevator(1, 300, 0.2,
                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(1)));
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(1000);
            List<Person> people1 = new ArrayList<>();
            people1.add(new Person(94, 3, 4));
            sharedCrowdsQueue.add(new Crowd(people1, Direction.UP));
            Thread.sleep(3000);
            elevatorController.stopController();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(2, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertEquals(2, elevatorController.getElevators().get(0).getTaskQueue().peek().getListOnExit().size());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        assertTrue(elevatorController.isExit());
    }

    private List<Elevator> createMultipleElevators() {
        return List.of(new Elevator(1, 300, 2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(2), new FloorStorage(5)),
                new Elevator(2, 300, 2, new LinkedBlockingQueue<>(),
                        new ElevatorStorage(2), new FloorStorage(5)));
    }

    @Test
    public void testRunMultipleElevatorsOneTask() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(73, 1, 2));
        people.add(new Person(74, 1, 2));
        people.add(new Person(94, 1, 2));
        people.add(new Person(84, 1, 2));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Elevator> elevators = createMultipleElevators();
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(3000);
            elevatorController.stopController();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(1, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertEquals(1, elevatorController.getElevators().get(1).getTaskQueue().size());
        assertEquals(3, elevatorController.getElevators().get(0).getTaskQueue().peek().getListOnExit().size());
        assertEquals(1, elevatorController.getElevators().get(1).getTaskQueue().peek().getListOnExit().size());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        assertTrue(elevatorController.isExit());
    }

   /* @Test
    public void testRunMultipleElevatorsTwoConsecutiveTasks() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(73, 1, 2));
        people.add(new Person(74, 1, 2));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Elevator> elevators = createMultipleElevators();
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(3000);
            elevatorController.stopController();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(1, elevatorController.getElevators().get(0).getTaskQueue().size());
        assertEquals(2, elevatorController.getElevators().get(0).getTaskQueue().peek().getListOnExit().size());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        assertTrue(elevatorController.isExit());
    }*/

    @Test
    public void testRunMultipleElevators() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(73, 1, 2));
        people.add(new Person(74, 1, 5));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        List<Person> people1 = new ArrayList<>();
        people1.add(new Person(73, 2, 4));
        people1.add(new Person(74, 2, 3));
        sharedCrowdsQueue.add(new Crowd(people1, Direction.UP));
        List<Elevator> elevators = createMultipleElevators();
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(3000);
            elevatorController.stopController();
            elevatorController.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertFalse(elevatorController.isAlive());
        assertTrue(elevatorController.getSharedCrowdsQueue().isEmpty());
        assertTrue(elevatorController.isExit());
    }

    @Test
    public void testRunWaitingForElevatorTasks() {
        BlockingQueue<Crowd> sharedCrowdsQueue = new LinkedBlockingQueue<>();
        List<Person> people = new ArrayList<>();
        people.add(new Person(23, 1, 5));
        List<Person> people1 = new ArrayList<>();
        people1.add(new Person(24, 3, 5));
        sharedCrowdsQueue.add(new Crowd(people, Direction.UP));
        sharedCrowdsQueue.add(new Crowd(people1, Direction.UP));
        List<Elevator> elevators = List.of(new Elevator(1, 300, 2,
                new LinkedBlockingQueue<>(), new ElevatorStorage(3), new FloorStorage(5)));
        ElevatorController elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
        elevatorController.start();
        try {
            Thread.sleep(3000);
            List<Person> people2 = new ArrayList<>();
            people2.add(new Person(23, 1, 2));
            sharedCrowdsQueue.add(new Crowd(people2, Direction.UP));
            assertEquals(1, elevatorController.getSharedCrowdsQueue().size());
            Thread.sleep(2000);
            assertEquals(0, elevatorController.getSharedCrowdsQueue().size());
            assertTrue(elevatorController.getElevators().stream()
                    .allMatch(elevator -> elevator.getTaskQueue().size() == 1));
            elevatorController.stopController();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}