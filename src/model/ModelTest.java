package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    private Model model;

    @BeforeEach
    void setUp() {
        model = new Model();
        model.loadDictionary();
    }

    @Test
    void testLoadDictionary() {
        assertFalse(model.getDictionary().isEmpty());
    }

    @Test
    void testSetStartAndTargetWords() {
        model.setStartAndTargetWords("test", "word");
        assertEquals("test", model.getStartWord());
        assertEquals("word", model.getTargetWord());
    }

    @Test
    void testSetRandomStartAndTargetWords() {
        model.setRandomWordFlag(true);
        model.setStartAndTargetWords(null, null);
        assertNotNull(model.getStartWord());
        assertNotNull(model.getTargetWord());
    }

    @Test
    void testIsValidWord() {
        model.setStartAndTargetWords("test", "word");
        assertTrue(model.isValidWord("test"));
        assertFalse(model.isValidWord("abc"));
    }

    @Test
    void testUpdateCurrentInput() {
        model.setStartAndTargetWords("test", "word");
        model.updateCurrentInput("test");
        assertEquals("test", model.getCurrentInput());
    }

    @Test
    void testCheckWin() {
        model.setStartAndTargetWords("test", "word");
        model.updateCurrentInput("word");
        assertTrue(model.checkWin());
    }

    @Test
    void testGetErrorMessage() {
        model.setStartAndTargetWords("test", "word");
        model.setShowErrorFlag(true);
        model.updateCurrentInput("abcd");
        assertEquals("Invalid Word!", model.getErrorMessage());
    }

    @Test
    void testGetPath() {
        model.setStartAndTargetWords("test", "word");
        model.setShowPathFlag(true);
        assertNotNull(model.getPath());
    }

    @Test
    void testResetGame() {
        model.setStartAndTargetWords("test", "word");
        model.updateCurrentInput("test");
        model.resetGame();
        assertNull(model.getCurrentInput());
        assertTrue(model.getHistoryWords().isEmpty());
    }

    @Test
    void testRemoveLastInput() {
        model.setStartAndTargetWords("test", "word");
        model.updateCurrentInput("test");
        model.removeLastInput();
        assertTrue(model.getHistoryWords().isEmpty());
    }
}