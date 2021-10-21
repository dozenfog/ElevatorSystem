package by.issoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {
    UP(1),
    DOWN(-1);

    private final int sign;
}
