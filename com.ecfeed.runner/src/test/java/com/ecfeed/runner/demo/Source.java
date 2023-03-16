package com.ecfeed.runner.demo;

public class Source {

    public static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class Data {
        private Person delinquent;
        private int id;

        public Data(Person delinquent, int id) {
            this.delinquent = delinquent;
            this.id = id;
        }
    }
}
