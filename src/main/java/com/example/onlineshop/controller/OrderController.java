package com.example.onlineshop.controller;

import com.example.onlineshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller()
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/get-all/{page}")
    public String getAllOrders(Model model, @PathVariable Integer page){
        model.addAttribute("orders",orderService.getAllOrders(page-1,model));
        return "ordersList";
    }
    @PostMapping("/change-status")
    public String changeStatus(Long id, Model model, @RequestParam("status") String status){
        orderService.updateOrderStatus(id,status);
        model.addAttribute("orders",orderService.getAllOrders(0,model));
        return "ordersList";
    }

}
