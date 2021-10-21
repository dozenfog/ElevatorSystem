package by.issoft.storage.elevator;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Setter
public class ElevatorStats {
    private final int elevatorNumber;
    private AtomicInteger numberOfPeopleTakenUp;
    private AtomicInteger numberOfPeopleTakenDown;
    private AtomicDouble weightOfPeopleTakenUp;
    private AtomicDouble weightOfPeopleTakenDown;

    public ElevatorStats(int elevatorNumber) {
        checkArgument(elevatorNumber > 0, "Elevator number should be positive!");
        this.elevatorNumber = elevatorNumber;
        this.numberOfPeopleTakenUp = new AtomicInteger();
        this.numberOfPeopleTakenDown = new AtomicInteger();
        this.weightOfPeopleTakenUp = new AtomicDouble();
        this.weightOfPeopleTakenDown = new AtomicDouble();
    }
}
