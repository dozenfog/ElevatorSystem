package by.issoft.storage.floor;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@Setter
public class FloorStorage {
    private final String filename = "\\src\\main\\java\\by\\issoft\\stats\\floorStats";
    private final File file;
    private final List<FloorStats> floorStats;

    public FloorStorage(int floorNumber) {
        checkArgument(floorNumber > 0, "Floor number should be positive!");
        this.file = createFile();
        this.floorStats = new ArrayList<>(floorNumber);
        createFloorStats(floorNumber);
    }

    private void createFloorStats(int floorNumber) {
        for (int i = 0; i < floorNumber; i++) {
            floorStats.add(new FloorStats(i + 1));
        }
    }

    private File createFile() {
        return new File(System.getProperty("user.dir") + filename + ".txt");
    }

    public void writeToStats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Floor stats\n");
            for (int i = 0; i < floorStats.size(); i++) {
                writer.write("Floor " + (i + 1) +
                        "| People arrived from UP: " + floorStats.get(i).getPeopleArrivedFromUpperFloors() +
                        "| People arrived from DOWN: " + floorStats.get(i).getPeopleArrivedFromLowerFloors() +
                        "| People left for UP: " + floorStats.get(i).getPeopleLeftForUpperFloors() +
                        "| People left for DOWN: " + floorStats.get(i).getPeopleLeftForLowerFloors() + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
