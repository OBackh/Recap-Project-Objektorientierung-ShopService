import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopService {
    private ProductRepo productRepo = new ProductRepo();
    private OrderRepo orderRepo = new OrderMapRepo();

    public Order addOrder(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product productToOrder = productRepo.getProductById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Produkt mit der ID " + productId + " existiert nicht!"));

            products.add(productToOrder);
            }

        Order newOrder = new Order(UUID.randomUUID().toString(), products, OrderStatus.PROCESSING);

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> listAllOrdersByStatus(OrderStatus status){
        return orderRepo.getOrders().stream().filter(order -> order.status().equals(status)).collect(Collectors.toList());
    }

    public Optional<Order> updateOrder(String orderId, OrderStatus newStatus) {
        // Search Order by orderId
        Order existingOrder = orderRepo.getOrders().stream()
                .filter(order -> order.id().equals(orderId))
                .findFirst()
                .orElse(null);
        // Check if order exists
        if (existingOrder == null ) {
            System.out.println("Keine Bestellung mit der ID " + orderId + " gefunden");
            return Optional.empty();
        }
        // Update the status of the order
        Order updatedOrder = existingOrder.withStatus(newStatus);
        //Optional: save the new status of the order if the method addOrder saves the status
        orderRepo.addOrder(updatedOrder);
        // return updated order
        return Optional.of(updatedOrder);

    }



}
