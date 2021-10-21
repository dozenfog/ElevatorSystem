package by.issoft.generator;

import by.issoft.enums.Direction;
import by.issoft.model.people.Crowd;
import by.issoft.model.people.Person;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Setter
public class PeopleSpawn {
    private static final Random random = new Random();
    private static final int MAX_GENERATED_PEOPLE_NUMBER = 10;
    public static final double MAX_PEOPLE_WEIGHT = 100;
    public static final double MIN_PEOPLE_WEIGHT = 10;   //1 y.o. babies can stand already and in average they weight 10kg
    private final int maxFloor;

    public PeopleSpawn(int maxFloor) {
        checkArgument(maxFloor > 0, "Max floor should be positive!");
        this.maxFloor = maxFloor;
    }

    public Crowd generateUpperFloorQueue(int floor) {
        checkUpperFloorParam(floor);
        List<Person> upperCrowd = new ArrayList<>();
        int crowdSize = random.nextInt(MAX_GENERATED_PEOPLE_NUMBER) + 1;
        for (int i = 0; i < crowdSize; i++) {
            double weightInKg = random.nextDouble() * (MAX_PEOPLE_WEIGHT - MIN_PEOPLE_WEIGHT) + MIN_PEOPLE_WEIGHT;
            int neededFloor;
            do {
                neededFloor = random.nextInt(maxFloor) + 1;
            } while (neededFloor <= floor);
            upperCrowd.add(new Person(weightInKg, floor, neededFloor));
        }
        return new Crowd(upperCrowd, Direction.UP);
    }

    private void checkUpperFloorParam(int floor) {
        checkArgument(floor > 0, "Floor for generated upper crowd should be positive!");
        checkArgument(floor < maxFloor, "Impossible to generate upper queue for final floor!");
    }

    public Crowd generateLowerFloorQueue(int floor) {
        checkLowerFloorParam(floor);
        List<Person> lowerCrowd = new ArrayList<>();
        int crowdSize = random.nextInt(MAX_GENERATED_PEOPLE_NUMBER) + 1;
        for (int i = 0; i < crowdSize; i++) {
            double weightInKg = random.nextDouble() * (MAX_PEOPLE_WEIGHT - MIN_PEOPLE_WEIGHT) + MIN_PEOPLE_WEIGHT;
            int neededFloor;
            do {
                neededFloor = random.nextInt(maxFloor) + 1;
            } while (neededFloor >= floor);
            lowerCrowd.add(new Person(weightInKg, floor, neededFloor));
        }
        return new Crowd(lowerCrowd, Direction.DOWN);
    }

    private void checkLowerFloorParam(int floor) {
        checkArgument(floor > 0, "Floor for generated lower crowd should be positive!");
        checkArgument(floor != 1, "Impossible to generate lower queue for first floor!");
    }
}
