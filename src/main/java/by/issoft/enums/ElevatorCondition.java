package by.issoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ElevatorCondition {
    WAITING_EMPTY(0),
    RIDING(1),
    MANAGING_PEOPLE(2);

    private final int sign;

}
