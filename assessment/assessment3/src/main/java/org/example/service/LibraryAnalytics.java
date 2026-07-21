package org.example.service;

import com.sun.source.tree.ReturnTree;
import org.example.entity.Book;

import java.util.*;
import java.util.stream.Collectors;

public  class LibraryAnalytics {

    private Map<String, Book> books = new HashMap<>();

    public void loadBooks(List<String> records) {
      if(records==null){
          System.out.println("Records are empty");
      }
      for(String record:records){
          if(record==null || record.isEmpty()){
              continue;
          }
          String[] data=record.trim().split("\\|");
          if(data.length!=6){
              System.out.println("Invalid record: " + record);
              continue;
          }
          String id = data[0].trim();
          String title = data[1].trim();
          String author = data[2].trim();
          String category = data[3].trim();
          int borrowcnt;
          double rating;
          try {
              borrowcnt = Integer.parseInt(data[4].trim());
              rating = Double.parseDouble(data[5].trim());
          } catch (NumberFormatException e) {
              System.out.println("Invalid number format in record: " + record);
              continue;
          }


          if(rating<0 || rating>5){
              System.out.println("Invalid rating in record: " + record);
              continue;
          }
          if(borrowcnt<0){
              System.out.println("Invalid borrow count in record: " + record);
              continue;
          }
          if(id.isEmpty() || title.isEmpty() || author.isEmpty() || category.isEmpty() ){
              continue;
          }
          Book current = new Book(id, title, author, category, borrowcnt, rating);
          Book existing = books.get(id);

          if (existing == null) {
              books.put(id, current);
          }

            else {
                if (current.getRating() > existing.getRating()) {
                    books.put(id, current);
                }else if(current.getRating() == existing.getRating()){
                    if(current.getBorrowCount()>existing.getBorrowCount()){
                        books.put(id,current);
                    }else if(current.getBorrowCount()== existing.getBorrowCount()){
                        if(current.getTitle().compareTo(existing.getTitle())<0){
                            books.put(id,current);
                        }
                    }
                }
            }
      }

    }

    public List<Book> topRatedBooks(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }

        return books.values().stream()
                .sorted(
                        Comparator.comparingDouble(Book::getRating).reversed()
                                .thenComparing(Comparator.comparingInt(Book::getBorrowCount).reversed())
                                .thenComparing(Book::getTitle)
                )
                .limit(n)
                .toList();
    }

    public Map<String, Double> averageRatingByCategory() {
        return books.values().stream().collect(
                Collectors.groupingBy(Book::getCategory,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(Book::getRating),
                                avg -> Math.round(avg * 100.0) / 100.0
                        )));
    }

    public Optional<Book> mostBorrowedBook() {
        return books.values().stream().max(
                Comparator.comparingInt(Book::getBorrowCount)
                        .thenComparingDouble(Book::getRating)
                        .thenComparing(Comparator.comparing(Book::getTitle).reversed()));
    }

    public Set<String> authorsWithMultipleCategories() {

        Set<String> res=new TreeSet<>();
        Map<String,Set<String>> tempres=new TreeMap<>();
        tempres=books.values().stream().collect(
                Collectors.groupingBy(
                        Book::getAuthor,
                        Collectors.mapping(Book::getCategory, Collectors.toCollection(TreeSet::new))
                )
        );
        tempres.forEach((author, categories) -> {
            if (categories.size() > 1) {
                res.add(author);
            }
        });

        return res;
    }

    public Map<String, List<Book>> groupBooksByAuthor() {
      return null;



    }

    public List<String> suspiciousBooks() {

        Set<String> result = new TreeSet<>();

        Map<String, Double> avgBorrow = books.values()
                .stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        Collectors.averagingInt(Book::getBorrowCount)
                ));

        Map<String, Double> avgRating = books.values()
                .stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        Collectors.averagingDouble(Book::getRating)
                ));

        for(Book book : books.values()){

            String title = book.getTitle();

            boolean sus = false;

            // word repetition
            String[] words = title.toLowerCase().split("\\s+");

            for(int i = 0; i < words.length - 1; i++){
                if(words[i].equalsIgnoreCase(words[i + 1])){
                    sus = true;
                }
            }

            // author name in title
            if(title.toLowerCase()
                    .contains(book.getAuthor().toLowerCase())){
                sus = true;
            }

            // avg borrow and avg rating by category
            double avgBorrowCat =
                    avgBorrow.get(book.getCategory());

            double avgRatingCat =
                    avgRating.get(book.getCategory());

            if(book.getBorrowCount() > 4 * avgBorrowCat){
                sus = true;
            }

            if(book.getRating() < avgRatingCat
                    && book.getBorrowCount() > avgBorrowCat){
                sus = true;
            }

            if(sus){
                result.add(title);
            }
        }

        return new ArrayList<>(result);
    }

    public Map<String, Map<String, Book>> categoryWiseTopRatedBookByEachAuthor() {
        return null;
    }
}
