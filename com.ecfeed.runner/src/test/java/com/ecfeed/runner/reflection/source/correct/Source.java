package com.ecfeed.runner.reflection.source.correct;

public class Source {

    public static class Element1 {

        public Element1(int a, double b, String c, Element2 d) {}

        public Element1(int a, int b, int c) {}
    }

    public static class Element2 {

    }
}
