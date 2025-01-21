import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    // JDBC URL, username, and password for MySQL
    public static final String DB_URL = "jdbc:mysql://localhost:3306/eyecheckupdb";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "Password";  // Replace with actual password

    public static void main(String[] args) {
        EyeCheckupApp app = new EyeCheckupApp();
        app.run();
    }
}

class EyeCheckupApp {

    private Connection connection;

    public EyeCheckupApp() {
        try {
            // Load the MySQL JDBC driver (optional but can help ensure it's loaded)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish a connection using DriverManager
            connection = DriverManager.getConnection(Main.DB_URL, Main.DB_USER, Main.DB_PASSWORD);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the Eye Checkup Application");
            while (true) {
                System.out.println("1. Register/Login User");
                System.out.println("2. Perform Eye Checkup");
                System.out.println("3. View Checkup Results");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        registerOrLoginUser(scanner);
                        break;
                    case 2:
                        performCheckup(scanner);
                        break;
                    case 3:
                        viewResults();
                        break;
                    case 4:
                        System.out.println("Thank you for using the application.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private void registerOrLoginUser(Scanner scanner) {
        try {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            System.out.print("Enter your age: ");
            int age = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.print("Enter your gender (M/F): ");
            String gender = scanner.nextLine();

            String sql = "INSERT INTO eye_checkup (name, age, gender) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setString(3, gender);
                statement.executeUpdate();
            }
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    private void performCheckup(Scanner scanner) {
        try {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            EyeTest[] tests = createTests(scanner);
            String result = "Eyes are healthy.";

            for (EyeTest test : tests) {
                if (!test.performTest()) {
                    result = "Eye issues detected. Consult a specialist.";
                }
            }

            String sql = "UPDATE eye_checkup SET result = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, result);
                statement.setString(2, name);
                statement.executeUpdate();
            }

            System.out.println("\nCheckup Completed.");
            System.out.println("Final Result: " + result);
        } catch (SQLException e) {
            System.out.println("Error performing checkup: " + e.getMessage());
        }
    }

    private EyeTest[] createTests(Scanner scanner) {
        return new EyeTest[] {
            new VisionTest(scanner),
            new ColorBlindnessTest(scanner),
            new AlphabetRecognitionTest(scanner),
            new PeripheralVisionTest(scanner),
            new EyeFatigueTest(scanner),
            new ContrastSensitivityTest(scanner),
            new DepthPerceptionTest(scanner),
            new VisualAcuityTest(scanner),
            new LightSensitivityTest(scanner),
            new ReadingClarityTest(scanner)
        };
    }

    private void viewResults() {
        try {
            String sql = "SELECT * FROM eye_checkup";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                System.out.println("\nCheckup Results:");
                while (resultSet.next()) {
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Age: " + resultSet.getInt("age"));
                    System.out.println("Gender: " + resultSet.getString("gender"));
                    System.out.println("Final Result: " + resultSet.getString("result"));
                    System.out.println("-----------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching results: " + e.getMessage());
        }
    }
}

abstract class EyeTest {
    protected Scanner scanner;

    public EyeTest(Scanner scanner) {
        this.scanner = scanner;
    }

    public abstract boolean performTest();
}

class VisionTest extends EyeTest {
    public VisionTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Vision Test...");
        System.out.print("Enter vision score (0-100): ");
        int score = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return score >= 50;
    }
}

class ColorBlindnessTest extends EyeTest {
    public ColorBlindnessTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Color Blindness Test...");
        System.out.print("Can you distinguish between red and green? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Yes");
    }
}

class AlphabetRecognitionTest extends EyeTest {
    public AlphabetRecognitionTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Alphabet Recognition Test...");
        System.out.println("Please Maintain a distance of 132cm Between Your Eyes and the System");
        String[] letters = {"E", "F", "P", "T", "O", "Z"};
        int correct = 0;

        for (String letter : letters) {
            System.out.print("Identify this letter: " + letter + " -> ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase(letter)) {
                correct++;
            }
        }

        return correct >= letters.length - 1;
    }
}

class PeripheralVisionTest extends EyeTest {
    public PeripheralVisionTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Peripheral Vision Test...");
        System.out.print("Can you see objects in your peripheral vision? (Pass/Fail): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Pass");
    }
}

class EyeFatigueTest extends EyeTest {
    public EyeFatigueTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Eye Fatigue Test...");
        System.out.print("Do you experience frequent eye fatigue? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("No");
    }
}

class ContrastSensitivityTest extends EyeTest {
    public ContrastSensitivityTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Contrast Sensitivity Test...");
        System.out.print("Can you differentiate between light and dark shades? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Yes");
    }
}

class DepthPerceptionTest extends EyeTest {
    public DepthPerceptionTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Depth Perception Test...");
        System.out.print("Can you estimate distances accurately? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Yes");
    }
}

class VisualAcuityTest extends EyeTest {
    public VisualAcuityTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Visual Acuity Test...");
        System.out.print("Can you read fine print clearly? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Yes");
    }
}

class LightSensitivityTest extends EyeTest {
    public LightSensitivityTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Light Sensitivity Test...");
        System.out.print("Do you experience discomfort in bright light? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("No");
    }
}

class ReadingClarityTest extends EyeTest {
    public ReadingClarityTest(Scanner scanner) {
        super(scanner);
    }

    @Override
    public boolean performTest() {
        System.out.println("Performing Reading Clarity Test...");
        System.out.print("Can you read and comprehend text without difficulty? (Yes/No): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Yes");
    }
}
