package com.ordermanagement.bdd;

import com.ordermanagement.domain.entity.*;
import com.ordermanagement.service.order.OrderService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderHistorySteps {

    @Autowired
    private OrderService orderService;

    private String customerId;
    private List<Order> orderHistory;
    private Long orderItemId;

    @Given("I am a customer with ID {string}")
    public void iAmACustomerWithId(String id) {
        this.customerId = id;
    }

    @Given("I have placed orders in the past")
    public void iHavePlacedOrdersInThePast() {
        // Test data will be set up in a separate test configuration
    }

    @When("I request to view my order history")
    public void iRequestToViewMyOrderHistory() {
        orderHistory = orderService.getCustomerOrderHistory(customerId);
    }

    @Then("I should see a list of my orders sorted by date")
    public void iShouldSeeAListOfMyOrdersSortedByDate() {
        assertNotNull(orderHistory);
        assertFalse(orderHistory.isEmpty());
        
        // Verify orders are sorted by date descending
        for (int i = 0; i < orderHistory.size() - 1; i++) {
            assertTrue(orderHistory.get(i).getOrderDate()
                .isAfter(orderHistory.get(i + 1).getOrderDate()));
        }
    }

    @Then("each order should contain its items and status")
    public void eachOrderShouldContainItsItemsAndStatus() {
        for (Order order : orderHistory) {
            assertNotNull(order.getItems());
            assertFalse(order.getItems().isEmpty());
            assertNotNull(order.getStatus());
        }
    }

    @Given("I have an order with ID {string} containing an item with ID {string}")
    public void iHaveAnOrderWithIdContainingAnItemWithId(String orderId, String itemId) {
        this.orderItemId = Long.parseLong(itemId);
    }

    @When("I submit a refund request for item {string} with:")
    public void iSubmitARefundRequestForItemWith(String itemId, io.cucumber.datatable.DataTable dataTable) {
        var data = dataTable.entries().get(0);
        String description = data.get("description");
        String evidenceImageUrl = data.get("evidenceImageUrl");
        
        orderService.submitRefundRequest(Long.parseLong(itemId), description, evidenceImageUrl);
    }

    @Then("the refund request should be recorded with status {string}")
    public void theRefundRequestShouldBeRecordedWithStatus(String status) {
        Order order = orderService.getOrderById(1L); // Using known test data ID
        OrderItem item = order.getItems().stream()
            .filter(i -> i.getId().equals(orderItemId))
            .findFirst()
            .orElseThrow();
            
        assertNotNull(item.getRefundRequest());
        assertEquals(RefundStatus.valueOf(status), item.getRefundRequest().getStatus());
        assertEquals(OrderStatus.DELIVERED, order.getStatus(), "Order status should remain DELIVERED when refund is requested");
    }

    @Then("the refund request should contain the provided evidence and description")
    public void theRefundRequestShouldContainTheProvidedEvidenceAndDescription() {
        Order order = orderService.getOrderById(1L); // Using known test data ID
        OrderItem item = order.getItems().stream()
            .filter(i -> i.getId().equals(orderItemId))
            .findFirst()
            .orElseThrow();
            
        RefundRequest refundRequest = item.getRefundRequest();
        assertNotNull(refundRequest);
        assertNotNull(refundRequest.getDescription(), "Refund request should have a description");
        assertNotNull(refundRequest.getEvidenceImageUrl(), "Refund request should have evidence");
        assertNotNull(refundRequest.getRequestDate(), "Refund request should have a request date");
    }
}
