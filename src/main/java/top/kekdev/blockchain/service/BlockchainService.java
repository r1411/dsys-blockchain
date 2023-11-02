package top.kekdev.blockchain.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.kekdev.blockchain.model.Block;
import top.kekdev.blockchain.model.Transaction;
import top.kekdev.blockchain.repository.BlockRepository;
import top.kekdev.blockchain.repository.TransactionRepository;

import java.util.*;

@Service
@Transactional
public class BlockchainService {
    private final static Integer COMPLEXITY = 4;
    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;
    private Block lastBlock;

    @Value("#{'${blockchain.nodes}'.split(',')}")
    private List<String> otherNodes;

    @Autowired
    public BlockchainService(BlockRepository blockRepository, TransactionRepository transactionRepository) {
        this.blockRepository = blockRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    private void postConstruct() {
        lastBlock = blockRepository.findTopByOrderByIndexDesc();
    }

    public Block createBlock(String data) {
        resolveConflicts();
        String prevBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
        if (getLastBlock() != null) {
            prevBlockHash = getLastBlock().getHash();
        }
        Block block = new Block();
        block.setTransaction(new Transaction(data));
        block.setPreviousBlockHash(prevBlockHash);
        block.setCreationTime(new Date().getTime());
        block.setHash(calculateBlockHash(block));
        block.setNonce(calculateBlockNonce(block));
        lastBlock = blockRepository.save(block);
        return lastBlock;
    }

    public Block getLastBlock() {
        return lastBlock;
    }

    public List<Block> getLastNBlocks(int n) {
        PageRequest pageRequest = PageRequest.of(0, n, Sort.Direction.DESC, "index");
        List<Block> result = new ArrayList<>(blockRepository.findAll(pageRequest).getContent());
        result.sort(Comparator.comparingLong(Block::getIndex));
        return result;
    }

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public long getBlocksCount() {
        return blockRepository.count();
    }

    public void resolveConflicts() {
        List<Block> maxChain = Collections.emptyList();
        for (String nodeUrl : otherNodes) {
            if (nodeUrl.isEmpty()) {
                continue;
            }
            try {
                String requestUrl = nodeUrl + "/getBlocks";
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity<?> entity = HttpEntity.EMPTY;
                ResponseEntity<Map> result = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);
                if (result.getStatusCode() != HttpStatus.OK) {
                    continue;
                }

                Map blocksResponse = result.getBody();
                if ((boolean) blocksResponse.get("success")) {
                    Map response = (Map) blocksResponse.get("response");
                    List<Map<String, Object>> blockItems = (List<Map<String, Object>>) response.get("items");
                    List<Block> chain = blockItems.stream()
                            .map(item -> {
                                Block block = new Block();
                                block.setIndex((Long) item.get("index"));
                                block.setCreationTime((Long) item.get("creation_time"));
                                block.setPreviousBlockHash((String) item.get("previous_block_hash"));
                                block.setHash((String) item.get("hash"));
                                block.setTransaction(new Transaction(
                                        UUID.fromString((String) item.get("transaction_id")),
                                        (String) item.get("data")
                                ));
                                block.setNonce(Long.valueOf((Integer) item.get("nonce")));
                                return block;
                            })
                            .toList();

                    if (isValidChain(chain) && chain.size() > maxChain.size()) {
                        maxChain = chain;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (maxChain.size() > getBlocksCount()) {
            List<Block> oldChain = getAllBlocks();
            int changedAtIdx = 0;
            if (oldChain.size() > 0) {
                while (changedAtIdx < oldChain.size() && oldChain.get(changedAtIdx).getHash().equalsIgnoreCase(maxChain.get(changedAtIdx).getHash())) {
                    changedAtIdx += 1;
                }
            }

            for (int i = changedAtIdx; i < maxChain.size(); i++) {
                Block block = maxChain.get(i);
                Transaction transaction = transactionRepository.save(block.getTransaction());
                block.setTransaction(transaction);
                lastBlock = blockRepository.save(block);
            }

            for (int i = changedAtIdx; i < oldChain.size(); i++) {
                createBlock(oldChain.get(i).getTransaction().getData());
            }
        }
    }

    private String calculateBlockHash(Block block) {
        return DigestUtils.sha256Hex(
                block.getCreationTime() +
                        block.getPreviousBlockHash() +
                        block.getTransaction().getData()
        );
    }

    private Long calculateBlockNonce(Block block) {
        String blockHeader = block.getCreationTime() + block.getPreviousBlockHash() + block.getHash();
        long nonce = 0;
        String proofHash = DigestUtils.sha256Hex(blockHeader + nonce);
        while (!isValidProof(proofHash)) {
            nonce += 1;
            proofHash = DigestUtils.sha256Hex(blockHeader + nonce);
        }
        return nonce;
    }

    private boolean isValidProof(String proofHash) {
        return proofHash.substring(0, COMPLEXITY).chars().allMatch(ch -> ch == '0');
    }

    private boolean isValidChain(List<Block> blocks) {
        for (int i = 1; i < blocks.size(); i++) {
            Block prevBlock = blocks.get(i - 1);
            Block currentBlock = blocks.get(i);
            if (!currentBlock.getPreviousBlockHash().equalsIgnoreCase(prevBlock.getHash())) {
                return false;
            }

            String blockHeader = currentBlock.getCreationTime() + currentBlock.getPreviousBlockHash() +
                    currentBlock.getHash();
            String proofHash = DigestUtils.sha256Hex(blockHeader + currentBlock.getNonce());
            if (!isValidProof(proofHash)) {
                return false;
            }
        }

        return true;
    }
}
