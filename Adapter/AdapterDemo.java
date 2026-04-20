// Adapter Pattern Example - Payment System

// 1. Target Interface (what client expects)
interface PaymentProcessor {
    void processPayment(double amount);
}

// 2. Adaptee (existing class with different method)
class OldPaymentSystem {
    public void makePayment(double value) {
        System.out.println("Payment made using Old System: " + value);
    }
}

// 3. Adapter (connects Target and Adaptee)
class PaymentAdapter implements PaymentProcessor {
    private OldPaymentSystem oldSystem;

    // Constructor
    public PaymentAdapter(OldPaymentSystem oldSystem) {
        this.oldSystem = oldSystem;
    }

    // Convert method
    @Override
    public void processPayment(double amount) {
        oldSystem.makePayment(amount); // adapting call
    }
}

// 4. Client Code (used by students/demo)
public class AdapterDemo {
    public static void main(String[] args) {

        // Adaptee
        OldPaymentSystem oldSystem = new OldPaymentSystem();

        // Adapter
        PaymentProcessor adapter = new PaymentAdapter(oldSystem);

        // Use adapter like normal system
        adapter.processPayment(1000);
    }
}