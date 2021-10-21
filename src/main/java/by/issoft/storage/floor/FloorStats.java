package by.issoft.storage.floor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Setter
public class FloorStats {
    private final int floorNumber;
    private AtomicInteger peopleArrivedFromUpperFloors;
    private AtomicInteger peopleArrivedFromLowerFloors;
    private AtomicInteger peopleLeftForUpperFloors;
    private AtomicInteger peopleLeftForLowerFloors;

    public FloorStats(int floorNumber) {
        checkArgument(floorNumber > 0, "Elevator number should be positive!");
        this.floorNumber = floorNumber;
        this.peopleArrivedFromUpperFloors = new AtomicInteger();
        this.peopleArrivedFromLowerFloors = new AtomicInteger();
        this.peopleLeftForUpperFloors = new AtomicInteger();
        this.peopleLeftForLowerFloors = new AtomicInteger();
    }
}
