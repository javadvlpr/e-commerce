package com.example.onlineshop.service;

import com.example.onlineshop.entity.HistoryEntity;
import com.example.onlineshop.entity.OrderEntity;
import com.example.onlineshop.entity.ProductEntity;
import com.example.onlineshop.entity.UserEntity;
import com.example.onlineshop.entity.dto.CreateOrderDto;
import com.example.onlineshop.enums.OrderStatus;
import com.example.onlineshop.exceptions.DataNotFoundException;
import com.example.onlineshop.repository.OrderRepository;
import com.example.onlineshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProductService productService;
    private final HistoryService historyService;

    public List<OrderEntity> getAllOrders(int pageNumber, Model model) {
        Pageable pageable = PageRequest.of(pageNumber, 5);
        Page<OrderEntity> orderEntityPage = orderRepository.findAll(pageable);
        model.addAttribute("pages",orderEntityPage.getTotalPages());
        return orderEntityPage.getContent();
    }

    public List<OrderEntity> getOrdersByOwnerId(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new DataNotFoundException("User not found .orders")
        );
        return orderRepository.getOrdersByOwnerId(ownerId);
    }

    public boolean createOrder(CreateOrderDto createOrderDto) {
        ProductEntity product = productService.getProductById(createOrderDto.getProductId());
        UserEntity user = userService.getUserById(createOrderDto.getUserId());
        if (product != null && user != null) {
            OrderEntity order = OrderEntity.builder()
                    .orderStatus(OrderStatus.valueOf(OrderStatus.NEW.toString()))
                    .products(List.of(product))
                    .amount(createOrderDto.getAmount())
                    .user(user)
                    .price(createOrderDto.getAmount() * product.getPrice())
                    .build();
            orderRepository.save(order);
        }
        return false;
    }

    public boolean updateOrder(Long orderId, int amount) {
        Optional<OrderEntity> order = getOrderById(orderId);
        if (order.isPresent()) {
            order.get().setAmount(amount);
            order.get().setPrice(order.get().getAmount() * order.get().getProducts().get(0).getPrice());
            orderRepository.save(order.get());
            return true;
        }
        return false;
    }

    public Optional<OrderEntity> getOrderById(Long id) {
        return orderRepository.getOrderById(id);
    }

    public boolean deleteOrder(Long orderId, boolean isDeleting) {
        Optional<OrderEntity> orderById = orderRepository.getOrderById(orderId);
        if (orderById.isPresent()) {
            orderById.get().setDeleted(true);
            if (isDeleting) {
                productService.updateProductOnDeletingOrder(orderById.get().getProducts().get(0).getId(), orderById.get().getAmount());
            }
            orderRepository.save(orderById.get());
            return true;
        }
        return false;
    }

    public boolean updateOrderStatus(Long orderId, String orderStatus) {
        Optional<OrderEntity> orderById = orderRepository.getOrderById(orderId);
        if (orderById.isPresent()) {
            orderById.get().setOrderStatus(OrderStatus.valueOf(orderStatus));
            if (Objects.equals(orderStatus, "ACCEPTED")) {
                HistoryEntity historyEntity = HistoryEntity.builder()
                        .orderEntity(orderById.get())
                        .build();
                historyService.addHistory(historyEntity);
                orderById.get().setDeleted(true);
            } else if (Objects.equals(orderStatus, "REJECTED")) {
                deleteOrder(orderId, true);
                HistoryEntity historyEntity = HistoryEntity.builder()
                        .orderEntity(orderById.get())
                        .build();
                historyService.addHistory(historyEntity);
            }
            orderRepository.save(orderById.get());
            return true;
        }
        return false;
    }

}
