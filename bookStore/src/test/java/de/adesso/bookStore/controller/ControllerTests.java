package de.adesso.bookStore.controller;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.service.BookService;
import de.adesso.bookStore.service.InvoiceLineItemService;
import de.adesso.bookStore.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebController.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private InvoiceLineItemService invoiceLineItemService;

    @Test
    void index() throws Exception {
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title1");
        book1.setAuthor("Author1");
        book1.setPrice(10);
        book1.setYear(2000);
        book1.setAmount(10);
        Book book2 = new Book();
        book2.setId(1);
        book2.setTitle("Title2");
        book2.setAuthor("Author2");
        book2.setPrice(10);
        book2.setYear(2000);
        book2.setAmount(10);
        Book book3 = new Book();
        book3.setId(1);
        book3.setTitle("Title3");
        book3.setAuthor("Author3");
        book3.setPrice(10);
        book3.setYear(2000);
        book3.setAmount(10);
        when(bookService.findAllSortedByTitle()).thenReturn(List.of(book1, book2, book3));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1> BookStore </h1>")))
                .andExpect(content().string(containsString("Title1")))
                .andExpect(content().string(containsString("Title2")))
                .andExpect(content().string(containsString("Title3")))
                .andExpect(content().string(containsString("Author1")))
                .andExpect(content().string(containsString("Author2")))
                .andExpect(content().string(containsString("Author3")))
                .andExpect(content().string(containsString("2000")))
                .andExpect(content().string(containsString("10")));
    }

    @Test
    void accounting() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoice.setInvoiceDate(LocalDate.of(2020,1,1));
        invoice.setInvoiceDateTime(LocalTime.of(2,30));
        invoice.setInvoiceTotal(50.32);
        Invoice invoice2 = new Invoice();
        invoice2.setId(1);
        invoice2.setInvoiceDate(LocalDate.of(2000,11,11));
        invoice2.setInvoiceDateTime(LocalTime.of(6, 15));
        invoice2.setInvoiceTotal(30);
        InvoiceLineItem item = new InvoiceLineItem();
        item.setInvoiceId(1);
        item.setBookTitle("Title1");
        item.setBookAuthor("Author1");
        item.setBookPrice(5);
        item.setSummedCosts(10);
        item.setAmount(4);
        item.setDiscount(7);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setInvoiceId(1);
        item2.setBookTitle("Title1");
        item2.setBookAuthor("Author1");
        item2.setBookPrice(5);
        item2.setSummedCosts(10);
        item2.setAmount(4);
        item2.setDiscount(7);
        InvoiceLineItem item3 = new InvoiceLineItem();
        item3.setInvoiceId(2);
        item3.setBookTitle("Title2");
        item3.setBookAuthor("Author2");
        item3.setBookPrice(5);
        item3.setSummedCosts(10);
        item3.setAmount(4);
        item3.setDiscount(7);
        when(invoiceLineItemService.findItemsByInvoice(any())).thenReturn(List.of(item, item2, item3));
        when(invoiceService.findBoughtInvoices()).thenReturn(List.of(invoice, invoice2));

        mockMvc.perform(get("/accounting"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3> Accounting </h3>")))
                .andExpect(content().string(containsString("Title1 by Author1")))
                .andExpect(content().string(containsString("50.32 €")));
    }

    @Test
    void createBook() throws Exception {
        mockMvc.perform(get("/createBook"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3> Create new Book </h3>")))
                .andExpect(content().string(containsString("<form ")))
                .andExpect(content().string(containsString("Title")))
                .andExpect(content().string(containsString("Year")))
                .andExpect(content().string(containsString("Author")))
                .andExpect(content().string(containsString("Amount")))
                .andExpect(content().string(containsString("Price")))
                .andExpect(content().string(containsString("Confirm")))
                .andExpect(content().string(containsString("Cancel")));
    }

    @Test
    void updateBook() throws Exception {
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title1");
        book1.setAuthor("Author1");
        book1.setPrice(10);
        book1.setYear(2000);
        book1.setAmount(10);
        when(bookService.findById(1)).thenReturn(book1);

        mockMvc.perform(get("/updateBook/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit")))
                .andExpect(content().string(containsString("Title1")));
    }

    @Test
    void bookInfo() throws Exception {
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title1");
        book1.setAuthor("Author1");
        book1.setPrice(10);
        book1.setYear(2000);
        book1.setAmount(10);
        when(bookService.findById(1)).thenReturn(book1);

        mockMvc.perform(get("/bookInfo/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3> Information </h3>")))
                .andExpect(content().string(containsString("Title1")));
    }

    @Test
    void shoppingCart() throws Exception {
        InvoiceLineItem item = new InvoiceLineItem();
        item.setInvoiceId(1);
        item.setBookTitle("Title1");
        item.setBookAuthor("Author1");
        item.setBookPrice(5);
        item.setSummedCosts(10);
        item.setAmount(4);
        item.setDiscount(7);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setInvoiceId(1);
        item2.setBookTitle("Title2");
        item2.setBookAuthor("Author2");
        item2.setBookPrice(5);
        item2.setSummedCosts(10);
        item2.setAmount(4);
        item2.setDiscount(7);
        when(invoiceLineItemService.findRecentItems()).thenReturn(List.of(item, item2));
        when(invoiceLineItemService.calculateSum(any())).thenReturn(100.56);

        mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title1 by Author1")))
                .andExpect(content().string(containsString("Title2 by Author2")))
                .andExpect(content().string(containsString("100.56")));
    }

    @Test
    void createBookSuccsessful() throws Exception{
        Book book1 = new Book();
        book1.setTitle("Title1");
        book1.setAuthor("Author1");
        book1.setPrice(20.0);
        book1.setYear(2002);
        book1.setAmount(10);
        mockMvc.perform(post("/createBook")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Title1&author=Author1&year=2002&price=20.0&amount=10"))
                .andExpect(status().is3xxRedirection());

        verify(bookService).save(book1);
    }

    @Test
    void updateBookSuccsessful() throws Exception{
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title1");
        book1.setAuthor("Author1");
        book1.setPrice(20.0);
        book1.setYear(2002);
        book1.setAmount(10);
        mockMvc.perform(post("/updateBook/1")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Title1&author=Author1&year=2002&price=20.0&amount=10"))
                .andExpect(status().is3xxRedirection());

        verify(bookService).update(book1, 1);
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(post("/deleteBook/1"))
                .andExpect(status().is3xxRedirection());

        verify(bookService).deleteById(1);
    }

    @Test
    void clearCart() throws Exception {
        mockMvc.perform(post("/clearCart"))
                .andExpect(status().is3xxRedirection());

        verify(invoiceLineItemService).clearCart();
    }

    @Test
    void buy() throws Exception {
        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());

        verify(invoiceLineItemService).buy();
    }

    @Test
    void fillshoppingCart() throws Exception {
        mockMvc.perform(post("/shoppingCart/1"))
                .andExpect(status().is3xxRedirection());

        verify(invoiceLineItemService).fillShoppingCart(1);
    }

    @Test
    void modifyCart() throws Exception {
        InvoiceLineItem item = new InvoiceLineItem();
        item.setBookTitle("Title1");
        item.setBookAuthor("Author1");
        item.setBookPrice(20.0);
        item.setInvoiceId(2);
        item.setSummedCosts(40);
        item.setAmount(1);
        mockMvc.perform(post("/modifyCart")
                .contentType("application/x-www-form-urlencoded")
                .content("bookTitle=Title1&bookAuthor=Author1&invoiceId=2&bookPrice=20.0&amount=1&summedCosts=40"))
                .andExpect(status().is3xxRedirection());

        verify(invoiceLineItemService).save(item);
    }

}
