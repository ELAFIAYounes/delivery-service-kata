Feature: Customer Order History and Refund Requests
  As a customer
  I want to view my order history and submit refund requests
  So that I can manage my purchases and request refunds when needed

  Scenario: View order history
    Given I am a customer with ID "customer123"
    And I have placed orders in the past
    When I request to view my order history
    Then I should see a list of my orders sorted by date
    And each order should contain its items and status

  Scenario: Submit refund request for an order item
    Given I am a customer with ID "customer123"
    And I have an order with ID "1" containing an item with ID "101"
    When I submit a refund request for item "101" with:
      | description          | evidenceImageUrl     |
      | Damaged on arrival   | /images/damage.jpg   |
    Then the refund request should be recorded
    And the order item status should be updated to "REFUND_REQUESTED"
