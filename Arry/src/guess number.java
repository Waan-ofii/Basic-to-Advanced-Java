import java.util.Scanner;
import java.util.Random;

 class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();
        
        boolean playAgain = true;
        
        while (playAgain) {
            int numberToGuess = rand.nextInt(20) + 1;  // Random number between 1 and 20
            int userGuess = 0;
            int attempts = 0;
            
            System.out.println("Welcome to the Number Guessing Game!");
            System.out.println("I have selected a number between 1 and 20.");
            System.out.println("Can you guess it?");
            
            while (userGuess != numberToGuess) {
                System.out.print("Enter your guess: ");
                userGuess = scanner.nextInt();
                attempts++;
                
                if (userGuess < numberToGuess) {
                    System.out.println("Too low! Try again.");
                } else if (userGuess > numberToGuess) {
                    System.out.println("Too high! Try again.");
                } else {
                    System.out.println("Congratulations! You've guessed the number.");
                    System.out.println("It took you " + attempts + " attempts.");
                }
            }
            
            System.out.print("Would you like to play again? (yes/no): ");
            String response = scanner.next();
            if (response.equalsIgnoreCase("no")) {
                playAgain = false;
            }
        }
        
        System.out.println("Thanks for playing!");
        scanner.close();
    }
}