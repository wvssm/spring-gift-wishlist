package gift.controller;

import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.service.JwtUtil;
import gift.service.WishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishes")
public class WishController {
    private final WishService wishService;
    private final JwtUtil jwtUtil;

    public WishController(WishService wishService, JwtUtil jwtUtil) {
        this.wishService = wishService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Void> addWish(@RequestHeader("Authorization") String token, @RequestBody WishRequestDto wishRequest){
        wishService.save(jwtUtil.extractEmail(token), wishRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<WishResponseDto>> getWishList(@RequestHeader("Authorization") String token){
        return ResponseEntity.status(HttpStatus.OK).body(wishService.findByUserEmail(jwtUtil.extractEmail(token)));
    }

}
