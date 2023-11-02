package top.kekdev.blockchain.dto;

import top.kekdev.blockchain.model.Block;

import java.util.UUID;

public class BlockResponseDto {
    private Long creationTime;

    private String previousBlockHash;

    private String hash;

    private Long nonce;

    private UUID transactionId;

    private String data;

    public static BlockResponseDto fromBlock(Block block) {
        BlockResponseDto blockResponseDto = new BlockResponseDto();
        blockResponseDto.setCreationTime(block.getCreationTime());
        blockResponseDto.setData(block.getTransaction().getData());
        blockResponseDto.setHash(block.getHash());
        blockResponseDto.setNonce(block.getNonce());
        blockResponseDto.setPreviousBlockHash(block.getPreviousBlockHash());
        blockResponseDto.setTransactionId(block.getTransaction().getId());
        return blockResponseDto;
    }

    public BlockResponseDto(Long creationTime, String previousBlockHash, String hash, Long nonce, UUID transactionId, String data) {
        this.creationTime = creationTime;
        this.previousBlockHash = previousBlockHash;
        this.hash = hash;
        this.nonce = nonce;
        this.transactionId = transactionId;
        this.data = data;
    }

    public BlockResponseDto() {

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
}
