package by.issoft.model.building;

import by.issoft.controller.ElevatorController;
import by.issoft.model.button.ButtonSwitchDistributor;
import by.issoft.model.button.ButtonSwitchMessage;
import by.issoft.model.elevator.Elevator;
import by.issoft.model.people.Crowd;
import by.issoft.storage.ButtonStorage;
import by.issoft.storage.elevator.ElevatorStorage;
import by.issoft.storage.floor.FloorStorage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class Building {
    private final int floorAmount;
    private final int elevatorNumber;
    private final ElevatorController elevatorController;
    private final ButtonSwitchDistributor buttonSwitchDistributor;
    private final List<Floor> floors;
    private final BlockingQueue<Crowd> sharedCrowdsQueue;
    private final BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue;
    private final ButtonStorage buttonStorage;
    private final ElevatorStorage elevatorStorage;
    private final FloorStorage floorStorage;

    public Building(int floorAmount, int elevatorNumber, int liftingCapacityInKg, double speedInFloorsPerSecond) {
        checkParams(floorAmount, elevatorNumber);
        this.floorAmount = floorAmount;
        this.elevatorNumber = elevatorNumber;

        this.sharedCrowdsQueue = new LinkedBlockingQueue<>(2 * floorAmount);
        this.buttonSwitchQueue = new LinkedBlockingQueue<>(2 * floorAmount);

        this.buttonStorage = new ButtonStorage(floorAmount);
        this.elevatorStorage = new ElevatorStorage(elevatorNumber);
        this.floorStorage = new FloorStorage(floorAmount);

        this.floors = Collections.synchronizedList(new ArrayList<>(floorAmount));
        initFloors();

        this.buttonSwitchDistributor = new ButtonSwitchDistributor(buttonSwitchQueue, floors);

        List<Elevator> elevators = new ArrayList<>(elevatorNumber);
        initElevators(elevators, liftingCapacityInKg, speedInFloorsPerSecond);
        elevatorController = new ElevatorController(sharedCrowdsQueue, elevators);
    }

    private void checkParams(int floorAmount, int elevatorNumber) {
        checkArgument(floorAmount > 0, "Amount of floors should be positive!");
        checkArgument(elevatorNumber > 0, "Number of elevators should be positive!");
    }

    private void initFloors() {
        for (int i = 1; i <= floorAmount; i++) {
            Floor floorThread = new Floor(i, floorAmount, sharedCrowdsQueue, buttonStorage);
            floors.add(floorThread);
            floorThread.setName("Floor " + i);
        }
    }

    private void initElevators(List<Elevator> elevators, int liftingCapacityInKg, double speedInFloorsPerSecond) {
        for (int i = 1; i <= elevatorNumber; i++) {
            Elevator elevatorThread = new Elevator(i, liftingCapacityInKg, speedInFloorsPerSecond, buttonSwitchQueue,
                    elevatorStorage, floorStorage);
            elevators.add(elevatorThread);
            elevatorThread.setName("Elevator " + i);
        }
    }

    public void openBuilding() {
        this.floors.forEach(Thread::start);
        buttonSwitchDistributor.setName("SwitchDistributor");
        buttonSwitchDistributor.start();
        elevatorController.setName("ElevatorController");
        elevatorController.start();
    }

    public void closeBuilding() {
        try {
            this.floors.forEach(Floor::stopFloor);
            elevatorController.stopController();
            elevatorController.join();
            buttonSwitchDistributor.interrupt();
            writeToStats();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeToStats() {
        buttonStorage.writeToStats();
        elevatorStorage.writeToStats();
        floorStorage.writeToStats();
    }
}
