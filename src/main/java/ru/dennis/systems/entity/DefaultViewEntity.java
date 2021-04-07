package ru.dennis.systems.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class DefaultViewEntity {
    private  static int nextVal = 0;
    @Id
    @Column(updatable = false, insertable = false)
    private Long id = random();

    private static long random() {
        return ++nextVal;
    }

    public void setId(Long id){

    }

    public Long getId(){
        return id;
    }
}
