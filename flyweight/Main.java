import java.util.*;


interface PaymentStrategy {
    void pay(int amount);
}

// Concrete strategies
class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using PayPal");
    }
}

// Context
class ShoppingCart {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void checkout(int amount) {
        if (strategy == null) {
            System.out.println("No payment strategy set!");
        } else {
            strategy.pay(amount);
        }
    }
}


// =======================
// OBSERVER PATTERN
// =======================

// Observer interface
interface Observer {
    void update(String message);
}

// Concrete observers
class EmailSubscriber implements Observer {
    public void update(String message) {
        System.out.println("Email received: " + message);
    }
}

class SMSSubscriber implements Observer {
    public void update(String message) {
        System.out.println("SMS received: " + message);
    }
}

// Subject
class NewsPublisher {
    private List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer o) {
        observers.add(o);
    }

    public void unsubscribe(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}


public class Main {
    public static void main(String[] args) {

        // ---- Strategy demo ----
        ShoppingCart cart = new ShoppingCart();

        cart.setStrategy(new CreditCardPayment());
        cart.checkout(100);

        cart.setStrategy(new PayPalPayment());
        cart.checkout(200);

        System.out.println("---------------------");

        // ---- Observer demo ----
        NewsPublisher publisher = new NewsPublisher();

        Observer email = new EmailSubscriber();
        Observer sms = new SMSSubscriber();

        publisher.subscribe(email);
        publisher.subscribe(sms);

        publisher.notifyObservers("Breaking News!");

        publisher.unsubscribe(sms);

        publisher.notifyObservers("More Updates!");
    }
}
