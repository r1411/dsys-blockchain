package top.kekdev.blockchain.model;

import jakarta.persistence.*;

@Entity
public class Block {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long index;

    @Column
    private Long creationTime;

    @Column
    private String previousBlockHash;

    @Column
    private String hash;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Transaction transaction;

    @Column
    private Long nonce;

    public Block(Long index, Long creationTime, String previousBlockHash, String hash, Transaction transaction) {
        this.index = index;
        this.creationTime = creationTime;
        this.previousBlockHash = previousBlockHash;
        this.hash = hash;
        this.transaction = transaction;
    }

    public Block() {

    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }
}
