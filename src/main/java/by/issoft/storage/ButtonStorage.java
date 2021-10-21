package by.issoft.storage;

import by.issoft.enums.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class ButtonStorage {
    private final String filename = "\\src\\main\\java\\by\\issoft\\stats\\buttonStats";
    private final File file;
    private final AtomicIntegerArray upButtons;
    private final AtomicIntegerArray downButtons;

    public ButtonStorage(int floorNumber) {
        checkArgument(floorNumber > 0, "Floor number should be positive!");
        this.file = createFile();
        upButtons = new AtomicIntegerArray(floorNumber + 1);
        downButtons = new AtomicIntegerArray(floorNumber + 1);
    }

    private File createFile() {
        return new File(System.getProperty("user.dir") + filename + ".txt");
    }

    public void writeToButtonStorage(int floor, Direction direction) {
        if (direction == Direction.UP) {
            upButtons.incrementAndGet(floor);
        } else if (direction == Direction.DOWN) {
            downButtons.incrementAndGet(floor);
        }
    }

    public void writeToStats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Button stats\n");
            for (int i = 1; i < upButtons.length(); i++) {
                writer.write("Floor " + i + "| UP pressed: " + upButtons.get(i) + " times| DOWN pressed: "
                        + downButtons.get(i) + " times\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
