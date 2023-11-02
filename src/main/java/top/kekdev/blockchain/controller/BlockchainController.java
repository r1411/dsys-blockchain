package top.kekdev.blockchain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kekdev.blockchain.dto.BlockResponseDto;
import top.kekdev.blockchain.dto.wrapper.ListResponseEntity;
import top.kekdev.blockchain.dto.wrapper.SuccessResponseEntity;
import top.kekdev.blockchain.model.Block;
import top.kekdev.blockchain.service.BlockchainService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/blockchain")
public class BlockchainController {
    private final BlockchainService blockchainService;

    @Autowired
    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @PostMapping("/createBlock")
    public SuccessResponseEntity<BlockResponseDto> createBlock(@RequestBody Map<String, Object> params) {
        Block block = blockchainService.createBlock(params.get("data").toString());
        return new SuccessResponseEntity<>(BlockResponseDto.fromBlock(block));
    }

    @GetMapping("/getBlocks")
    public SuccessResponseEntity<ListResponseEntity<BlockResponseDto>> getBlocks(@RequestParam Optional<Integer> count) {
        List<Block> resultBlocks;
        if (count.isPresent()) {
            resultBlocks = blockchainService.getLastNBlocks(count.get());
        } else {
            resultBlocks = blockchainService.getAllBlocks();
        }
        List<BlockResponseDto> resultDtos = resultBlocks.stream()
                .map(BlockResponseDto::fromBlock)
                .toList();

        return new SuccessResponseEntity<>(new ListResponseEntity<>(resultDtos));
    }
}
