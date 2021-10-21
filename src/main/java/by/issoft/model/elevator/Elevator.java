package by.issoft.model.elevator;

import by.issoft.enums.Direction;
import by.issoft.enums.ElevatorCondition;
import by.issoft.model.button.ButtonSwitchMessage;
import by.issoft.model.people.Person;
import by.issoft.storage.elevator.ElevatorStats;
import by.issoft.storage.elevator.ElevatorStorage;
import by.issoft.storage.floor.FloorStats;
import by.issoft.storage.floor.FloorStorage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@Setter
public class Elevator extends Thread {
    private final int number;
    private final double liftingCapacityInKg;
    private final List<Person> peopleRiding;
    private double takenWeightInKg;
    private final double speedInFloorsPerSecond;
    public int gateOpeningTimeInSeconds;
    public int managingPeopleTimeInSeconds;
    private ElevatorCondition condition;
    private Direction direction;
    private AtomicInteger positionOfLastCheckpoint;
    private BlockingQueue<ElevatorTask> taskQueue;
    private ElevatorTask curTask;
    private final BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue;
    private final ElevatorStorage elevatorStorage;
    private final FloorStorage floorStorage;
    private static final Logger logger = LoggerFactory.getLogger("elevatorLogger");
    private boolean exit;

    public Elevator(int number, double liftingCapacityInKg, double speedInFloorsPerSecond,
                    BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue, ElevatorStorage elevatorStorage,
                    FloorStorage floorStorage) {
        checkParams(number, liftingCapacityInKg, speedInFloorsPerSecond, buttonSwitchQueue, elevatorStorage, floorStorage);
        this.number = number;
        this.liftingCapacityInKg = liftingCapacityInKg;
        this.peopleRiding = new ArrayList<>();
        this.takenWeightInKg = 0;
        this.speedInFloorsPerSecond = speedInFloorsPerSecond;
        this.positionOfLastCheckpoint = new AtomicInteger(1);
        this.direction = Direction.UP;
        this.condition = ElevatorCondition.WAITING_EMPTY;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.buttonSwitchQueue = buttonSwitchQueue;
        this.elevatorStorage = elevatorStorage;
        this.floorStorage = floorStorage;
        this.exit = false;
    }

    private void checkParams(int number, double liftingCapacityInKg, double speedInFloorsPerSecond,
                             BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue, ElevatorStorage elevatorStorage,
                             FloorStorage floorStorage) {
        checkArgument(number > 0, "Elevator number should be positive!");
        checkArgument(liftingCapacityInKg > 0, "Elevator lifting capacity should be positive!");
        checkArgument(speedInFloorsPerSecond > 0, "Elevator speed should be positive!");
        checkNotNull(buttonSwitchQueue);
        checkNotNull(elevatorStorage);
        checkNotNull(floorStorage);
    }

    public double recalculateTakenWeight(List<Person> people) {
        return people.stream().map(Person::getWeightInKg).reduce(Double::sum).orElse(0.0);
    }

    @Override
    public void run() {
        try {
            while (!exit) {
                while (!taskQueue.isEmpty()) {
                    curTask = taskQueue.take();
                    if (curTask.getNeededFloor() != positionOfLastCheckpoint.get()) {
                        changeDirectionIfNeeded();
                    }
                    condition = ElevatorCondition.RIDING;
                    while (curTask.getNeededFloor() != positionOfLastCheckpoint.get()) {
                        skipFloor();
                    }
                    visitFloor();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        logger.debug("Elevator " + number + " has been stopped");
    }

    private void changeDirectionIfNeeded() {
        if (Math.signum(curTask.getNeededFloor() - positionOfLastCheckpoint.get()) != direction.getSign()) {
            for (Direction value : Direction.values()) {
                if (value.getSign() != direction.getSign()) {
                    direction = value;
                    break;
                }
            }
            logger.debug("Direction of elevator " + number + " has been changed to " + direction.toString());
        }
    }

    private void visitFloor() throws InterruptedException {
        logger.debug("Elevator " + number + " is on level " + positionOfLastCheckpoint);
        condition = ElevatorCondition.MANAGING_PEOPLE;
        openGates();
        managePeople();
        closeGates();
        changeCondition();
    }

    private void produceButtonMessage() throws InterruptedException {
        ButtonSwitchMessage message =
                new ButtonSwitchMessage(curTask.getNeededFloor(), curTask.getListOnEnter().get(0).getDirection());
        if (!buttonSwitchQueue.contains(message)) {
            buttonSwitchQueue.put(message);
        }
    }

    private void changeCondition() {
        if (taskQueue.isEmpty()) {
            condition = ElevatorCondition.WAITING_EMPTY;
            logger.debug("Elevator " + number + " is waiting empty on level " + positionOfLastCheckpoint);
        } else {
            condition = ElevatorCondition.RIDING;
        }
    }

    private void skipFloor() throws InterruptedException {
        Thread.sleep((long) (1000 / speedInFloorsPerSecond));
        positionOfLastCheckpoint.addAndGet(direction.getSign());

        logger.debug("Elevator " + number + " is riding " + direction.toString() +
                ", currently on floor " + positionOfLastCheckpoint);
    }

    private void openGates() throws InterruptedException {
        logger.debug("Elevator " + number + " is opening gates on level " + positionOfLastCheckpoint);
        Thread.sleep(gateOpeningTimeInSeconds * 1000);
    }

    private void closeGates() throws InterruptedException {
        logger.debug("Elevator " + number + " is closing gates on level " + positionOfLastCheckpoint);
        Thread.sleep(gateOpeningTimeInSeconds * 1000);
    }

    private void managePeople() throws InterruptedException {
        logger.debug("Elevator " + number + " is managing people on level " + positionOfLastCheckpoint);
        peopleRiding.removeAll(curTask.getListOnExit());
        peopleRiding.addAll(curTask.getListOnEnter());
        if (!curTask.getListOnEnter().isEmpty()) {
            produceButtonMessage();
            updateElevatorStats();
        }
        updateFloorStats();
        takenWeightInKg = recalculateTakenWeight(peopleRiding);
        Thread.sleep(managingPeopleTimeInSeconds * 1000);
    }

    private void updateElevatorStats() {
        double newWeight = recalculateTakenWeight(curTask.getListOnEnter());
        Direction direction = curTask.getListOnEnter().get(0).getDirection();
        ElevatorStats curStats = elevatorStorage.getElevatorStats().get(number - 1);
        if (direction == Direction.UP) {
            curStats.getNumberOfPeopleTakenUp().addAndGet(curTask.getListOnEnter().size());
            curStats.getWeightOfPeopleTakenUp().addAndGet(newWeight);
        } else if (direction == Direction.DOWN) {
            curStats.getNumberOfPeopleTakenDown().addAndGet(curTask.getListOnEnter().size());
            curStats.getWeightOfPeopleTakenDown().addAndGet(newWeight);
        }
    }

    private void updateFloorStats() {
        FloorStats floorStats = floorStorage.getFloorStats().get(positionOfLastCheckpoint.get() - 1);
        if (!curTask.getListOnEnter().isEmpty()) {
            int newAmount = curTask.getListOnEnter().size();
            Direction direction = curTask.getListOnEnter().get(0).getDirection();
            if (direction == Direction.DOWN) {
                floorStats.getPeopleLeftForLowerFloors().addAndGet(newAmount);
            } else if (direction == Direction.UP) {
                floorStats.getPeopleLeftForUpperFloors().addAndGet(newAmount);
            }
        }
        if (!curTask.getListOnExit().isEmpty()) {
            int newAmount = curTask.getListOnExit().size();
            Direction direction = curTask.getListOnExit().get(0).getDirection();
            if (direction == Direction.DOWN) {
                floorStats.getPeopleArrivedFromUpperFloors().addAndGet(newAmount);
            } else if (direction == Direction.UP) {
                floorStats.getPeopleArrivedFromLowerFloors().addAndGet(newAmount);
            }
        }
    }

    public void stopElevator() {
        exit = true;
    }
}
