/**
 * Interface for loyalty offer operations.
 * 
 * This interface defines the contract for managing loyalty point offers
 * in the cafeteria system. It supports both admin operations (creating/managing offers)
 * and student operations (viewing/redeeming offers).
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.interfaces;

import com.university.cafeteria.model.LoyaltyOffer;
import java.util.List;

public interface ILoyaltyOfferOperations {
    /**
     * Creates a new loyalty offer for a menu item.
     * 
     * @param offer The loyalty offer to create
     * @return true if offer was created successfully, false otherwise
     */
    boolean createOffer(LoyaltyOffer offer);
    
    /**
     * Updates an existing loyalty offer.
     * 
     * @param offer The updated offer information
     * @return true if offer was updated successfully, false otherwise
     */
    boolean updateOffer(LoyaltyOffer offer);
    
    /**
     * Deactivates a loyalty offer.
     * 
     * @param offerId ID of the offer to deactivate
     * @return true if offer was deactivated successfully, false otherwise
     */
    boolean deactivateOffer(String offerId);
    
    /**
     * Activates a loyalty offer.
     * 
     * @param offerId ID of the offer to activate
     * @return true if offer was activated successfully, false otherwise
     */
    boolean activateOffer(String offerId);
    
    /**
     * Removes a loyalty offer completely.
     * 
     * @param offerId ID of the offer to remove
     * @return true if offer was removed successfully, false otherwise
     */
    boolean removeOffer(String offerId);
    
    /**
     * Gets all loyalty offers in the system.
     * 
     * @return List of all loyalty offers
     */
    List<LoyaltyOffer> getAllOffers();
    
    /**
     * Gets all active loyalty offers.
     * 
     * @return List of active loyalty offers
     */
    List<LoyaltyOffer> getActiveOffers();
    
    /**
     * Gets offers for a specific menu item.
     * 
     * @param menuItemId ID of the menu item
     * @return List of offers for the specified menu item
     */
    List<LoyaltyOffer> getOffersForMenuItem(String menuItemId);
    
    /**
     * Gets offers that a student can afford with their current points.
     * 
     * @param studentPoints Student's current loyalty points
     * @return List of affordable offers
     */
    List<LoyaltyOffer> getAffordableOffers(int studentPoints);
    
    /**
     * Gets a specific offer by ID.
     * 
     * @param offerId ID of the offer to retrieve
     * @return The loyalty offer, or null if not found
     */
    LoyaltyOffer getOfferById(String offerId);
    
    /**
     * Redeems a loyalty offer for a student.
     * 
     * @param offerId ID of the offer to redeem
     * @param studentId ID of the student redeeming the offer
     * @return true if redemption was successful, false otherwise
     */
    boolean redeemOffer(String offerId, String studentId);
}
