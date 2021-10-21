package by.issoft.model.elevator;

import by.issoft.model.people.Person;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
@ToString
public class ElevatorTask {
    private final int neededFloor;
    private final List<Person> listOnExit;
    private final List<Person> listOnEnter;

    public ElevatorTask(int neededFloor) {
        checkArgument(neededFloor > 0, "Needed floor should be positive!");
        this.neededFloor = neededFloor;
        this.listOnExit = new ArrayList<>();
        this.listOnEnter = new ArrayList<>();
    }
}
