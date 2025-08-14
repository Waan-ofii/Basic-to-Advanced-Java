public class Validation {
    public void validateAge(int age) {
        if (age < 0) {
            // Throw an IllegalArgumentException with a custom error message
            throw new IllegalArgumentException("Age must be a non-negative value.");
        }
        // Other validation logic
    }
}


