import java.util.HashMap;
import java.util.Map;

// Flyweight (shared object)
class CharacterStyle {
    private String font;
    private int size;
    private String color;

    public CharacterStyle(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }

    public void render(char c, int x, int y) {
        System.out.println("Char: " + c + " at (" + x + "," + y + ") "
                + "Style: " + font + ", " + size + "px, " + color);
    }
}

// Flyweight Factory
class StyleFactory {
    private static final Map<String, CharacterStyle> styles = new HashMap<>();

    public static CharacterStyle getStyle(String font, int size, String color) {
        String key = font + "-" + size + "-" + color;

        if (!styles.containsKey(key)) {
            styles.put(key, new CharacterStyle(font, size, color));
        }
        return styles.get(key);
    }
}

// Context (extrinsic data holder)
class Character {
    private char value;
    private int x, y;
    private CharacterStyle style;

    public Character(char value, int x, int y, CharacterStyle style) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.style = style;
    }

    public void draw() {
        style.render(value, x, y);
    }
}

// Client
public class FlyweightDemo {
    public static void main(String[] args) {

        CharacterStyle style1 = StyleFactory.getStyle("Arial", 12, "Black");
        CharacterStyle style2 = StyleFactory.getStyle("Arial", 12, "Black");

        Character c1 = new Character('H', 0, 0, style1);
        Character c2 = new Character('i', 1, 0, style2);

        c1.draw();
        c2.draw();

        // proves reuse
        System.out.println("Same object? " + (style1 == style2));
    }
}