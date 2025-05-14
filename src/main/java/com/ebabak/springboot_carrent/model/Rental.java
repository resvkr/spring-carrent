package com.ebabak.springboot_carrent.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {


    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String rentDate;
    private String returnDate;
}
