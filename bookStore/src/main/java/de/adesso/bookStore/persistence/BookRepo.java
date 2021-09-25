package de.adesso.bookStore.persistence;

import de.adesso.bookStore.domain.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface BookRepo extends CrudRepository<Book, Integer> {

    @Override
    Book save(Book book);

    Book findById(int id);

    void deleteById(int id);

    @Override
    List<Book> findAll();

    Book findByTitleAndAuthor(String title, String author);

}
