package com.ecfeed.runner.demo;

public class Source {

    public static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static class Data {
        private Person delinquent;
        private int id;

        public Data() {
        }

        public Data(Person delinquent, int id) {
            this.delinquent = delinquent;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "delinquent=" + delinquent +
                    ", id=" + id +
                    '}';
        }
    }
}
