# SOLID Principles - Code Examples for Beginners

## Real-World Analogy: A Restaurant

Imagine you're building a restaurant software system. Let's see how SOLID principles apply:

---

## 1. Single Responsibility Principle (SRP)

### ❌ BEFORE (Bad)
One chef does EVERYTHING:
```java
class Chef {
    public void cookFood() { /* cooking */ }
    public void takeOrders() { /* ordering */ }
    public void serveFood() { /* serving */ }
    public void cleanKitchen() { /* cleaning */ }
    public void handlePayments() { /* payments */ }
}
```
**Problem:** If anything changes (new payment system, new serving style), you have to modify the Chef class!

### ✅ AFTER (Good)
Each person has ONE job:
```java
class Chef {
    public void cookFood() { /* only cooking */ }
}

class Waiter {
    public void takeOrders() { /* only ordering */ }
    public void serveFood() { /* only serving */ }
}

class Janitor {
    public void cleanKitchen() { /* only cleaning */ }
}

class Cashier {
    public void handlePayments() { /* only payments */ }
}
```
**Benefit:** If payment system changes, only modify `Cashier`!

---

## 2. Open/Closed Principle (OCP)

### ❌ BEFORE (Bad)
Hard-coded menu items:
```java
class Menu {
    public void displayMenu() {
        if (cuisine == "Italian") {
            System.out.println("Pasta");
        } else if (cuisine == "Chinese") {
            System.out.println("Fried Rice");
        } else if (cuisine == "Mexican") {
            System.out.println("Tacos");
        }
        // To add new cuisine, modify this class!
    }
}
```
**Problem:** Every time you add a new cuisine, you modify the Menu class. The class is "closed" for extension.

### ✅ AFTER (Good)
Use a factory pattern:
```java
interface Cuisine {
    void cook();
}

class ItalianCuisine implements Cuisine {
    public void cook() { System.out.println("Cooking Pasta"); }
}

class ChineseCuisine implements Cuisine {
    public void cook() { System.out.println("Cooking Fried Rice"); }
}

class CuisineFactory {
    public static Cuisine createCuisine(String type) {
        if (type.equals("Italian")) return new ItalianCuisine();
        if (type.equals("Chinese")) return new ChineseCuisine();
        // Add new cuisines without touching Menu!
    }
}
```
**Benefit:** Add new cuisine? Just create new class, don't modify existing ones!

---

## 3. Liskov Substitution Principle (LSP)

### ❌ BEFORE (Bad)
Square "violates" Rectangle contract:
```java
class Rectangle {
    protected int width, height;
    
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
    
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        width = w;
        height = w;  // Square breaks Rectangle's contract!
    }
}

// Problem: This breaks!
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(3);
// Expected area: 5*3 = 15
// Actual area: 3*3 = 9  ❌
```

### ✅ AFTER (Good)
Use composition instead of inheritance:
```java
interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private int width, height;
    
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
    
    public int getArea() { return width * height; }
}

class Square implements Shape {
    private int side;
    
    public void setSide(int s) { side = s; }
    
    public int getArea() { return side * side; }
}

// Now it works properly!
Shape rect = new Rectangle();
// Shape square = new Square();
```

---

## 4. Interface Segregation Principle (ISP)

### ❌ BEFORE (Bad)
One huge interface that forces unnecessary methods:
```java
interface Animal {
    void eat();
    void sleep();
    void fly();      // Not all animals fly!
    void swim();     // Not all animals swim!
}

class Dog implements Animal {
    public void eat() { /* real code */ }
    public void sleep() { /* real code */ }
    public void fly() { /* ERROR! Dogs can't fly */ }
    public void swim() { /* ERROR! Not all dogs swim */ }
}
```

### ✅ AFTER (Good)
Split into smaller, focused interfaces:
```java
interface Animal {
    void eat();
    void sleep();
}

interface Flyer {
    void fly();
}

interface Swimmer {
    void swim();
}

class Dog implements Animal, Swimmer {
    public void eat() { /* dogs eat */ }
    public void sleep() { /* dogs sleep */ }
    public void swim() { /* dogs can swim */ }
    // No fly() method! ✅
}

class Eagle implements Animal, Flyer {
    public void eat() { /* eagles eat */ }
    public void sleep() { /* eagles sleep */ }
    public void fly() { /* eagles fly */ }
    // No swim() method! ✅
}
```
**Benefit:** Classes only implement methods they actually need!

---

## 5. Dependency Inversion Principle (DIP)

### ❌ BEFORE (Bad)
High-level depends on low-level concrete classes:
```java
class PaymentService {
    private CreditCardProcessor processor;  // Direct dependency!
    
    public PaymentService() {
        processor = new CreditCardProcessor();
    }
    
    public void pay() {
        processor.processPayment();  // Tightly coupled!
    }
}

// Problem: What if you want to use PayPal instead?
// You have to modify PaymentService!
```

### ✅ AFTER (Good)
High-level depends on abstractions (interfaces):
```java
interface PaymentProcessor {
    void processPayment();
}

class CreditCardProcessor implements PaymentProcessor {
    public void processPayment() { /* credit card logic */ }
}

class PayPalProcessor implements PaymentProcessor {
    public void processPayment() { /* paypal logic */ }
}

class PaymentService {
    private PaymentProcessor processor;  // Depends on interface!
    
    // Dependency Injection - pass the processor in
    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }
    
    public void pay() {
        processor.processPayment();
    }
}

// Usage:
PaymentService service1 = new PaymentService(new CreditCardProcessor());
PaymentService service2 = new PaymentService(new PayPalProcessor());
// Easy to swap! ✅
```

---

## Your Grade System: Before & After

### BEFORE: GradeManager Violating SOLID

```java
public class GradeManager {
    private Grade[] grades;  // SRP: storing grades
    
    // SRP: calculating grades
    public void recordGrade(Student student, Subject subject, double score) {
        // ... storing logic
    }
    
    // SRP: exporting grades
    public void exportGradeReport(Student student) {
        FileWriter writer = new FileWriter(...);  // DIP: Depends on concrete FileWriter!
        // ... export logic
    }
    
    // SRP: importing grades
    public void bulkImportGrades(StudentManager studentManager) {
        BufferedReader reader = new BufferedReader(...);  // DIP: Depends on concrete FileReader!
        // ... import logic
    }
    
    // SRP: calculating statistics
    public void viewGradeStatistics(StudentManager studentManager) {
        // ... statistics logic
    }
}

// OCP Problem in Main.java:
if (s == 1) {
    subject = new CoreSubject("Math", "MATH101");
} else if (s == 2) {
    subject = new CoreSubject("English", "ENG101");
}
// Adding new subject requires modifying Main!
```

**Problems:**
- GradeManager does 4+ different jobs (SRP violated)
- Hard-coded subjects in Main (OCP violated)
- FileWriter dependency is concrete (DIP violated)
- One big Gradable interface (ISP violated)

### AFTER: SOLID Refactoring

```java
// 1. ISP: Split interfaces
interface Gradable {
    boolean isPassing(double grade);
    String getGradeLevel(double grade);
}

interface GPACalculable {
    double calculateGPA(double score);
}

// 2. SRP: Each class has one job
class GradeRepository implements IGradeRepository {
    public void addGrade(Grade grade) { /* only storage */ }
}

class GradeExporter implements IFileExporter {
    public void exportGradeReport(Student student, IGradeRepository repo) { /* only export */ }
}

class GradeImporter implements IGradeImporter {
    public void importGrades(IGradeRepository repo, StudentManager mgr) { /* only import */ }
}

class GradeStatisticsCalculator implements IGradeStatisticsCalculator {
    public void displayStatistics(IGradeRepository repo, StudentManager mgr) { /* only stats */ }
}

// 3. DIP: GradeManager depends on interfaces, not concrete classes
public class GradeManager {
    private IGradeRepository gradeRepository;
    private IFileExporter fileExporter;
    private IGradeImporter gradeImporter;
    private IGradeStatisticsCalculator statisticsCalculator;
    
    // Constructor with Dependency Injection
    public GradeManager(IGradeRepository repo,
                       IFileExporter exporter,
                       IGradeImporter importer,
                       IGradeStatisticsCalculator calc) {
        this.gradeRepository = repo;
        this.fileExporter = exporter;
        this.gradeImporter = importer;
        this.statisticsCalculator = calc;
    }
    
    public void recordGrade(Student student, Subject subject, double score) {
        // Delegates to GradeRepository
        gradeRepository.addGrade(new Grade(...));
    }
    
    public void exportGradeReport(Student student) {
        // Delegates to GradeExporter (abstraction!)
        fileExporter.exportGradeReport(student, gradeRepository);
    }
}

// 4. OCP: SubjectFactory handles subject creation
class SubjectFactory {
    public static Subject createCoreSubject(int choice) {
        switch (choice) {
            case 1: return new CoreSubject("Math", "MATH101");
            case 2: return new CoreSubject("English", "ENG101");
            // Add new subjects here, not in Main!
        }
    }
}

// Main.java is much cleaner:
public static void main(String[] args) {
    // DIP: Create dependencies
    IGradeRepository repo = new GradeRepository();
    IFileExporter exporter = new GradeExporter();
    IGradeImporter importer = new GradeImporter();
    IGradeStatisticsCalculator calc = new GradeStatisticsCalculator();
    
    // DIP: Inject into GradeManager
    GradeManager manager = new GradeManager(repo, exporter, importer, calc);
    
    // OCP: Use factory for subjects
    Subject subject = SubjectFactory.createCoreSubject(1);
}
```

**Benefits:**
- ✅ Each class has ONE job (SRP)
- ✅ Add new subjects without modifying code (OCP)
- ✅ Easy to swap implementations (DIP)
- ✅ Focused interfaces (ISP)
- ✅ Students properly substitute Student (LSP)

---

## Key Takeaways

| Principle | Key Idea | Real-World |
|-----------|----------|-----------|
| **SRP** | One class, one job | Chef only cooks, Waiter only serves |
| **OCP** | Open for extension, closed for modification | Add new cuisine without modifying menu |
| **LSP** | Subtypes can replace parent types | Any bird can use fly() method properly |
| **ISP** | Clients only see what they need | Dog doesn't need fly() method |
| **DIP** | Depend on abstractions, not concrete classes | Pay with any processor, not just credit card |

---

## Practice Exercise

### Your Task:
The system needs to support **Discord notifications** when a grade is recorded.

**Using SOLID:**

1. Create interface:
   ```java
   interface NotificationService {
       void notify(String message);
   }
   ```

2. Implement Discord:
   ```java
   class DiscordNotificationService implements NotificationService {
       public void notify(String message) {
           // Send to Discord API
       }
   }
   ```

3. Inject into GradeManager:
   ```java
   public class GradeManager {
       private NotificationService notifier;
       
       public GradeManager(..., NotificationService notifier) {
           this.notifier = notifier;
       }
       
       public void recordGrade(Student student, Subject subject, double score) {
           // ... record grade
           notifier.notify("Grade recorded for " + student.getName());
       }
   }
   ```

4. Use in Main:
   ```java
   NotificationService notifier = new DiscordNotificationService();
   GradeManager manager = new GradeManager(..., notifier);
   ```

**That's it!** No changes to GradeManager, just injected a new dependency. SOLID in action! 🎉

---

## Remember

> "Make each class responsible for one thing. Make it one reason to change."
> — Uncle Bob (Robert C. Martin)

Your code is now following this principle. Keep learning and growing! 🚀
