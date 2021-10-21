package by.issoft.model.building;

import by.issoft.enums.Direction;
import by.issoft.generator.PeopleSpawn;
import by.issoft.model.button.Button;
import by.issoft.model.people.Crowd;
import by.issoft.model.people.Person;
import by.issoft.storage.ButtonStorage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@Setter
public class Floor extends Thread {
    private final int number;
    private final int maxNumber;
    private final PeopleSpawn peopleSpawn;
    private final Button buttonUp;
    private final Button buttonDown;
    private final List<Person> upperCrowd;
    private final List<Person> lowerCrowd;
    private static final Random random = new Random();
    private static int maxTimeoutBetweenGenerations = 10;
    private final BlockingQueue<Crowd> sharedCrowdsQueue;
    private final ButtonStorage buttonStorage;
    private static final Logger logger = LoggerFactory.getLogger("floorLogger");
    private boolean exit;

    public Floor(int number, int maxNumber, BlockingQueue<Crowd> sharedCrowdsQueue, ButtonStorage buttonStorage) {
        checkParams(number, maxNumber, sharedCrowdsQueue, buttonStorage);
        this.number = number;
        this.maxNumber = maxNumber;
        this.peopleSpawn = new PeopleSpawn(maxNumber);
        buttonUp = new Button(Direction.UP);
        buttonDown = new Button(Direction.DOWN);
        this.upperCrowd = new ArrayList<>();
        this.lowerCrowd = new ArrayList<>();
        this.sharedCrowdsQueue = sharedCrowdsQueue;
        this.buttonStorage = buttonStorage;
        this.exit = false;
    }

    private void checkParams(int number, int maxNumber, BlockingQueue<Crowd> sharedCrowdsQueue,
                             ButtonStorage buttonStorage) {
        checkArgument(number > 0, "Floor number should be positive!");
        checkArgument(maxNumber > 0, "Number of floors should be positive!");
        checkArgument(maxNumber >= number, "Floor number is out of bounds!");
        checkNotNull(sharedCrowdsQueue);
        checkNotNull(buttonStorage);
        checkArgument(buttonStorage.getUpButtons().length() - 1 == maxNumber,
                "Invalid number of floors!");
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                produce();
                int timeoutInSeconds = random.nextInt(maxTimeoutBetweenGenerations) + 1;
                Thread.sleep(timeoutInSeconds * 1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        logger.debug("Floor " + number + " has been stopped");
    }

    private void produce() throws InterruptedException {
        Crowd spawnedPeople;
        if (number != 1 && !buttonDown.isPushed()) {
            spawnedPeople = peopleSpawn.generateLowerFloorQueue(number);
            logger.debug("Lower crowd has been generated in floor " + number);
            processSpawnedPeople(lowerCrowd, buttonDown, Direction.DOWN, spawnedPeople);
        }
        if (number != maxNumber && !buttonUp.isPushed()) {
            spawnedPeople = peopleSpawn.generateUpperFloorQueue(number);
            logger.debug("Upper crowd has been generated in floor " + number);
            processSpawnedPeople(upperCrowd, buttonUp, Direction.UP, spawnedPeople);
        }
    }

    private void processSpawnedPeople(List<Person> crowd, Button button, Direction direction, Crowd spawnedPeople)
            throws InterruptedException {
        crowd.addAll(spawnedPeople.getPeople());
        button.setPushed(true);
        buttonStorage.writeToButtonStorage(number, direction);
        logger.debug("Button " + direction.toString() + " has been pushed on floor " + number);
        logger.debug("Queue was updated from floor " + number + " with crowd of size "
                + spawnedPeople.getPeople().size() + " riding " + spawnedPeople.getDirection().toString());
        sharedCrowdsQueue.put(spawnedPeople);
    }

    public void stopFloor() {
        exit = true;
    }
}
