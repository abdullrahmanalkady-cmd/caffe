/**
 * LoyaltyOfferManager service class for managing loyalty point offers.
 * 
 * This service implements the ILoyaltyOfferOperations interface and provides
 * comprehensive functionality for creating, managing, and redeeming loyalty offers.
 * It integrates with the existing loyalty program and order system.
 * 
 * Features:
 * - Admin offer creation and management
 * - Student offer browsing and redemption
 * - Integration with existing loyalty points system
 * - Validation and error handling
 * - Offer analytics and reporting
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.service;

import com.university.cafeteria.interfaces.ILoyaltyOfferOperations;
import com.university.cafeteria.interfaces.ILoyaltyProgram;
import com.university.cafeteria.model.LoyaltyOffer;
import com.university.cafeteria.model.MenuItem;
import java.util.*;
import java.util.stream.Collectors;

public class LoyaltyOfferManager implements ILoyaltyOfferOperations {
    /** Storage for all loyalty offers */
    private Map<String, LoyaltyOffer> offers = new HashMap<>();
    
    /** Reference to loyalty program for points management */
    private ILoyaltyProgram loyaltyProgram;
    
    /** Reference to menu manager for menu item validation */
    private MenuManager menuManager;

    /**
     * Constructor initializes the manager and loads existing data.
     * 
     * @param loyaltyProgram Reference to loyalty program service
     * @param menuManager Reference to menu management service
     */
    public LoyaltyOfferManager(ILoyaltyProgram loyaltyProgram, MenuManager menuManager) {
        this.loyaltyProgram = loyaltyProgram;
        this.menuManager = menuManager;
        loadOfferData();
    }
    
    /**
     * Load loyalty offers from persistent storage
     */
    private void loadOfferData() {
        List<LoyaltyOffer> offerList = DataManager.loadLoyaltyOffers();
        offers.clear();
        for (LoyaltyOffer offer : offerList) {
            offers.put(offer.getOfferId(), offer);
        }
    }
    
    /**
     * Save loyalty offers to persistent storage
     */
    private void saveOfferData() {
        DataManager.saveLoyaltyOffers(new ArrayList<>(offers.values()));
    }

    /**
     * Initializes some default offers for demonstration purposes.
     */
    private void initializeDefaultOffers() {
        // Get some menu items for default offers
        List<MenuItem> menuItems = menuManager.getMenuItems();
        if (!menuItems.isEmpty()) {
            // Create a free coffee offer (assuming there's a drink)
            MenuItem drink = menuItems.stream()
                .filter(item -> item.getCategory().name().equals("DRINK"))
                .findFirst()
                .orElse(menuItems.get(0));
            
            LoyaltyOffer freeItemOffer = new LoyaltyOffer(
                UUID.randomUUID().toString(),
                drink.getId(),
                100, // 100 points for free item
                "FREE",
                0,
                "Free " + drink.getName() + " - 100 points",
                "system"
            );
            offers.put(freeItemOffer.getOfferId(), freeItemOffer);
            
            // Create a discount offer for expensive items
            MenuItem expensive = menuItems.stream()
                .max(Comparator.comparing(MenuItem::getPrice))
                .orElse(menuItems.get(0));
            
            LoyaltyOffer discountOffer = new LoyaltyOffer(
                UUID.randomUUID().toString(),
                expensive.getId(),
                75, // 75 points for 15 EGP discount
                "DISCOUNT",
                15.0,
                "15 EGP discount on " + expensive.getName() + " - 75 points",
                "system"
            );
            offers.put(discountOffer.getOfferId(), discountOffer);
        }
    }

    @Override
    public boolean createOffer(LoyaltyOffer offer) {
        if (offer == null || offers.containsKey(offer.getOfferId())) {
            return false;
        }
        
        // Validate that menu item exists
        MenuItem menuItem = menuManager.getMenuItemById(offer.getMenuItemId());
        if (menuItem == null) {
            return false;
        }
        
        // Validate offer parameters
        if (offer.getPointsRequired() <= 0) {
            return false;
        }
        
        if (offer.isDiscountOffer() && offer.getDiscountAmount() <= 0) {
            return false;
        }
        
        offers.put(offer.getOfferId(), offer);
        saveOfferData();
        return true;
    }

    @Override
    public boolean updateOffer(LoyaltyOffer offer) {
        if (offer == null || !offers.containsKey(offer.getOfferId())) {
            return false;
        }
        
        // Validate updated parameters
        if (offer.getPointsRequired() <= 0) {
            return false;
        }
        
        if (offer.isDiscountOffer() && offer.getDiscountAmount() <= 0) {
            return false;
        }
        
        offers.put(offer.getOfferId(), offer);
        saveOfferData();
        return true;
    }

    @Override
    public boolean deactivateOffer(String offerId) {
        LoyaltyOffer offer = offers.get(offerId);
        if (offer != null) {
            offer.setActive(false);
            saveOfferData();
            return true;
        }
        return false;
    }

    @Override
    public boolean activateOffer(String offerId) {
        LoyaltyOffer offer = offers.get(offerId);
        if (offer != null) {
            offer.setActive(true);
            saveOfferData();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeOffer(String offerId) {
        return offers.remove(offerId) != null;
    }

    @Override
    public List<LoyaltyOffer> getAllOffers() {
        return new ArrayList<>(offers.values());
    }

    @Override
    public List<LoyaltyOffer> getActiveOffers() {
        return offers.values().stream()
            .filter(LoyaltyOffer::isActive)
            .collect(Collectors.toList());
    }

    @Override
    public List<LoyaltyOffer> getOffersForMenuItem(String menuItemId) {
        return offers.values().stream()
            .filter(offer -> offer.getMenuItemId().equals(menuItemId) && offer.isActive())
            .collect(Collectors.toList());
    }

    @Override
    public List<LoyaltyOffer> getAffordableOffers(int studentPoints) {
        return getActiveOffers().stream()
            .filter(offer -> offer.getPointsRequired() <= studentPoints)
            .collect(Collectors.toList());
    }

    @Override
    public LoyaltyOffer getOfferById(String offerId) {
        return offers.get(offerId);
    }

    @Override
    public boolean redeemOffer(String offerId, String studentId) {
        LoyaltyOffer offer = offers.get(offerId);
        if (offer == null || !offer.isActive()) {
            return false;
        }
        
        // Check if student has enough points
        int studentPoints = loyaltyProgram.getPointsBalance(studentId);
        if (studentPoints < offer.getPointsRequired()) {
            return false;
        }
        
        // Deduct points from student's balance
        return loyaltyProgram.redeemPoints(studentId, offer.getPointsRequired());
    }

    /**
     * Gets the menu item associated with an offer.
     * 
     * @param offer The loyalty offer
     * @return The associated menu item, or null if not found
     */
    public MenuItem getMenuItemForOffer(LoyaltyOffer offer) {
        return menuManager.getMenuItemById(offer.getMenuItemId());
    }

    /**
     * Calculates the final price for an item after applying an offer.
     * 
     * @param offer The loyalty offer being applied
     * @return The final price (0 for free items, discounted price for discount offers)
     */
    public double calculateOfferPrice(LoyaltyOffer offer) {
        MenuItem menuItem = getMenuItemForOffer(offer);
        if (menuItem == null) {
            return 0;
        }
        
        if (offer.isFreeItemOffer()) {
            return 0;
        } else if (offer.isDiscountOffer()) {
            return Math.max(0, menuItem.getPrice() - offer.getDiscountAmount());
        }
        
        return menuItem.getPrice();
    }

    /**
     * Gets offers grouped by menu item for easier browsing.
     * 
     * @return Map of menu item ID to list of offers
     */
    public Map<String, List<LoyaltyOffer>> getOffersGroupedByMenuItem() {
        return getActiveOffers().stream()
            .collect(Collectors.groupingBy(LoyaltyOffer::getMenuItemId));
    }
}
