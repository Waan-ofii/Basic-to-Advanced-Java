// Memento Pattern Example - Text Editor

// 1. Memento (stores state)
class Memento {
    private String text;

    public Memento(String text) {
        this.text = text;
    }

    public String getSavedText() {
        return text;
    }
}

// 2. Originator (main object)
class TextEditor {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // Save state
    public Memento save() {
        return new Memento(text);
    }

    // Restore state
    public void restore(Memento memento) {
        text = memento.getSavedText();
    }
}

// 3. Caretaker (stores history)
class History {
    private Memento memento;

    public void saveState(Memento m) {
        memento = m;
    }

    public Memento getState() {
        return memento;
    }
}

// 4. Demo (Client)
public class MementoDemo {
    public static void main(String[] args) {

        TextEditor editor = new TextEditor();
        History history = new History();

        // Step 1: Set text
        editor.setText("Hello World");
        System.out.println("Current: " + editor.getText());

        // Step 2: Save state
        history.saveState(editor.save());

        // Step 3: Change text
        editor.setText("New Text");
        System.out.println("Changed: " + editor.getText());

        // Step 4: Restore old state
        editor.restore(history.getState());
        System.out.println("Restored: " + editor.getText());
    }
}