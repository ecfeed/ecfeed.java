package com.ecfeed.runner.reflection;

import com.ecfeed.runner.reflection.source.correct.Source;
import com.ecfeed.structure.StructureInitializer;
import com.ecfeed.structure.StructureInitializerDefault;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class FeedTest {

    static Iterable<Object[]> feed() {
        StructureInitializer initializer = new StructureInitializerDefault();

        initializer.source("com.ecfeed.runner.reflection.source.correct");

        initializer.activate("Element1(int,double,String,Element2)");
        initializer.activate("Element2(int,int,int)");

        var feed = new ArrayList<Object[]>();

        feed.add(initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("1", "2.1", "test 1", "-1", "-2", "-3", "ecFeed 1"))));
        feed.add(initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("3", "4.2", "test 2", "-4", "-5", "-6", "ecFeed 2"))));
        feed.add(initializer.getTestCase("method(Element1,String)", new LinkedList<>(Arrays.asList("9", "6.4", "test 3", "-7", "-8", "-9", "ecFeed 3"))));

        return feed;
    }

    @ParameterizedTest
    @MethodSource("feed")
    void feedTest(Source.Element1 element, String name) {
        System.out.println("element = " + element + ", name = " + name);
    }
}
