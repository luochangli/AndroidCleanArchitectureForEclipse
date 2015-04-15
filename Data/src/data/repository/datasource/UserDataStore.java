/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package data.repository.datasource;

import java.util.Collection;

import data.entity.UserEntity;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface UserDataStore {
  /**
   * Callback used for clients to be notified when either a user list has been loaded or any error
   * occurred.
   */
  interface UserListCallback {
    void onUserListLoaded(Collection<UserEntity> usersCollection);

    void onError(Exception exception);
  }

  /**
   * Callback used for clients to be notified when either user data has been retrieved successfully
   * or any error occurred.
   */
  interface UserDetailsCallback {
    void onUserEntityLoaded(UserEntity userEntity);

    void onError(Exception exception);
  }

  /**
   * Get a collection of {@link com.fernandocejas.android10.sample.domain.User}.
   *
   * @param userListCallback A {@link UserListCallback} used for notifying clients.
   */
  void getUsersEntityList(UserListCallback userListCallback);

  /**
   * Get a {@link UserEntity} by its id.
   *
   * @param id The id to retrieve user data.
   * @param userDetailsCallback A {@link UserDetailsCallback} for notifications.
   */
  void getUserEntityDetails(int id, UserDetailsCallback userDetailsCallback);
}
