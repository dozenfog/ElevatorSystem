package by.issoft.model.button;

import by.issoft.enums.Direction;
import by.issoft.model.building.Floor;
import by.issoft.storage.ButtonStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class ButtonSwitchDistributorTest {
    private ButtonSwitchDistributor buttonSwitchDistributor;

    @Before
    public void setup() {
        BlockingQueue<ButtonSwitchMessage> queue = new LinkedBlockingQueue<>();
        Floor floor = new Floor(1, 1, new LinkedBlockingQueue<>(), new ButtonStorage(1));
        buttonSwitchDistributor = new ButtonSwitchDistributor(queue, List.of(floor));
    }

    @Test
    public void testRun() {
        buttonSwitchDistributor.getFloors().get(0).getButtonUp().setPushed(true);
        buttonSwitchDistributor.getFloors().get(0).getButtonDown().setPushed(true);
        buttonSwitchDistributor.getButtonSwitchQueue().add(new ButtonSwitchMessage(1, Direction.UP));
        buttonSwitchDistributor.getButtonSwitchQueue().add(new ButtonSwitchMessage(1, Direction.DOWN));
        buttonSwitchDistributor.start();
        try {
            Thread.sleep(1000);
            buttonSwitchDistributor.interrupt();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertFalse(buttonSwitchDistributor.getFloors().get(0).getButtonUp().isPushed());
        assertFalse(buttonSwitchDistributor.getFloors().get(0).getButtonDown().isPushed());
    }
}