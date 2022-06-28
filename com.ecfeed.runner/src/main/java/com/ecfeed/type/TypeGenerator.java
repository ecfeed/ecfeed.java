package com.ecfeed.type;

/**
 * Generator type.
 */
public enum TypeGenerator {
    NWise("genNWise", "NWise"),
    Pairwise("genNWise", "Pairwise"),
    Cartesian("genCartesian", "Cartesian"),
    Random("genRandom", "Random"),
    Static("static", "Static");

    private final String name;
    private final String nickname;

    TypeGenerator(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public String getName() {

        return this.name;
    }

    public String getNickname() {

        return this.nickname;
    }
}
