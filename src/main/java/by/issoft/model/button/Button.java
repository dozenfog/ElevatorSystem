package by.issoft.model.button;

import by.issoft.enums.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@EqualsAndHashCode
@ToString
public class Button {
    private final Direction direction;
    private volatile boolean pushed;

    public Button(Direction direction) {
        this.direction = direction;
        pushed = false;
    }

    public synchronized void setPushed(boolean pushed) {
        this.pushed = pushed;
    }
}
