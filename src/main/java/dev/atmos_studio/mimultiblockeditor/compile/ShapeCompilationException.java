package dev.atmos_studio.mimultiblockeditor.compile;

public final class ShapeCompilationException
        extends RuntimeException {

    public ShapeCompilationException(
            String message
    ) {
        super(message);
    }

    public ShapeCompilationException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}