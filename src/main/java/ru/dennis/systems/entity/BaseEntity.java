package ru.dennis.systems.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class BaseEntity implements DefaultEntity{
    @Id
    @GeneratedValue (strategy = GenerationType.TABLE)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
}
