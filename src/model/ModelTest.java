package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Model covering three key scenarios.
 */
public class ModelTest {
    private Model model;

    @BeforeEach
    public void setup() {
        model = new Model();
        // Load a small dictionary for predictable behavior
        model.loadDictionary();
    }

    /*
      requires model.getDictionary().contains("crop");
      ensures model.getHistoryWords().isEmpty();
      ensures model.getCurrentInput() == null;
      */
    @Test
    public void testRejectInvalidWord() {
        int beforeSize = model.getHistoryWords().size();
        model.updateCurrentInput("zzzz");
        // History should remain unchanged for invalid word
        assertEquals(beforeSize, model.getHistoryWords().size());
        assertNull(model.getCurrentInput());
    }

    /*
      public normal_behavior
      requires model.getDictionary().contains("care") && model.getDictionary().contains("cure");
      requires \result == true;
      ensures model.getHistoryWords().get(0).equals("cure");
      */
    @Test
    public void testAcceptOneLetterChange() {
        // Ensure dictionary contains both words
        assertTrue(model.getDictionary().contains("care"));
        assertTrue(model.getDictionary().contains("cure"));

        // Set start and target for controlled test
        model.setRandomWordFlag(false);
        model.setStartAndTargetWords("care", "care");

        // First guess must differ by one letter
        boolean win = model.countLetterDifferences("cure", model.getStartWord()) == 1;
        assertTrue(win);

        model.updateCurrentInput("cure");
        // After valid guess, history and currentInput should update
        List<String> history = model.getHistoryWords();
        assertEquals(1, history.size());
        assertEquals("cure", history.get(0));
        assertEquals("cure", model.getCurrentInput());
    }

    /*
      public normal_behavior
      requires startWord.equals("care") && targetWord.equals("tank");
      ensures \result.equals(Arrays.asList("care","cane","bane","bank","tank"));
      */
    @Test
    public void testSolutionPathFound() {
        // Build a minimal dictionary for path test
        model.getDictionary().clear();
        model.getDictionary().addAll(Arrays.asList(
                "care", "cane", "bane", "bank", "tank"
        ));

        // Set fixed start and target
        model.setRandomWordFlag(false);
        model.setStartAndTargetWords("care", "tank");

        List<String> path = model.getSolutionPath();
        List<String> expected = Arrays.asList("care","cane","bane","bank","tank");
        assertNotNull(path);
        assertEquals(expected, path);
    }
}
