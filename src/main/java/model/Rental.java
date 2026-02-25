package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rental {
    private int id;
    private int bookId;
    private int customerId;
    private LocalDate date;
    private LocalDate returnDate;
    private double fine;
}