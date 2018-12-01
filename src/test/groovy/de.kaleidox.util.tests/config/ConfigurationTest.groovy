package de.kaleidox.util.tests.config

import de.kaleidox.util.config.Configuration
import org.junit.Before
import org.junit.Test

class ConfigurationTest {
    Configuration config

    @Before
    void setup() {
        config = new Configuration("configTest.json")

        config.register("level", "warn")
                .register("person.age", 20)
                .register("person.lang.java", true)
                .register("person.lang.cpp", true)
    }

    @Test
    void testFirstLevel() {
        assert config.var("level", String) == "warn"
    }

    @Test
    void testSecondLevel() {
        assert config.var("person.name") == "Tobias"
        assert config.var("person.age", Integer) == 19
    }

    @Test
    void testThirdLevel() {
        assert config.var("person.lang.java", Boolean)
        assert !config.var("person.lang.cpp", Boolean)
    }

    @Test(expected = NoSuchElementException)
    void testUnknownVar() {
        config.var("color")
    }
}
