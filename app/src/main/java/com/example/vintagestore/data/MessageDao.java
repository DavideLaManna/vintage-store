package com.example.vintagestore.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vintagestore.model.Message;

import java.util.List;

@Dao
public interface MessageDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Message message);
    
    @Update
    void update(Message message);
    
    @Delete
    void delete(Message message);
    
    @Query("SELECT * FROM messages WHERE id = :id")
    Message getMessageById(int id);
    
    @Query("SELECT * FROM messages WHERE (sender_id = :userId OR receiver_id = :userId) ORDER BY timestamp DESC")
    List<Message> getMessagesByUser(int userId);
    
    @Query("SELECT * FROM messages WHERE (sender_id = :userId OR receiver_id = :userId) ORDER BY timestamp DESC")
    LiveData<List<Message>> getMessagesByUserLive(int userId);
    
    @Query("SELECT * FROM messages WHERE ((sender_id = :user1Id AND receiver_id = :user2Id) OR (sender_id = :user2Id AND receiver_id = :user1Id)) ORDER BY timestamp ASC")
    List<Message> getConversation(int user1Id, int user2Id);
    
    @Query("SELECT * FROM messages WHERE ((sender_id = :user1Id AND receiver_id = :user2Id) OR (sender_id = :user2Id AND receiver_id = :user1Id)) ORDER BY timestamp ASC")
    LiveData<List<Message>> getConversationLive(int user1Id, int user2Id);
    
    @Query("SELECT * FROM messages WHERE item_id = :itemId ORDER BY timestamp ASC")
    List<Message> getMessagesByItem(int itemId);
    
    @Query("UPDATE messages SET is_read = 1 WHERE receiver_id = :userId AND is_read = 0")
    void markAllAsRead(int userId);
    
    @Query("SELECT COUNT(*) FROM messages WHERE receiver_id = :userId AND is_read = 0")
    int getUnreadMessageCount(int userId);
}