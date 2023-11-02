package top.kekdev.blockchain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Transaction {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String data;

    public Transaction(UUID id, String data) {
        this.id = id;
        this.data = data;
    }

    public Transaction(String data) {
        this.data = data;
    }

    public Transaction() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
