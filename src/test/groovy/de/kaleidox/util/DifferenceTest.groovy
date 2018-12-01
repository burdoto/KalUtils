package de.kaleidox.util

import org.junit.Before
import org.junit.Test

import static de.kaleidox.util.Difference.of

class DifferenceTest {
    List<String> original, changed

    @Before
    void createLists() {
        original = ["google", "bing", "facebook", "linkedin", "twitter",  "googleplus", "bingnews",   "plexoogl"]
        changed  = ["google", "bing", "gmail",    "outlook",  "facebook", "twitter",    "googleplus", "plexoogl"]

        // removed: linkedin & bingnews
        // added: gmail & outlook
    }

    @Test
    void testAdditions() {
        def diff = of(original, changed)
        def added = diff.added

        assert diff.hasAdded("gmail").result
        assert diff.hasAdded("outlook").result

        assert added.size() == 2
        assert added.contains("gmail")
        assert added.contains("outlook")

        assert diff.hasRemoved("linkedin").result
        assert diff.hasRemoved("bingnews").result
    }

    @Test
    void testDeletions() {
        def diff = of(original, changed)
        def removed = diff.removed

        assert diff.hasRemoved("linkedin").result
        assert diff.hasRemoved("bingnews").result

        assert removed.size() == 2
        assert removed.contains("linkedin")
        assert removed.contains("bingnews")

        assert diff.hasAdded("gmail").result
        assert diff.hasAdded("outlook").result
    }
}
