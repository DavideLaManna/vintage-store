package com.example.vintagestore.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;
import com.example.vintagestore.util.ErrorHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository class that abstracts access to data sources
 */
public class Repository {
    private static final String TAG = "Repository";
    
    private final AppDatabase database;
    private final ExecutorService executorService;
    private static Repository instance;
    
    // Result classes for indicating success or failure
    public static class Result<T> {
        private T data;
        private Exception error;
        
        private Result(T data) {
            this.data = data;
        }
        
        private Result(Exception error) {
            this.error = error;
        }
        
        public static <T> Result<T> success(T data) {
            return new Result<>(data);
        }
        
        public static <T> Result<T> error(Exception error) {
            return new Result<>(error);
        }
        
        public boolean isSuccess() {
            return error == null;
        }
        
        public T getData() {
            return data;
        }
        
        public Exception getError() {
            return error;
        }
    }
    
    private Repository(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public static synchronized Repository getInstance(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return instance;
    }
    
    // User related operations
    public LiveData<List<User>> getAllUsers() {
        return database.userDao().getAllUsers();
    }
    
    public LiveData<User> getUserById(long userId) {
        return database.userDao().getUserById(userId);
    }
    
    public LiveData<Result<Long>> insertUser(User user, Context context) {
        MutableLiveData<Result<Long>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                long id = database.userDao().insert(user);
                resultLiveData.postValue(Result.success(id));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    public LiveData<Result<Integer>> updateUser(User user, Context context) {
        MutableLiveData<Result<Integer>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                int rowsAffected = database.userDao().update(user);
                resultLiveData.postValue(Result.success(rowsAffected));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    // Item related operations
    public LiveData<List<Item>> getAllItems() {
        return database.itemDao().getAllItems();
    }
    
    public LiveData<List<Item>> getFavoriteItems(long userId) {
        return database.itemDao().getFavoriteItems(userId);
    }
    
    public LiveData<List<Item>> getItemsByCategory(String category) {
        return database.itemDao().getItemsByCategory(category);
    }
    
    public LiveData<Item> getItemById(long itemId) {
        return database.itemDao().getItemById(itemId);
    }
    
    public LiveData<Result<Long>> insertItem(Item item, Context context) {
        MutableLiveData<Result<Long>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                long id = database.itemDao().insert(item);
                resultLiveData.postValue(Result.success(id));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    public LiveData<Result<Integer>> updateItem(Item item, Context context) {
        MutableLiveData<Result<Integer>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                int rowsAffected = database.itemDao().update(item);
                resultLiveData.postValue(Result.success(rowsAffected));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    public LiveData<Result<Integer>> toggleFavorite(long itemId, boolean isFavorite, Context context) {
        MutableLiveData<Result<Integer>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                int rowsAffected = database.itemDao().updateFavoriteStatus(itemId, isFavorite);
                resultLiveData.postValue(Result.success(rowsAffected));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    // Search operations
    public LiveData<List<Item>> searchItems(String query) {
        String searchQuery = "%" + query + "%";
        return database.itemDao().searchItems(searchQuery, searchQuery);
    }
    
    // Message related operations
    public LiveData<List<Message>> getMessagesByUserId(long userId) {
        return database.messageDao().getMessagesByUserId(userId);
    }
    
    public LiveData<List<Message>> getChatMessages(long senderId, long receiverId) {
        return database.messageDao().getChatMessages(senderId, receiverId);
    }
    
    public LiveData<Result<Long>> sendMessage(Message message, Context context) {
        MutableLiveData<Result<Long>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                long id = database.messageDao().insert(message);
                resultLiveData.postValue(Result.success(id));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    // Authentication simulation (in a real app, this would connect to an auth service)
    public LiveData<Result<User>> login(String email, String password, Context context) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                User user = database.userDao().getUserByEmailAndPassword(email, password);
                if (user != null) {
                    resultLiveData.postValue(Result.success(user));
                } else {
                    resultLiveData.postValue(Result.error(new Exception("Invalid credentials")));
                }
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    public LiveData<Result<User>> register(User user, Context context) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();
        
        executorService.execute(() -> {
            try {
                // Check if user already exists
                User existingUser = database.userDao().getUserByEmail(user.getEmail());
                if (existingUser != null) {
                    resultLiveData.postValue(Result.error(new Exception("Email already in use")));
                    return;
                }
                
                // Insert new user
                long userId = database.userDao().insert(user);
                User newUser = database.userDao().getUserById(userId).getValue();
                resultLiveData.postValue(Result.success(newUser));
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
                resultLiveData.postValue(Result.error(e));
            }
        });
        
        return resultLiveData;
    }
    
    // Helper method to clear database (for testing or logout)
    public void clearDatabase(Context context) {
        executorService.execute(() -> {
            try {
                database.clearAllTables();
                Log.d(TAG, "Database cleared successfully");
            } catch (Exception e) {
                ErrorHandler.handleDatabaseError(TAG, e, context);
            }
        });
    }
}
