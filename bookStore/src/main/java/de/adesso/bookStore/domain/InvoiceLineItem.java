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
public class InvoiceLineItem {

    @Id
    private int id;
    @Size(min=2, max=30)
    private String bookTitle;
    @Size(min=2, max=20)
    private String bookAuthor;
    @Min(1)
    private double bookPrice;
    @Min(0)
    @Max(20)
    private double discount;
    @Min(1)
    @Max(6)
    private int amount;
    private double summedCosts;
    private int invoiceId;
    private boolean bought;

}
