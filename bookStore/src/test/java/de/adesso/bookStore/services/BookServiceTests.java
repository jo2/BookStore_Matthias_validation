package de.adesso.bookStore.services;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.persistence.BookRepo;
import de.adesso.bookStore.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @Mock
    private Errors errors;

    @Test
    void testFindAll() {
        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();
        List<Book> books = List.of(book1, book2, book3);
        when(bookRepo.findAll()).thenReturn(books);

        List<Book> foundBooks = bookService.findAll();

        assertThat(foundBooks).isEqualTo(List.of(book2, book1, book3));
    }

    @Test
    void testFindAllSortedByTitle() {
        Book book1 = new Book();
        book1.setTitle("Ende der Liste");
        Book book2 = new Book();
        book2.setTitle("Mitte der Liste");
        Book book3 = new Book();
        book3.setTitle("Start der Liste");
        List<Book> books = List.of(book1, book2, book3);
        when(bookRepo.findAll()).thenReturn(books);

        List<Book> foundBooks = bookService.findAllSortedByTitle();
        System.out.println(foundBooks);
        assertThat(foundBooks).isEqualTo(List.of(book3, book2, book1));
    }

    @Test
    void testFindById() {
        Book book1 = new Book();
        when(bookRepo.findById(1)).thenReturn(book1);

        Book foundBook = bookService.findById(1);

        assertThat(foundBook).isEqualTo(book1);
    }

    @Test
    void testSave() {
        Book book1 = new Book();

        bookService.save(book1);

        verify(bookRepo).save(book1);
    }

    @Test
    void testUpdate() {
        Book book1 = new Book();
        book1.setId(1);
        Book updatedBook = new Book();
        updatedBook.setTitle("Book 1");
        when(bookRepo.findById(1)).thenReturn(book1);

        bookService.update(updatedBook, 1);

        verify(bookRepo).save(updatedBook);
        assertThat(updatedBook.getId()).isEqualTo(1);
    }

    @Test
    void testDeleteById() {
        bookService.deleteById(1);

        verify(bookRepo).deleteById(1);
    }

    @Test
    void testFindByTitleAndAuthor() {
        Book book1 = new Book();
        when(bookRepo.findByTitleAndAuthor("Book 1", "Author 1")).thenReturn(book1);

        Book foundBook = bookService.findByTitleAndAuthor("Book 1", "Author 1");

        assertThat(foundBook).isEqualTo(book1);
    }

    @Test
    void testValidate() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setId(1);
        Book differentBook = new Book();
        differentBook.setTitle("Book 2");
        differentBook.setAuthor("Author 1");
        differentBook.setId(2);
        when(bookRepo.findByTitleAndAuthor("Book 1", "Author 1")).thenReturn(differentBook);

        bookService.validate(book1, errors);

        verify(errors).rejectValue("title", "title_and_author_must_be_unique",
                "combination of title and author must be unique");
        verify(errors).rejectValue("author", "title_and_author_must_be_unique",
                "combination of title and author must be unique");
    }

    @Test
    void testdifferentBookWithSameTitleAuthorExistsTrue() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setId(1);
        Book differentBook = new Book();
        differentBook.setTitle("Book 2");
        differentBook.setAuthor("Author 1");
        differentBook.setId(2);
        when(bookRepo.findByTitleAndAuthor("Book 1", "Author 1")).thenReturn(differentBook);

        boolean bookExists = bookService.differentBookWithSameTitleAuthorExists(book1);

        assertThat(bookExists).isTrue();
    }

    @Test
    void testdifferentBookWithSameTitleAuthorExistsFalse() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        Book differentBook = new Book();
        differentBook.setTitle("Book 1");
        differentBook.setAuthor("Author 1");
        when(bookRepo.findByTitleAndAuthor("Book 1", "Author 1")).thenReturn(differentBook);

        boolean bookExists = bookService.differentBookWithSameTitleAuthorExists(book1);

        assertThat(bookExists).isFalse();
    }

}
