import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LibraryManagement {

    private static final String DATA_FILE = "books.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Book> books = new ArrayList<>();


    static class Book {
        private String id;
        private String title;
        private String author;

        Book(String id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }

        String getId()     { return id; }
        String getTitle()  { return title; }
        String getAuthor() { return author; }

        void setTitle(String title)   { this.title = title; }
        void setAuthor(String author) { this.author = author; }

        String toFileLine() {
            return id + ";" + title + ";" + author;
        }
    }


    private static void loadBooks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    books.add(new Book(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read data file.");
        }
    }

    private static void saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Book b : books) {
                writer.println(b.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Error: could not save data.");
        }
    }

    // -----------------------------------------------------------------------
    // Input validation
    // -----------------------------------------------------------------------
    private static String inputText(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.contains(";")) {
                System.out.println("  Error: the character ';' is not allowed.");
                continue;
            }
            if (!value.isEmpty() || allowEmpty) {
                return value;
            }
            System.out.println("  Error: this field cannot be empty.");
        }
    }

    private static Book findById(String id) {
        for (Book b : books) {
            if (b.getId().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }


    private static void displayBooks(List<Book> list, String title) {
        System.out.println("\n=== " + title + " ===");
        if (list.isEmpty()) {
            System.out.println("No books to show.");
            return;
        }
        System.out.printf("%-8s %-30s %-25s%n", "Book ID", "Title", "Author");
        System.out.println("-".repeat(65));
        for (Book b : list) {
            System.out.printf("%-8s %-30s %-25s%n",
                    b.getId(), shorten(b.getTitle(), 30), shorten(b.getAuthor(), 25));
        }
        System.out.println("\nTotal: " + list.size() + " book(s)");
    }

    private static String shorten(String text, int max) {
        return text.length() > max ? text.substring(0, max - 3) + "..." : text;
    }


    private static void addBook() {
        System.out.println("\n--- Add new book ---");
        String id;
        while (true) {
            id = inputText("Book ID: ", false);
            if (findById(id) == null) {
                break;
            }
            System.out.println("  Error: this Book ID already exists.");
        }
        String title = inputText("Title: ", false);
        String author = inputText("Author: ", false);

        books.add(new Book(id, title, author));
        saveBooks();
        System.out.println("Book added successfully.");
    }

    private static void updateBook() {
        System.out.println("\n--- Update book ---");
        String id = inputText("Enter Book ID to update: ", false);
        Book book = findById(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.println("Current: " + book.getTitle() + " - " + book.getAuthor());
        System.out.println("Press Enter to keep the current value.");

        String newTitle = inputText("New title: ", true);
        String newAuthor = inputText("New author: ", true);

        if (!newTitle.isEmpty())  book.setTitle(newTitle);
        if (!newAuthor.isEmpty()) book.setAuthor(newAuthor);

        saveBooks();
        System.out.println("Book updated successfully.");
    }

    private static void deleteBook() {
        System.out.println("\n--- Delete book ---");
        String id = inputText("Enter Book ID to delete: ", false);
        Book book = findById(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.print("Delete '" + book.getTitle() + "'? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            books.remove(book);
            saveBooks();
            System.out.println("Book deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void searchBook() {
        System.out.println("\n--- Search book ---");
        System.out.println("1. Search by Book ID");
        System.out.println("2. Search by title");
        System.out.println("3. Search by author");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
            System.out.println("Invalid choice.");
            return;
        }
        String keyword = inputText("Keyword: ", false).toLowerCase();

        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            String field = switch (choice) {
                case "1" -> b.getId();
                case "2" -> b.getTitle();
                default  -> b.getAuthor();
            };
            if (field.toLowerCase().contains(keyword)) {
                results.add(b);
            }
        }
        displayBooks(results, "SEARCH RESULTS");
    }


    public static void main(String[] args) {
        loadBooks();
        while (true) {
            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("1. Add book");
            System.out.println("2. Update book");
            System.out.println("3. Delete book");
            System.out.println("4. Search book");
            System.out.println("5. Display all books");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> updateBook();
                case "3" -> deleteBook();
                case "4" -> searchBook();
                case "5" -> displayBooks(books, "ALL BOOKS");
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose 0-5.");
            }
        }
    }
}
