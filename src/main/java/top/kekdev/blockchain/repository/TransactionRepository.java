package top.kekdev.blockchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kekdev.blockchain.model.Transaction;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
