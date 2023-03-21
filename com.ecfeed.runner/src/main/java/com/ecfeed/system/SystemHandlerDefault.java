package com.ecfeed.system;

public class SystemHandlerDefault implements SystemHandler {

    private SystemHandlerDefault() {
    }

    public static SystemHandler create() {

        return new SystemHandlerDefault();
    }

    @Override
    public void printDiagnosticMessage(String message, boolean isDiagnostic) {

        if (isDiagnostic) {
            java.lang.System.out.println(message);
        }
    }

    @Override
    public void sleep(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
        }
    }

    @Override
    public void nop(Object chunk) {

        java.lang.System.out.println(chunk);
    }
}
