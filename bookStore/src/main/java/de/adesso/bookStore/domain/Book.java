package de.adesso.bookStore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(min=2, max=30)
    private String title;
    @Size(min=2, max=20)
    private String author;
    @Min(1000)
    @Max(2050)
    private int year;
    @Min(1)
    private double price;
    @Min(0)
    private int amount;

}
