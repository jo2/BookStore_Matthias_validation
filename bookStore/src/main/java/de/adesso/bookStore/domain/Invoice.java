package de.adesso.bookStore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    @Id
    private int id;
    private LocalDate invoiceDate;
    private LocalTime invoiceDateTime;
    private double invoiceTotal;

}
