package com.ecfeed.system;

public interface SystemHandler {

    void printDiagnosticMessage(String message, boolean isDiagnostic);

    void sleep(int milliseconds);

    void nop(Object chunk);
}
