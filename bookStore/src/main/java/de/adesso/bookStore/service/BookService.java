package de.adesso.bookStore.service;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.persistence.BookRepo;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@Setter
public class BookService {

    @Autowired
    private BookRepo bookRepo;

    public List<Book> findAll() {
        return bookRepo.findAll();
    }

    public List<Book> findAllSortedByTitle() {
        return findAll()
                .stream()
                .sorted(Comparator.comparing(Book::getTitle).reversed())
                .collect(Collectors.toList());
    }

    public Book findById(int id) {
        return bookRepo.findById(id);
    }

    public void save(Book book) {
        bookRepo.save(book);
    }

    public void update(Book changedBook, int id) {
        Book book = bookRepo.findById(id);
        changedBook.setId(book.getId());
        bookRepo.save(changedBook);
    }

    public void deleteById(int id) {
        bookRepo.deleteById(id);
    }

    public Book findByTitleAndAuthor(String bookTitle, String bookAuthor) {
        return bookRepo.findByTitleAndAuthor(bookTitle, bookAuthor);
    }

    public void validate(Book book, Errors errors) {
        if (differentBookWithSameTitleAuthorExists(book)) {
            errors.rejectValue("title", "title_and_author_must_be_unique",
                    "combination of title and author must be unique");
            errors.rejectValue("author", "title_and_author_must_be_unique",
                    "combination of title and author must be unique");
        }
    }

    public boolean differentBookWithSameTitleAuthorExists(Book book) {
        Book differentBook = findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        return differentBook != null && differentBook.getId() != book.getId();
    }

}
