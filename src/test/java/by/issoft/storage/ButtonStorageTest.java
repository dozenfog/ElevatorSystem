package by.issoft.storage;

import by.issoft.enums.Direction;
import org.junit.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ButtonStorageTest {
    @Test
    public void testCreateInvalidStorage() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new ButtonStorage(-3));
        assertEquals("Floor number should be positive!", e.getMessage());
    }

    @Test
    public void testCreateValidStorage() {
        ButtonStorage buttonStorage = new ButtonStorage(3);
        assertTrue(buttonStorage.getFile().isFile());
        assertEquals(buttonStorage.getDownButtons().length(), 4);
        assertEquals(buttonStorage.getUpButtons().length(), 4);
    }

    @Test
    public void testWriteToButtonStorage() {
        ButtonStorage buttonStorage = new ButtonStorage(3);
        buttonStorage.writeToButtonStorage(2, Direction.UP);
        buttonStorage.writeToButtonStorage(2, Direction.DOWN);
        assertEquals(buttonStorage.getDownButtons().get(2), 1);
        assertEquals(buttonStorage.getUpButtons().get(2), 1);
    }

    @Test
    public void testWriteToStats() {
        ButtonStorage buttonStorage = new ButtonStorage(3);
        buttonStorage.writeToStats();
        final String filename = "\\src\\main\\java\\by\\issoft\\stats\\buttonStats";
        File file = new File(System.getProperty("user.dir") + filename + ".txt");
        assertTrue(file.length() > 0);
    }
}