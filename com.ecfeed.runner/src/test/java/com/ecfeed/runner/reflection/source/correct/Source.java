package com.ecfeed.runner.reflection.source.correct;

public class Source {

    public static class Element1 {

        public Element1(int a, double b, String c, Element2 d) {}

        public Element1(int a, int b, int c) {}

        public Element1(byte a, short b, int c, long d, float e, double f, boolean g, char h, String i, Element2 j) {}
    }

    public static class Element2 {

        public Element2(int a, int b, int c) {}
    }
}
