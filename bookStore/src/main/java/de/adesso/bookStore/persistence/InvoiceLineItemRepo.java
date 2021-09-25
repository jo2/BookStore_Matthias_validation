package de.adesso.bookStore.persistence;

import de.adesso.bookStore.domain.InvoiceLineItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface InvoiceLineItemRepo extends CrudRepository<InvoiceLineItem, Integer> {

    List<InvoiceLineItem> findByInvoiceId(int id);
    List<InvoiceLineItem> findByBought(boolean bought);
    void deleteByBought(boolean bought);
    @Override
    InvoiceLineItem save(InvoiceLineItem item);

}
