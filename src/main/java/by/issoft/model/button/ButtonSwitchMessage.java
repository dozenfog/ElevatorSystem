package by.issoft.model.button;

import by.issoft.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ButtonSwitchMessage {
    private final int floor;
    private final Direction buttonDirection;
}
