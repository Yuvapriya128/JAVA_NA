package org.example.entity;

//no setters because Book entity is final
public final class Book {
    private final String bookId;
    private final String title;
    private final String author;
    private final String category;
    private final int borrowCount;
    private final double rating;

    public Book(String bookId,
                String title,
                String author,
                String category,
                int borrowCount,
                double rating) {

        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.borrowCount = borrowCount;
        this.rating = rating;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", borrowCount=" + borrowCount +
                ", rating=" + rating +
                '}';
    }
}
