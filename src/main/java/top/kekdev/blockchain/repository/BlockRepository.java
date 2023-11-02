package top.kekdev.blockchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kekdev.blockchain.model.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Block findTopByOrderByIndexDesc();
}
