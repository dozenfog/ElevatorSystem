package by.issoft;

import by.issoft.model.building.Building;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int floorNumber = 3;
        final int elevatorNumber = 2;
        final int liftingCapacityInKg = 400;
        final double speedInFloorsPerSecond = 1;
        final int workingTimeInSeconds = 10;

        Building building = new Building(floorNumber, elevatorNumber, liftingCapacityInKg, speedInFloorsPerSecond);
        building.openBuilding();
        Thread.sleep(workingTimeInSeconds * 1000);
        //all threads except for elevators will be killed immediately, elevators will finish their tasks
        building.closeBuilding();
    }
}
