package de.adesso.bookStore.persistence;

import de.adesso.bookStore.domain.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface InvoiceRepo extends CrudRepository<Invoice, Integer> {

    List<Invoice> findAll();
    Invoice save(Invoice invoice);
    List<Invoice> findByIdLessThan(int id);

}
