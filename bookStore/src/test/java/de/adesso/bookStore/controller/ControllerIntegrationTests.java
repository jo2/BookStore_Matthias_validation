package de.adesso.bookStore.controller;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.persistence.BookRepo;
import de.adesso.bookStore.persistence.InvoiceLineItemRepo;
import de.adesso.bookStore.persistence.InvoiceRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("dummy")
@SpringBootTest(classes = de.adesso.bookStore.TestApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    private InvoiceLineItemRepo invoiceLineItemRepo;

    @BeforeEach
    void setUp() {
        bookRepo.deleteAll();
        invoiceLineItemRepo.deleteAll();
        invoiceRepo.deleteAll();
        Book book = new Book();
        book.setId(901);
        book.setAmount(3);
        book.setPrice(10);
        book.setYear(2000);
        book.setAuthor("Author1");
        book.setTitle("Title1");

        Book book2 = new Book();
        book2.setId(902);
        book2.setAmount(5);
        book2.setPrice(25);
        book2.setYear(2010);
        book2.setAuthor("Author2");
        book2.setTitle("Title2");

        Book book3 = new Book();
        book3.setId(903);
        book3.setAmount(10);
        book3.setPrice(50);
        book3.setYear(1999);
        book3.setAuthor("Author3");
        book3.setTitle("Title3");
        bookRepo.saveAll(List.of(
                book,
                book2,
                book3
        ));
        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoice.setInvoiceTotal(20);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setInvoiceDateTime(LocalTime.now());
        invoiceRepo.save(invoice);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2);
        invoice2.setInvoiceTotal(0);
        invoice2.setInvoiceDate(LocalDate.now());
        invoice2.setInvoiceDateTime(LocalTime.now());
        invoiceRepo.save(invoice2);
        InvoiceLineItem item = new InvoiceLineItem();
        item.setId(1);
        item.setBookAuthor("Author1");
        item.setBookTitle("Title1");
        item.setBookPrice(10);
        item.setDiscount(10);
        item.setInvoiceId(1);
        item.setAmount(2);
        item.setSummedCosts(20);
        item.setBought(true);
        InvoiceLineItem item2 = new InvoiceLineItem();
        item2.setId(2);
        item2.setBookAuthor("Author1");
        item2.setBookTitle("Title1");
        item2.setBookPrice(10);
        item2.setDiscount(10);
        item2.setInvoiceId(2);
        item2.setAmount(1);
        item2.setSummedCosts(10);
        item2.setBought(false);
        InvoiceLineItem item3 = new InvoiceLineItem();
        item3.setId(3);
        item3.setBookAuthor("Author2");
        item3.setBookTitle("Title2");
        item3.setBookPrice(25);
        item3.setDiscount(10);
        item3.setInvoiceId(2);
        item3.setAmount(5);
        item3.setSummedCosts(125);
        item3.setBought(false);
        invoiceLineItemRepo.saveAll(List.of(item, item2, item3));
    }

    @AfterEach
    void tearDown() {
        bookRepo.deleteAll();
        invoiceLineItemRepo.deleteAll();
        invoiceRepo.deleteAll();
    }

    @Test
    void getIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title1")))
                .andExpect(content().string(containsString("Title2")))
                .andExpect(content().string(containsString("Title3")));
    }

    @Test
    void getAccounting() throws Exception {
        this.mockMvc.perform(get("/accounting"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("20.0 €")))
                .andExpect(content().string(containsString("Title1 by Author1")));
    }

    @Test
    void getUpdateBook() throws Exception {
        this.mockMvc.perform(get("/updateBook/901"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title1")));
    }

    @Test
    void getShoppingCart() throws Exception {
        this.mockMvc.perform(get("/shoppingCart"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title1 by Author1")))
                .andExpect(content().string(containsString("Title2 by Author2")))
                .andExpect(content().string(containsString("Sum: 121.5")));
    }

    @Test
    void getBookInfo() throws Exception {
        this.mockMvc.perform(get("/bookInfo/901"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title1")))
                .andExpect(content().string(containsString("Author1")));
    }

    @Test
    void updateBook() throws Exception {
        this.mockMvc.perform(post("/updateBook/901")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Book1&author=Author1&year=2021&price=10&amount=3"))
                .andExpect(status().is3xxRedirection());
        Book book = bookRepo.findById(901);
        assertThat(book.getYear()).isEqualTo(2021);
    }

    @Test
    void createBook() throws Exception {
        this.mockMvc.perform(post("/createBook")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Book1&author=Author1&year=2021&price=10&amount=3&id=1"))
                .andExpect(status().is3xxRedirection());
        int size = bookRepo.findAll().size();
        assertThat(size).isEqualTo(4);
    }

    @Test
    void deleteBook() throws Exception {
        this.mockMvc.perform(post("/deleteBook/901"))
                .andExpect(status().is3xxRedirection());
        int size = bookRepo.findAll().size();
        assertThat(size).isEqualTo(2);
        Book book = bookRepo.findById(901);
        assertThat(book).isNull();
    }

    @Test
    void fillShoppingCart() throws Exception {
        this.mockMvc.perform(post("/shoppingCart/901"))
                .andExpect(status().is3xxRedirection());
        int size = invoiceLineItemRepo.findByBought(false).size();
        assertThat(size).isEqualTo(3);
    }

    @Test
    void clearCart() throws Exception {
        this.mockMvc.perform(post("/clearCart"))
                .andExpect(status().is3xxRedirection());
        int size = invoiceLineItemRepo.findByBought(false).size();
        assertThat(size).isEqualTo(0);
        int invoiceSize = invoiceRepo.findAll().size();
        assertThat(invoiceSize).isEqualTo(1);
    }

    @Test
    void buy() throws Exception {
        this.mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());
        int size = invoiceLineItemRepo.findByBought(false).size();
        assertThat(size).isEqualTo(0);
        int invoiceSize = invoiceRepo.findAll().size();
        assertThat(invoiceSize).isEqualTo(2);
    }

    @Test
    void modifyCart() throws Exception {
        this.mockMvc.perform(post("/modifyCart")
                .contentType("application/x-www-form-urlencoded")
                .content("bookTitle=Title9&bookAuthor=Author1&invoiceId=2&bookPrice=20.0&amount=1&summedCosts=40&id=1"));
        InvoiceLineItem item = invoiceLineItemRepo.findById(1).orElse(null);
        assertThat(item.getBookTitle()).isEqualTo("Title9");
    }

}
