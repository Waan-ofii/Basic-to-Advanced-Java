package src.com.rpsgame;

import java.util.Random;

public class GameLogic {
    private Random random;
    
    // Constructor
    public GameLogic() {
        random = new Random();
    }
    
    // Computer makes random choice
    public String getComputerChoice() {
        int rand = random.nextInt(3);  // 0, 1, or 2
        
        switch(rand) {
            case 0: return "rock";
            case 1: return "paper";
            case 2: return "scissors";
            default: return "rock";
        }
    }
    
    // Get emoji for any choice
    public String getChoiceEmoji(String choice) {
        switch(choice) {
            case "rock": return "✊";
            case "paper": return "✋";
            case "scissors": return "✌️";
            default: return "❓";
        }
    }
    
    // Determine winner: returns "player", "computer", or "tie"
    public String determineWinner(String playerChoice, String computerChoice) {
        if (playerChoice.equals(computerChoice)) {
            return "tie";
        }
        
        // Player wins scenarios
        if ((playerChoice.equals("rock") && computerChoice.equals("scissors")) ||
            (playerChoice.equals("paper") && computerChoice.equals("rock")) ||
            (playerChoice.equals("scissors") && computerChoice.equals("paper"))) {
            return "player";
        }
        
        // Computer wins
        return "computer";
    }
    
    // Get result message with emojis
    public String getResultMessage(String winner, String playerChoice, String computerChoice) {
        String playerEmoji = getChoiceEmoji(playerChoice);
        String computerEmoji = getChoiceEmoji(computerChoice);
        
        switch(winner) {
            case "player":
                return "🎉 You win! " + playerEmoji + " beats " + computerEmoji + " 🎉";
            case "computer":
                return "💻 Computer wins! " + computerEmoji + " beats " + playerEmoji + " 💔";
            case "tie":
                return "🤝 Tie! Both chose " + playerEmoji + " 🤝";
            default:
                return "Make a choice!";
        }
    }
}