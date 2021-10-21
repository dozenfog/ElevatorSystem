package by.issoft.model.button;

import by.issoft.enums.Direction;
import by.issoft.model.building.Floor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class ButtonSwitchDistributor extends Thread {
    private final BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue;
    private final List<Floor> floors;
    private static final Logger logger = LoggerFactory.getLogger("buttonSwitchLogger");

    public ButtonSwitchDistributor(BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue, List<Floor> floors) {
        checkParams(buttonSwitchQueue, floors);
        this.buttonSwitchQueue = buttonSwitchQueue;
        this.floors = floors;
    }

    private void checkParams(BlockingQueue<ButtonSwitchMessage> buttonSwitchQueue, List<Floor> floors) {
        checkNotNull(buttonSwitchQueue, "Button switch queue should not be null");
        checkArgument(!floors.isEmpty());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                ButtonSwitchMessage message = buttonSwitchQueue.take();
                Floor neededFloor = floors.get(message.getFloor() - 1);
                if (!neededFloor.getButtonUp().isPushed() && !neededFloor.getButtonDown().isPushed()) {
                    continue;
                }
                if (message.getButtonDirection() == Direction.UP) {
                    neededFloor.getButtonUp().setPushed(false);

                } else if (message.getButtonDirection() == Direction.DOWN) {
                    neededFloor.getButtonDown().setPushed(false);
                }
                logger.debug("Button was switched off on floor " +
                        (message.getFloor()) + " " + message.getButtonDirection());
            }
        } catch (InterruptedException e) {
            logger.debug("Button switch distributor has been stopped");
        }
    }
}
