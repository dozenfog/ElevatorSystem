package by.issoft.storage.elevator;

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
public class ElevatorStorage {
    private final String filename = "\\src\\main\\java\\by\\issoft\\stats\\elevatorStats";
    private final File file;
    private final List<ElevatorStats> elevatorStats;

    public ElevatorStorage(int elevatorNumber) {
        checkArgument(elevatorNumber > 0, "Elevator amount should be positive!");
        this.file = createFile();
        this.elevatorStats = new ArrayList<>(elevatorNumber);
        createElevatorStats(elevatorNumber);
    }

    private void createElevatorStats(int elevatorNumber) {
        for (int i = 0; i < elevatorNumber; i++) {
            elevatorStats.add(new ElevatorStats(i + 1));
        }
    }

    private File createFile() {
        return new File(System.getProperty("user.dir") + filename + ".txt");
    }

    public void writeToStats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Elevator stats\n");
            for (int i = 0; i < elevatorStats.size(); i++) {
                writer.write("Elevator " + (i + 1) +
                        "| People taken UP: " + elevatorStats.get(i).getNumberOfPeopleTakenUp() +
                        "/ Weight: " + elevatorStats.get(i).getWeightOfPeopleTakenUp() +
                        "| People taken DOWN: " + elevatorStats.get(i).getNumberOfPeopleTakenDown() +
                        "/ Weight: " + elevatorStats.get(i).getWeightOfPeopleTakenDown() + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
