package com.medivault.exception;

/**
 * InvalidInputException.java — Custom Exception
 *
 * Thrown by PatientService when user input fails validation.
 * Examples: blank name, non-positive age, empty phone number.
 *
 * Extends RuntimeException so callers don't need a checked throws clause.
 * Caught in AddPatientUI and shown to the user via JOptionPane.
 *
 * [EXCEPTION HANDLING] — custom exception type used for validation errors
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
