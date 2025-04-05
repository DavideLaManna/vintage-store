package com.example.vintagestore.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entity class representing a clothing item listing
 */
@Entity(tableName = "items",
        foreignKeys = @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "sellerId",
            onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("sellerId")})
public class Item {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private double price;
    private String size;
    private String brand;
    private String category;
    private String condition;
    private String color;
    private String location;
    private String[] imageUrls;
    private long sellerId;
    private boolean sold;
    private Date createdAt;
    private Date updatedAt;
    private boolean isFavorite;
    
    // Required by Room
    public Item() {
    }
    
    @Ignore
    public Item(String title, String description, double price, String size, String brand,
                String category, String condition, String color, String location,
                String[] imageUrls, long sellerId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.size = size;
        this.brand = brand;
        this.category = category;
        this.condition = condition;
        this.color = color;
        this.location = location;
        this.imageUrls = imageUrls;
        this.sellerId = sellerId;
        this.sold = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isFavorite = false;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String[] getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }
    
    public boolean isSold() {
        return sold;
    }
    
    public void setSold(boolean sold) {
        this.sold = sold;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    // Helper method to format the price for display
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }
    
    // Get the main image URL or a placeholder
    public String getMainImageUrl() {
        if (imageUrls != null && imageUrls.length > 0) {
            return imageUrls[0];
        }
        return "placeholder_image";
    }
    
    // Update the timestamp before saving changes
    public void updateTimestamp() {
        this.updatedAt = new Date();
    }
}
