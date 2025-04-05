package com.example.vintagestore.util;

import android.content.Context;

import com.example.vintagestore.data.Repository;
import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating mock data for testing and development.
 */
public class MockDataUtil {
    
    /**
     * Populates the database with mock data
     * @param context Application context
     * @param repository Data repository
     */
    public static void populateWithMockData(Context context, Repository repository) {
        // Create mock users
        List<User> users = createMockUsers();
        for (User user : users) {
            repository.insertUser(user);
        }
        
        // Create mock items
        List<Item> items = createMockItems(users.size());
        for (Item item : items) {
            repository.insertItem(item);
        }
        
        // Create mock messages
        List<Message> messages = createMockMessages(users.size(), items.size());
        for (Message message : messages) {
            repository.insertMessage(message);
        }
    }
    
    /**
     * Creates a list of mock users.
     * @return List of mock User objects
     */
    private static List<User> createMockUsers() {
        List<User> users = new ArrayList<>();
        
        // Create 5 mock users
        User user1 = new User("Alice Johnson", "alice@example.com", "password123", "555-123-4567");
        user1.setProfileImage("");
        user1.setRating(4.8f);
        user1.setReviewCount(24);
        
        User user2 = new User("Bob Smith", "bob@example.com", "password123", "555-234-5678");
        user2.setProfileImage("");
        user2.setRating(4.5f);
        user2.setReviewCount(18);
        
        User user3 = new User("Charlie Davis", "charlie@example.com", "password123", "555-345-6789");
        user3.setProfileImage("");
        user3.setRating(4.9f);
        user3.setReviewCount(32);
        
        User user4 = new User("Diana Miller", "diana@example.com", "password123", "555-456-7890");
        user4.setProfileImage("");
        user4.setRating(4.7f);
        user4.setReviewCount(15);
        
        User user5 = new User("Ethan Wilson", "ethan@example.com", "password123", "555-567-8901");
        user5.setProfileImage("");
        user5.setRating(4.6f);
        user5.setReviewCount(21);
        
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        
        return users;
    }
    
    /**
     * Creates a list of mock items.
     * @param userCount Number of users to associate items with
     * @return List of mock Item objects
     */
    private static List<Item> createMockItems(int userCount) {
        List<Item> items = new ArrayList<>();
        Random random = new Random();
        
        if (userCount <= 0) {
            userCount = 1;
        }
        
        // Categories
        String[] categories = {"Tops", "Dresses", "Pants", "Jackets", "Accessories", "Shoes"};
        // Sizes
        String[] sizes = {"XS", "S", "M", "L", "XL", "XXL"};
        // Brands
        String[] brands = {"Levi's", "Nike", "Adidas", "Zara", "H&M", "Gucci", "Vintage"};
        // Conditions
        String[] conditions = {"New with tags", "Like new", "Good", "Fair", "Worn"};
        
        // Create 20 mock items
        for (int i = 0; i < 20; i++) {
            int sellerId = (i % userCount) + 1; // User IDs start from 1
            
            String category = categories[i % categories.length];
            String title = generateItemTitle(category, brands[i % brands.length]);
            String description = generateItemDescription(title, conditions[i % conditions.length]);
            double price = 10.0 + (i * 5.0); // Prices from $10 to $105
            String size = sizes[i % sizes.length];
            String brand = brands[i % brands.length];
            String condition = conditions[i % conditions.length];
            
            Item item = new Item(title, description, price, brand, size, condition, category, sellerId);
            
            // Set some items as favorites randomly
            item.setFavorite(random.nextBoolean());
            
            // Set some items as sold randomly
            item.setSold(i % 5 == 0);
            
            // Set a random creation date within the last year
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(365));
            item.setCreatedAt(cal.getTime());
            
            items.add(item);
        }
        
        return items;
    }
    
    /**
     * Creates a list of mock messages between users.
     * @param userCount Number of users to create messages for
     * @param itemCount Number of items to discuss in messages
     * @return List of mock Message objects
     */
    private static List<Message> createMockMessages(int userCount, int itemCount) {
        List<Message> messages = new ArrayList<>();
        Random random = new Random();
        
        if (userCount <= 1 || itemCount <= 0) {
            return messages;
        }
        
        // Create 15 mock messages
        for (int i = 0; i < 15; i++) {
            int senderId = (i % userCount) + 1; // User IDs start from 1
            int receiverId = ((i + 1) % userCount) + 1; // Ensure different from sender
            int itemId = (i % itemCount) + 1; // Item IDs start from 1
            
            String content = generateMessageText(i % 6, "Item " + itemId);
            
            // Create message with generated content
            Message message = new Message();
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setItemId(itemId);
            message.setContent(content);
            
            // Set some as read randomly
            message.setRead(random.nextBoolean());
            
            // Set a random timestamp within the last week
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -random.nextInt(168)); // Up to 7 days ago (168 hours)
            message.setTimestamp(cal.getTime());
            
            messages.add(message);
        }
        
        return messages;
    }
    
    /**
     * Generates a title for a mock item.
     * @param category The item category
     * @param brand The item brand
     * @return A generated item title
     */
    private static String generateItemTitle(String category, String brand) {
        String[] adjectives = {"Vintage", "Retro", "Classic", "Rare", "Unique", "Designer", "Premium"};
        String adjective = adjectives[(int) (Math.random() * adjectives.length)];
        
        switch (category) {
            case "Tops":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "T-Shirt" : "Blouse");
            case "Dresses":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "Summer Dress" : "Evening Gown");
            case "Pants":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "Jeans" : "Trousers");
            case "Jackets":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "Leather Jacket" : "Denim Jacket");
            case "Accessories":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "Scarf" : "Bag");
            case "Shoes":
                return adjective + " " + brand + " " + (Math.random() > 0.5 ? "Sneakers" : "Boots");
            default:
                return adjective + " " + brand + " Item";
        }
    }
    
    /**
     * Generates a description for a mock item.
     * @param title The item title
     * @param condition The item condition
     * @return A generated item description
     */
    private static String generateItemDescription(String title, String condition) {
        return title + " in " + condition + " condition. " +
                "This is a great addition to any wardrobe. " +
                "Please see photos for details and feel free to ask any questions.";
    }
    
    /**
     * Generates message text for mock conversations.
     * @param messageIndex The index of the message in the conversation
     * @param itemTitle The title of the item being discussed
     * @return Generated message text
     */
    private static String generateMessageText(int messageIndex, String itemTitle) {
        switch (messageIndex) {
            case 0:
                return "Hi, is this " + itemTitle + " still available?";
            case 1:
                return "Yes, it's still available! Are you interested in buying it?";
            case 2:
                return "Great! Would you consider $45 for it?";
            case 3:
                return "I can do $48. Does that work for you?";
            case 4:
                return "That sounds good. When can we meet for the exchange?";
            default:
                return "Let me know if you have any other questions about the " + itemTitle + ".";
        }
    }
}
