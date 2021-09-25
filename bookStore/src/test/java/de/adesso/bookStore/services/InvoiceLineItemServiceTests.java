package de.adesso.bookStore.services;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.persistence.InvoiceLineItemRepo;
import de.adesso.bookStore.persistence.InvoiceRepo;
import de.adesso.bookStore.service.BookService;
import de.adesso.bookStore.service.InvoiceLineItemService;
import de.adesso.bookStore.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceLineItemServiceTests {

    @InjectMocks
    private InvoiceLineItemService invoiceLineItemService;

    @Mock
    private InvoiceLineItemRepo invoiceLineItemRepo;

    @Mock
    private InvoiceRepo invoiceRepo;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private BookService bookService;

    @Test
    void testFindRecentItems() {
        InvoiceLineItem invoiceLineItem1 = new InvoiceLineItem();
        InvoiceLineItem invoiceLineItem2 = new InvoiceLineItem();
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(invoiceLineItem1, invoiceLineItem2));

        List<InvoiceLineItem> recentItems = invoiceLineItemService.findRecentItems();

        assertThat(recentItems).isEqualTo(List.of(invoiceLineItem1, invoiceLineItem2));
    }

    @Test
    void testFillShoppingCartFirstItem() {
        Book book = new Book();
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setPrice(25.0);
        when(bookService.findById(1)).thenReturn(book);
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(new ArrayList<>());
        when(invoiceService.findMaxId()).thenReturn(1);
        InvoiceLineItem invoiceLineItem = new InvoiceLineItem();
        invoiceLineItem.setBookTitle("Book 1");
        invoiceLineItem.setBookAuthor("Author 1");
        invoiceLineItem.setBookPrice(25.0);
        invoiceLineItem.setDiscount(0.0);
        invoiceLineItem.setAmount(1);
        invoiceLineItem.setSummedCosts(25.0);
        invoiceLineItem.setBought(false);
        invoiceLineItem.setInvoiceId(1);

        invoiceLineItemService.fillShoppingCart(1);

        verify(invoiceRepo).save(any(Invoice.class));
        verify(invoiceLineItemRepo).save(invoiceLineItem);
    }

    @Test
    void testFillShoppingCartNotFirstItem() {
        Book book = new Book();
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setPrice(25.0);
        when(bookService.findById(1)).thenReturn(book);
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(new InvoiceLineItem()));
        when(invoiceService.findMaxId()).thenReturn(1);
        InvoiceLineItem invoiceLineItem = new InvoiceLineItem();
        invoiceLineItem.setBookTitle("Book 1");
        invoiceLineItem.setBookAuthor("Author 1");
        invoiceLineItem.setBookPrice(25.0);
        invoiceLineItem.setDiscount(0.0);
        invoiceLineItem.setAmount(1);
        invoiceLineItem.setSummedCosts(25.0);
        invoiceLineItem.setBought(false);
        invoiceLineItem.setInvoiceId(1);

        invoiceLineItemService.fillShoppingCart(1);

        verifyNoInteractions(invoiceRepo);
        verify(invoiceLineItemRepo).save(invoiceLineItem);
    }

    @Test
    void calculateSumOneItem() {
        InvoiceLineItem item1 = new InvoiceLineItem();
        item1.setAmount(5);
        item1.setBookPrice(2.43);

        double sum = invoiceLineItemService.calculateSum(List.of(item1));

        assertThat(sum).isEqualTo(12.15);
    }

    @Test
    void calculateSumManyItems() {
        InvoiceLineItem item1 = new InvoiceLineItem();
        item1.setAmount(6);
        item1.setBookPrice(2);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setAmount(0);
        item2.setBookPrice(1.44);
        InvoiceLineItem item3 = new InvoiceLineItem();
        item3.setAmount(1);
        item3.setBookPrice(20.78);
        InvoiceLineItem item4 = new InvoiceLineItem();
        item4.setAmount(10);
        item4.setBookPrice(2.5);

        double sum = invoiceLineItemService.calculateSum(List.of(item1, item2, item3, item4));

        assertThat(sum).isEqualTo(57.78);
    }

    @Test
    void testClearCart() {
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(new InvoiceLineItem()));

        invoiceLineItemService.clearCart();

        verify(invoiceLineItemRepo).deleteByBought(false);
        verify(invoiceService).deleteRecentInvoice();
    }

    @Test
    void testClearEmptyCart() {
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(new ArrayList<>());

        invoiceLineItemService.clearCart();

        verify(invoiceLineItemRepo).findByBought(false);
        verifyNoMoreInteractions(invoiceLineItemRepo);
        verifyNoInteractions(invoiceService);
    }

    @Test
    void testBuyNoInvoiceLineItems() {
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(new ArrayList<>());

        invoiceLineItemService.buy();

        verify(invoiceLineItemRepo).findByBought(false);
        verifyNoMoreInteractions(invoiceLineItemRepo);
        verifyNoInteractions(bookService);
    }

    @Test
    void testBuySuccessfulDifferentBooks() {
        InvoiceLineItem item1 = new InvoiceLineItem();
        item1.setBookAuthor("Author 1");
        item1.setBookTitle("Title 1");
        item1.setAmount(5);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setBookTitle("Title 2");
        item2.setBookAuthor("Author 2");
        item2.setAmount(1);
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title 1");
        book1.setAuthor("Author 1");
        book1.setAmount(5);
        Book book2 = new Book();
        book2.setId(2);
        book2.setAmount(3);
        book2.setTitle("Title 2");
        book2.setAuthor("Author 2");
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(item1, item2));
        when(bookService.findByTitleAndAuthor("Title 1", "Author 1")).thenReturn(book1);
        when(bookService.findByTitleAndAuthor("Title 2", "Author 2")).thenReturn(book2);

        invoiceLineItemService.buy();

        Book savedBook1 = new Book();
        savedBook1.setId(1);
        savedBook1.setAuthor("Author 1");
        savedBook1.setTitle("Title 1");
        savedBook1.setAmount(0);
        Book savedBook2 = new Book();
        savedBook2.setId(2);
        savedBook2.setAuthor("Author 2");
        savedBook2.setTitle("Title 2");
        savedBook2.setAmount(2);
        InvoiceLineItem savedItem1 = new InvoiceLineItem();
        savedItem1.setBookAuthor("Author 1");
        savedItem1.setBookTitle("Title 1");
        savedItem1.setAmount(5);
        savedItem1.setBought(true);
        InvoiceLineItem savedItem2 = new InvoiceLineItem();
        savedItem2.setBookTitle("Title 2");
        savedItem2.setBookAuthor("Author 2");
        savedItem2.setAmount(1);
        savedItem2.setBought(true);
        verify(invoiceService).buy();
        verify(invoiceLineItemRepo).save(savedItem1);
        verify(invoiceLineItemRepo).save(savedItem2);
        verify(bookService).save(savedBook1);
        verify(bookService).save(savedBook2);
    }

    @Test
    void testBuySuccessfulSameBook() {
        InvoiceLineItem item1 = new InvoiceLineItem();
        item1.setBookAuthor("Author 1");
        item1.setBookTitle("Title 1");
        item1.setAmount(3);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setBookTitle("Title 1");
        item2.setBookAuthor("Author 1");
        item2.setAmount(2);
        Book book1 = new Book();
        book1.setTitle("Title 1");
        book1.setAuthor("Author 1");
        book1.setAmount(5);
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(item1, item2));
        when(bookService.findByTitleAndAuthor("Title 1", "Author 1")).thenReturn(book1);

        invoiceLineItemService.buy();

        Book savedBook = new Book();
        savedBook.setAuthor("Author 1");
        savedBook.setTitle("Title 1");
        savedBook.setAmount(0);
        InvoiceLineItem savedItem1 = new InvoiceLineItem();
        savedItem1.setBookAuthor("Author 1");
        savedItem1.setBookTitle("Title 1");
        savedItem1.setAmount(3);
        savedItem1.setBought(true);
        InvoiceLineItem savedItem2 = new InvoiceLineItem();
        savedItem2.setBookTitle("Title 1");
        savedItem2.setBookAuthor("Author 1");
        savedItem2.setAmount(2);
        savedItem2.setBought(true);
        verify(invoiceService).buy();
        verify(invoiceLineItemRepo).save(savedItem1);
        verify(invoiceLineItemRepo).save(savedItem2);
        verify(bookService).save(savedBook);
    }

    @Test
    void testBuyUnsuccessful() {
        InvoiceLineItem item1 = new InvoiceLineItem();
        item1.setBookAuthor("Author 1");
        item1.setBookTitle("Title 1");
        item1.setAmount(5);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setBookTitle("Title 2");
        item2.setBookAuthor("Author 2");
        item2.setAmount(1);
        InvoiceLineItem item3 = new InvoiceLineItem();
        item3.setBookTitle("Title 2");
        item3.setBookAuthor("Author 2");
        item3.setAmount(3);
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title 1");
        book1.setAuthor("Author 1");
        book1.setAmount(5);
        Book book2 = new Book();
        book2.setId(2);
        book2.setAmount(3);
        book2.setTitle("Title 2");
        book2.setAuthor("Author 2");
        when(invoiceLineItemRepo.findByBought(false)).thenReturn(List.of(item1, item2, item3));
        when(bookService.findByTitleAndAuthor("Title 1", "Author 1")).thenReturn(book1);
        when(bookService.findByTitleAndAuthor("Title 2", "Author 2")).thenReturn(book2);

        invoiceLineItemService.buy();

        verifyNoInteractions(invoiceService);
        verify(invoiceLineItemRepo).findByBought(false);
        verifyNoMoreInteractions(invoiceLineItemRepo);
        verify(bookService).findByTitleAndAuthor("Title 1", "Author 1");
        verify(bookService,times(2)).findByTitleAndAuthor("Title 2", "Author 2");
        verifyNoMoreInteractions(bookService);
        assertThat(book1.getAmount()).isEqualTo(5);
        assertThat(book2.getAmount()).isEqualTo(3);
    }

    @Test
    void testFindItemsByInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(1);

        invoiceLineItemService.findItemsByInvoice(invoice);

        verify(invoiceLineItemRepo).findByInvoiceId(1);
    }

    @Test
    void testSave() {
        InvoiceLineItem item = new InvoiceLineItem();

        invoiceLineItemService.save(item);

        verify(invoiceLineItemRepo).save(item);
    }

}
