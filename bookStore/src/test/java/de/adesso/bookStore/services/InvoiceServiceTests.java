package de.adesso.bookStore.services;

import de.adesso.bookStore.domain.Invoice;
import de.adesso.bookStore.domain.InvoiceLineItem;
import de.adesso.bookStore.persistence.InvoiceRepo;
import de.adesso.bookStore.service.InvoiceLineItemService;
import de.adesso.bookStore.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTests {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepo invoiceRepo;

    @Mock
    private InvoiceLineItemService invoiceLineItemService;

    @Test
    void testFindMaxId() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1);
        Invoice invoice2 = new Invoice();
        invoice2.setId(5);
        Invoice invoice3 = new Invoice();
        invoice3.setId(3);
        List<Invoice> invoices = List.of(invoice1, invoice2, invoice3);
        when(invoiceRepo.findAll()).thenReturn(invoices);

        int maxId = invoiceService.findMaxId();

        assertThat(maxId).isEqualTo(5);
    }

    @Test
    void testFindAll() {
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Invoice invoice3 = new Invoice();
        List<Invoice> invoices = List.of(invoice1, invoice2, invoice3);
        when(invoiceRepo.findAll()).thenReturn(invoices);

        List<Invoice> foundInvoices = invoiceService.findAll();

        assertThat(foundInvoices).isEqualTo(List.of(invoice1, invoice2, invoice3));
    }

    @Test
    void testFindBoughtInvoicesIncludingLastInvoice() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2);
        Invoice invoice3 = new Invoice();
        invoice3.setId(3);
        when(invoiceRepo.findByIdLessThan(3)).thenReturn(List.of(invoice1, invoice2));
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice2, invoice3, invoice1));
        when(invoiceLineItemService.findRecentItems()).thenReturn(new ArrayList<>());
        when(invoiceRepo.findById(3)).thenReturn(Optional.of(invoice3));

        List<Invoice> foundInvoices = invoiceService.findBoughtInvoices();

        assertThat(foundInvoices).containsOnly(invoice1, invoice2, invoice3);
    }

    @Test
    void testFindBoughtInvoicesFindRecentItemsIsNotEmpty() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2);
        Invoice invoice3 = new Invoice();
        invoice3.setId(3);
        when(invoiceRepo.findByIdLessThan(3)).thenReturn(List.of(invoice1, invoice2));
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice2, invoice3, invoice1));
        when(invoiceLineItemService.findRecentItems()).thenReturn(List.of(new InvoiceLineItem()));
        when(invoiceRepo.findById(3)).thenReturn(Optional.of(invoice3));

        List<Invoice> foundInvoices = invoiceService.findBoughtInvoices();

        assertThat(foundInvoices).containsOnly(invoice1, invoice2);
    }

    @Test
    void testDeleteRecentInvoiceSuccessful() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2);
        Invoice invoice3 = new Invoice();
        invoice3.setId(3);
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice3, invoice1, invoice2));
        when(invoiceRepo.findById(3)).thenReturn(Optional.of(invoice3));

        invoiceService.deleteRecentInvoice();

        verify(invoiceRepo).delete(invoice3);
    }

    @Test
    void testDeleteRecentInvoiceUnsuccessful() {
        Invoice invoice1 = new Invoice();
        invoice1.setId(1);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2);
        Invoice invoice3 = new Invoice();
        invoice3.setId(3);
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice3, invoice1, invoice2));
        when(invoiceRepo.findById(3)).thenReturn(Optional.ofNullable(null));

        invoiceService.deleteRecentInvoice();

        verify(invoiceRepo).findAll();
        verify(invoiceRepo).findById(3);
        verifyNoMoreInteractions(invoiceRepo);
    }

    @Test
    void testFindById() {
        Invoice invoice1 = new Invoice();
        when(invoiceRepo.findById(1)).thenReturn(Optional.of(invoice1));

        Invoice foundInvoice = invoiceService.findById(1);

        assertThat(foundInvoice).isEqualTo(invoice1);
    }

    @Test
    void testBuyInvoicePresent() {
        Invoice invoice = new Invoice();
        invoice.setId(1);
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));
        when(invoiceRepo.findById(1)).thenReturn(Optional.of(invoice));
        when(invoiceLineItemService.findRecentItems()).thenReturn(new ArrayList<>());
        when(invoiceLineItemService.calculateSum(any())).thenReturn(50.0);

        Invoice savedInvoice = invoiceService.buy();

        verify(invoiceRepo).save(savedInvoice);
        assertThat(savedInvoice.getInvoiceTotal()).isEqualTo(50.0);
        assertThat(savedInvoice.getInvoiceDate()).isBetween(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertThat(savedInvoice.getInvoiceDateTime()).isNotNull();
    }

    @Test
    void testBuyInvoiceNotPresent() {
        Invoice invoice = new Invoice();
        invoice.setId(1);
        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));
        when(invoiceRepo.findById(1)).thenReturn(Optional.ofNullable(null));

        invoiceService.buy();

        verify(invoiceRepo).findAll();
        verify(invoiceRepo).findById(1);
        verifyNoMoreInteractions(invoiceRepo);
    }

}
