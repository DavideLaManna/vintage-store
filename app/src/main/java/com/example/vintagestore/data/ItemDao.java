package com.example.vintagestore.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vintagestore.model.Item;

import java.util.List;

/**
 * Data Access Object for the Item entity
 */
@Dao
public interface ItemDao {
    /**
     * Get all items sorted by creation date (newest first)
     */
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    LiveData<List<Item>> getAllItems();
    
    /**
     * Get all items from a specific category
     */
    @Query("SELECT * FROM items WHERE category = :category ORDER BY createdAt DESC")
    LiveData<List<Item>> getItemsByCategory(String category);
    
    /**
     * Get all items marked as favorites by a specific user
     */
    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    LiveData<List<Item>> getFavoriteItems(long userId);
    
    /**
     * Get items sold by a specific seller
     */
    @Query("SELECT * FROM items WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    LiveData<List<Item>> getItemsBySeller(long sellerId);
    
    /**
     * Search for items by title or description
     */
    @Query("SELECT * FROM items WHERE title LIKE :query OR description LIKE :query ORDER BY createdAt DESC")
    LiveData<List<Item>> searchItems(String query, String descriptionQuery);
    
    /**
     * Get a specific item by ID
     */
    @Query("SELECT * FROM items WHERE id = :itemId")
    LiveData<Item> getItemById(long itemId);
    
    /**
     * Update an item's favorite status
     */
    @Query("UPDATE items SET isFavorite = :isFavorite WHERE id = :itemId")
    int updateFavoriteStatus(long itemId, boolean isFavorite);
    
    /**
     * Update an item's sold status
     */
    @Query("UPDATE items SET sold = :isSold WHERE id = :itemId")
    int updateSoldStatus(long itemId, boolean isSold);
    
    /**
     * Insert a new item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Item item);
    
    /**
     * Update an existing item
     */
    @Update
    int update(Item item);
    
    /**
     * Delete an item
     */
    @Delete
    int delete(Item item);
}
