package de.adesso.bookStore.controller;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.service.BookService;
import de.adesso.bookStore.service.InvoiceLineItemService;
import de.adesso.bookStore.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    BookService bookService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceLineItemService invoiceLineItemService;

    @GetMapping("/")
    public String index(final Model model) {
        List<Book> books = bookService.findAllSortedByTitle();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/accounting")
    public String accounting(final Model model) {
        List<Invoice> invoices = invoiceService.findBoughtInvoices();
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceLineItemService", invoiceLineItemService);
        return "accounting";
    }

    @GetMapping("/createBook")
    public String createBook(Model model) {
        model.addAttribute("book", new Book());
        return "createBook";
    }

    @GetMapping("updateBook/{id}")
    public String updateBook(@PathVariable("id") final int id, final Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "updateOneBook";
    }

    @GetMapping("shoppingCart")
    public String shoppingCart(final Model model) {
        List<InvoiceLineItem> items = invoiceLineItemService.findRecentItems();
        double sum = invoiceLineItemService.calculateSum(items);
        model.addAttribute("items", items);
        model.addAttribute("sum", sum);
        return "shoppingCart";
    }

    @GetMapping("bookInfo/{id}")
    public String bookInfo(@PathVariable("id") final int id, final Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        return "bookInfo";
    }

    @PostMapping("updateBook/{id}")
    public String updateBook(@PathVariable("id") final int id, @ModelAttribute final Book changedBook,
                             final Errors errors) {
        bookService.validate(changedBook, errors);
        if (errors.hasErrors()) {
            return "redirect:/updateBook/{id}";
        }
        bookService.update(changedBook, id);
        return "redirect:/";
    }

    @PostMapping("/createBook")
    public String createBook(@Valid @ModelAttribute final Book book, final Errors errors, final Model model) {
        model.addAttribute("book", book);
        bookService.validate(book, errors);
        if (errors.hasErrors()) {
            return "createBook";
        }
        bookService.save(book);
        return "redirect:/";
    }

    @PostMapping("deleteBook/{id}")
    public String deleteBook(@PathVariable("id") final int id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("shoppingCart/{bookId}")
    public String shoppingCart(@PathVariable("bookId") final int bookId) {
        invoiceLineItemService.fillShoppingCart(bookId);
        return "redirect:/";
    }

    @PostMapping("clearCart")
    public String clearCart() {
        invoiceLineItemService.clearCart();
        return "redirect:/shoppingCart";
    }

    @PostMapping("buy")
    public String buy() {
        invoiceLineItemService.buy();
        return "redirect:/shoppingCart";
    }

    @PostMapping("modifyCart")
    public String modifyCart(@Valid @ModelAttribute final InvoiceLineItem invoiceLineItem, final Errors errors) {
        if (!errors.hasErrors()) {
            invoiceLineItemService.save(invoiceLineItem);
        }
        return "redirect:/shoppingCart";
    }


}
