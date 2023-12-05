package io.github.vilginushki.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericUtilsTest {


    @Test
    public void shouldSplitStringByLength() {
        String input = "testtesttest";
        List<String> expectedOutput = List.of("test", "test", "test");

        List<String> actualOutput = Arrays.stream(GenericUtils.splitByLength(input, 4)).toList();

        assertEquals(expectedOutput, actualOutput);
    }

}