package com.example.demo.bookstore.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class DataGenerator {

    public static List<Book> generateBookData(String[] args) {
        return Arrays.asList(
                Book.builder()
                        .title("The Pragmatic Programmer")
                        .author("Andrew Hunt & David Thomas")
                        .price(45.99)
                        .publishDate(getDate(1999, Calendar.OCTOBER, 30))
                        .publisher("Addison-Wesley")
                        .description("A guide to becoming a better programmer with practical advice.")
                        .build(),

                Book.builder()
                        .title("Clean Code")
                        .author("Robert C. Martin")
                        .price(39.99)
                        .publishDate(getDate(2008, Calendar.AUGUST, 1))
                        .publisher("Prentice Hall")
                        .description("A handbook of agile software craftsmanship.")
                        .build(),

                Book.builder()
                        .title("Effective Java")
                        .author("Joshua Bloch")
                        .price(49.50)
                        .publishDate(getDate(2018, Calendar.JANUARY, 6))
                        .publisher("Addison-Wesley")
                        .description("Best practices for Java programming.")
                        .build(),

                Book.builder()
                        .title("Design Patterns")
                        .author("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides")
                        .price(59.95)
                        .publishDate(getDate(1994, Calendar.OCTOBER, 31))
                        .publisher("Addison-Wesley")
                        .description("Elements of reusable object-oriented software.")
                        .build(),

                Book.builder()
                        .title("Java Concurrency in Practice")
                        .author("Brian Goetz")
                        .price(42.75)
                        .publishDate(getDate(2006, Calendar.MAY, 19))
                        .publisher("Addison-Wesley")
                        .description("Comprehensive guide to writing robust concurrent programs in Java.")
                        .build()
        );

    }

    private static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}