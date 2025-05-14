package com.ebabak.springboot_carrent.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.util.Map;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.HashMap;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    @Builder.Default
    private boolean deleted = false;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
}
