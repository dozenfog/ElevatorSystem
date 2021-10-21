package by.issoft.model.people;

import by.issoft.enums.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
@ToString
public class Person {
    private final double weightInKg;
    private final int initialFloor;
    private final int neededFloor;
    private final Direction direction;

    public Person(double weightInKg, int initialFloor, int neededFloor) {
        checkParams(weightInKg, initialFloor, neededFloor);
        this.weightInKg = weightInKg;
        this.initialFloor = initialFloor;
        this.neededFloor = neededFloor;
        this.direction = calculateDirection();
    }

    private void checkParams(double weightInKg, int initialFloor, int neededFloor) {
        checkArgument(weightInKg > 0, "Person weight should be positive!");
        checkArgument(initialFloor > 0, "Floor should be positive!");
        checkArgument(neededFloor > 0, "Floor should be positive!");
        checkArgument(initialFloor != neededFloor, "Person cannot stay on the same floor!");
    }

    private Direction calculateDirection() {
        for (Direction dir : Direction.values()) {
            if (dir.getSign() == Math.signum(neededFloor - initialFloor)) {
                return dir;
            }
        }
        return null;
    }
}
