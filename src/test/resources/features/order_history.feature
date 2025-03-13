Feature: Customer Order History and Refund Requests
  As a customer
  I want to view my order history and submit refund requests with evidence
  So that I can track my purchases and request refunds for problematic products

  Background:
    Given I am a customer with ID "customer123"
    And I have placed orders in the past

  Scenario: View order history sorted by date
    When I request to view my order history
    Then I should see a list of my orders sorted by date
    And each order should contain its items and status
    And the orders should be sorted from newest to oldest

  Scenario: View detailed order information
    When I request to view my order history
    Then each order should show:
      | Field          | Required |
      | Order Date     | Yes      |
      | Status         | Yes      |
      | Product Names  | Yes      |
      | Quantities     | Yes      |
      | Prices        | Yes      |

  Scenario: Submit refund request with evidence
    Given I have an order with ID "1" containing an item with ID "101"
    When I submit a refund request for item "101" with:
      | description                  | evidenceImageUrl           |
      | Product arrived defective    | /evidence/defect1.jpg     |
    Then the refund request should be recorded
    And the order item status should be updated to "PENDING"
    And the refund request should contain:
      | Field             | Value                    |
      | Description       | Product arrived defective|
      | Evidence Image    | /evidence/defect1.jpg    |
      | Status           | PENDING                   |

  Scenario: Cannot submit multiple refund requests for same item
    Given I have an order with ID "1" containing an item with ID "101"
    And I have already submitted a refund request for item "101"
    When I try to submit another refund request for item "101" with:
      | description              | evidenceImageUrl       |
      | Second refund attempt    | /evidence/defect2.jpg |
    Then I should receive an error indicating "Refund request already exists"
    And the original refund request should remain unchanged

  Scenario: Refund request requires evidence and description
    Given I have an order with ID "1" containing an item with ID "101"
    When I try to submit a refund request for item "101" with:
      | description | evidenceImageUrl |
      |            |                  |
    Then I should receive an error indicating "Description and evidence image are required"
    And no refund request should be created
