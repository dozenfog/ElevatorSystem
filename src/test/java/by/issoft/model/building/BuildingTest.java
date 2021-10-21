package by.issoft.model.building;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingTest {
    @Test
    public void testBuildingCreation() {
        Building a = new Building(2, 3, 4, 5);
        assertEquals(2, a.getFloorAmount());
        assertEquals(3, a.getElevatorNumber());
    }

    @Test
    public void createBuildingInvalidFloorAmount() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Building(-3, 5, 3, 3));
        assertEquals("Amount of floors should be positive!", e.getMessage());
    }

    @Test
    public void createBuildingInvalidElevatorAmount() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                new Building(3, -5, 3, 3));
        assertEquals("Number of elevators should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidBuilding() {
        Building building1 = new Building(2, 4, 256, 3);
        assertNotNull(building1.getButtonSwitchQueue());
        assertNotNull(building1.getSharedCrowdsQueue());
        assertNotNull(building1.getElevatorController());
        assertNotNull(building1.getButtonSwitchDistributor());
        assertNotNull(building1.getButtonStorage());
        assertNotNull(building1.getElevatorStorage());
        assertEquals(2, building1.getFloors().size());
        assertEquals(4, building1.getElevatorController().getElevators().size());
        assertEquals(2, building1.getButtonSwitchDistributor().getFloors().size());
    }

    @Test
    public void testRunBuilding() {
        Building building2 = new Building(2, 3, 200, 3);
        building2.openBuilding();
        assertTrue(building2.getFloors().stream().allMatch(Thread::isAlive));
        assertTrue(building2.getElevatorController().isAlive());
        assertTrue(building2.getButtonSwitchDistributor().isAlive());
        building2.closeBuilding();
    }

    @Test
    public void testTerminateBuilding() {
        Building building = new Building(2, 1, 200, 3);
        building.openBuilding();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        building.closeBuilding();
        assertFalse(building.getElevatorController().isAlive());
        assertFalse(building.getButtonSwitchDistributor().isAlive());
        assertTrue(building.getFloors().stream().noneMatch(Thread::isAlive));
    }
}