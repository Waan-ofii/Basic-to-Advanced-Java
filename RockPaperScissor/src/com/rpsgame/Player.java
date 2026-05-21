package src.com.rpsgame;

public class Player {
    // Properties (variables)
    private String name;
    private int score;
    private String currentChoice;  // Stores "rock", "paper", or "scissors"
    
    // Constructor - runs when we create a new Player
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.currentChoice = "";
    }
    
    // Getters and Setters (ways to access private properties)
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
    
    public void addScore() {
        this.score++;
    }
    
    public void resetScore() {
        this.score = 0;
    }
    
    public String getCurrentChoice() {
        return currentChoice;
    }
    
    public void setCurrentChoice(String choice) {
        this.currentChoice = choice;
    }
    
    // Get emoji for display
    public String getChoiceEmoji() {
        switch(currentChoice) {
            case "rock": return "✊";
            case "paper": return "✋";
            case "scissors": return "✌️";
            default: return "❓";
        }
    }
}