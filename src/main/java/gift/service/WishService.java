package gift.service;

import gift.domain.Product;
import gift.domain.User;
import gift.domain.Wish;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.exception.ProductNotFoundException;
import gift.exception.UserNotFoundException;
import gift.exception.WishNotFoundException;
import gift.repository.ProductRepository;
import gift.repository.UserRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final String NOT_FOUND_USER_BY_EMAIL_MESSAGE = "해당 email의 유저가 존재하지 않습니다.";
    private final String NOT_FOUND_PRODUCT_BY_NAME_MESSAGE = "해당 이름의 상품이 존재하지 않습니다.";
    private final String NOT_FOUND_USER_WISHLIST_MESSAGE = "해당 유저의 위시리스트가 존재하지 않습니다.";

    public WishService(WishRepository wishRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public void save(String userEmail, WishRequestDto request){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_USER_BY_EMAIL_MESSAGE));
        Product product = productRepository.findByName(request.getProductName())
                .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND_PRODUCT_BY_NAME_MESSAGE));
        wishRepository.save(new Wish(user.getId(),product.getId(), request.getQuantity()));
    }

    public List<WishResponseDto> findByUserEmail(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_USER_BY_EMAIL_MESSAGE));
        return wishRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WishNotFoundException(NOT_FOUND_USER_WISHLIST_MESSAGE))
                .stream()
                .map(this::convertToWishDto)
                .collect(Collectors.toList());
    }

    private WishResponseDto convertToWishDto(Wish wish) {
        Product product = productRepository.findById(wish.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND_PRODUCT_BY_NAME_MESSAGE));
        return new WishResponseDto(
                wish.getId(),
                wish.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                wish.getQuantity()
        );
    }
}
