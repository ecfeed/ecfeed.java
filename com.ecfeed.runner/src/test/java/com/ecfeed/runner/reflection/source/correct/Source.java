package com.ecfeed.runner.reflection.source.correct;

public class Source {

    public static class Element1 {
        public int a;
        public double b;
        public String c;
        public Element2 d;

        public Element1(int a, double b, String c, Element2 d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public Element1(int a, double b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    public static class Element2 {
        public int a, b, c;

        public Element2(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}
