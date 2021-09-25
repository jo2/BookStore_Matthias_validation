package de.adesso.bookStore.service;

import de.adesso.bookStore.domain.Book;
import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.persistence.InvoiceLineItemRepo;
import de.adesso.bookStore.persistence.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceLineItemService {

    @Autowired
    InvoiceLineItemRepo invoiceLineItemRepo;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceRepo invoiceRepo;

    @Autowired
    BookService bookService;

    public List<InvoiceLineItem> findRecentItems() {
        return invoiceLineItemRepo.findByBought(false);
    }

    public void fillShoppingCart(int bookId) {
        Book book = bookService.findById(bookId);
        InvoiceLineItem item = new InvoiceLineItem();
        item.setBookTitle(book.getTitle());
        item.setBookAuthor(book.getAuthor());
        item.setBookPrice(book.getPrice());
        item.setDiscount(0.0);
        item.setAmount(1);
        item.setSummedCosts(item.getBookPrice() * item.getAmount());
        item.setBought(false);

        int numberOfItemsInCart = findRecentItems().size();
        if (numberOfItemsInCart < 1) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceTotal(0);
            invoiceRepo.save(invoice);
        }
        item.setInvoiceId(invoiceService.findMaxId());
        invoiceLineItemRepo.save(item);
    }

    public double calculateSum(List<InvoiceLineItem> items) {
        items.forEach(item -> item.setSummedCosts(item.getAmount()*item.getBookPrice()*(1-(item.getDiscount()/100.0))));
        return items
                .stream()
                .map(InvoiceLineItem::getSummedCosts)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public void clearCart() {
        if (findRecentItems().size() > 0) {
            invoiceLineItemRepo.deleteByBought(false);
            invoiceService.deleteRecentInvoice();
        }
    }

    public void buy() {
        List<Book> updatedBooks = new ArrayList<>();

        List<InvoiceLineItem> recentItems = findRecentItems();
        if (recentItems.isEmpty()) {
            return;
        }
        for (InvoiceLineItem item : recentItems) {
            Book book = bookService.findByTitleAndAuthor(item.getBookTitle(), item.getBookAuthor());
            Book updatedBook = new Book();
            boolean alreadyUpadated = false;
            updatedBook.setAmount(book.getAmount());
            for (Book alreadyUpdatedBook: updatedBooks) {
                if (alreadyUpdatedBook.getId() == book.getId()) {
                    updatedBook = alreadyUpdatedBook;
                    alreadyUpadated = true;
                    break;
                }
            }
            updatedBook.setId(book.getId());
            updatedBook.setTitle(book.getTitle());
            updatedBook.setAuthor(book.getAuthor());
            updatedBook.setPrice(book.getPrice());
            updatedBook.setYear(book.getYear());
            if (updatedBook.getAmount()- item.getAmount() < 0) {
                return;
            }
            updatedBook.setAmount(updatedBook.getAmount()- item.getAmount());
            if (alreadyUpadated) {
                for (Book alreadyUpdatedBook: updatedBooks) {
                    if (alreadyUpdatedBook.getId() == book.getId()) {
                        updatedBooks.remove(alreadyUpdatedBook);
                        break;
                    }
                }
            }
            updatedBooks.add(updatedBook);

        }
        invoiceService.buy();
        recentItems.forEach(item -> {
            item.setBought(true);
            invoiceLineItemRepo.save(item);
        });
        updatedBooks.forEach(book -> {
            bookService.save(book);
        });
    }

    public List<InvoiceLineItem> findItemsByInvoice(Invoice invoice) {
        return invoiceLineItemRepo.findByInvoiceId(invoice.getId());
    }

    public void save(InvoiceLineItem item) {
        invoiceLineItemRepo.save(item);
    }

}
