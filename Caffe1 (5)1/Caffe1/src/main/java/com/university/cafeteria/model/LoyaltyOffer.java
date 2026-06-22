/**
 * LoyaltyOffer model representing point-based offers for menu items.
 * 
 * This class allows admins to create special offers where students can
 * redeem loyalty points for free items or discounted items.
 * 
 * Features:
 * - Point-based redemption for specific menu items
 * - Offer activation/deactivation by admins
 * - Support for both free items and discounted items
 * - Unique offer identification system
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.model;

public class LoyaltyOffer {
    /** Unique identifier for the offer */
    private String offerId;
    
    /** The menu item this offer applies to */
    private String menuItemId;
    
    /** Points required to redeem this offer */
    private int pointsRequired;
    
    /** Type of offer: "FREE" for free item, "DISCOUNT" for discounted item */
    private String offerType;
    
    /** Discount amount (0 for free items, positive value for discounts) */
    private double discountAmount;
    
    /** Whether this offer is currently active */
    private boolean isActive;
    
    /** Description of the offer for display purposes */
    private String description;
    
    /** Admin who created this offer */
    private String createdBy;
    
    /** Timestamp when offer was created */
    private long createdTimestamp;

    /**
     * Constructor for creating a new loyalty offer.
     * 
     * @param offerId Unique identifier for the offer
     * @param menuItemId ID of the menu item this offer applies to
     * @param pointsRequired Points needed to redeem this offer
     * @param offerType Type of offer ("FREE" or "DISCOUNT")
     * @param discountAmount Discount amount (0 for free items)
     * @param description Human-readable description of the offer
     * @param createdBy Admin username who created the offer
     */
    public LoyaltyOffer(String offerId, String menuItemId, int pointsRequired, 
                       String offerType, double discountAmount, String description, String createdBy) {
        this.offerId = offerId;
        this.menuItemId = menuItemId;
        this.pointsRequired = pointsRequired;
        this.offerType = offerType;
        this.discountAmount = discountAmount;
        this.description = description;
        this.createdBy = createdBy;
        this.isActive = true; // New offers are active by default
        this.createdTimestamp = System.currentTimeMillis();
    }

    // Getter methods
    public String getOfferId() { return offerId; }
    public String getMenuItemId() { return menuItemId; }
    public int getPointsRequired() { return pointsRequired; }
    public String getOfferType() { return offerType; }
    public double getDiscountAmount() { return discountAmount; }
    public boolean isActive() { return isActive; }
    public String getDescription() { return description; }
    public String getCreatedBy() { return createdBy; }
    public long getCreatedTimestamp() { return createdTimestamp; }

    // Setter methods for admin modifications
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public void setOfferType(String offerType) { this.offerType = offerType; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedTimestamp(long createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    /**
     * Checks if this is a free item offer.
     * @return true if offer type is FREE
     */
    public boolean isFreeItemOffer() {
        return "FREE".equalsIgnoreCase(offerType);
    }

    /**
     * Checks if this is a discount offer.
     * @return true if offer type is DISCOUNT
     */
    public boolean isDiscountOffer() {
        return "DISCOUNT".equalsIgnoreCase(offerType);
    }
}
