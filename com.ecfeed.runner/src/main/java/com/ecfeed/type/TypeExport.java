package com.ecfeed.type;

/**
 * Export data format.
 */
public enum TypeExport {
    XML, CSV, JSON, Gherkin,
    Raw, Custom,
    RFC_4627, RFC_4180;

    private String delimiter = ",";
    private int indent = 2;
    private boolean nested = false;
    private boolean explicit = false;

    public TypeExport setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public TypeExport setIndent(int indent) {
        this.indent = indent;
        return this;
    }

    public int getIndent() {
        return indent;
    }

    public TypeExport setNested(boolean nested) {
        this.nested = nested;
        return this;
    }

    public boolean getNested() {
        return nested;
    }

    public TypeExport setExplicit(boolean explicit) {
        this.explicit = explicit;
        return this;
    }

    public boolean getExplicit() {
        return explicit;
    }

    public String getRFC4627() {
        return "RFC 4627\nIndent:" + indent + "\nExplicit:" + explicit + "\nNested:" + nested;
    }

    public String getRFC4180() {
        return "RFC 4180\nDelimiter:" + delimiter + "\nExplicit:" + explicit + "\nNested:" + nested;
    }
}
