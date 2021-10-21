package by.issoft.model.people;

import by.issoft.enums.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@EqualsAndHashCode
@ToString
public class Crowd {
    private final List<Person> people;
    private final int floor;
    private final Direction direction;
    private final double overallWeight;

    public Crowd(List<Person> people, Direction direction) {
        checkParams(people, direction);
        this.people = people;
        this.floor = people.get(0).getInitialFloor();
        this.direction = direction;
        this.overallWeight = calculateOverallWeight();
    }

    private void checkParams(List<Person> people, Direction direction) {
        checkNotNull(people);
        checkNotNull(direction);
        checkArgument(!people.isEmpty(), "Crowd cannot be empty!");
        checkArgument(people.stream().allMatch(person -> person.getDirection() == direction),
                "Given direction does not match the direction of people!");
    }

    public double calculateOverallWeight() {
        return people.stream().map(Person::getWeightInKg).reduce(Double::sum).orElse(0.0);
    }
}
