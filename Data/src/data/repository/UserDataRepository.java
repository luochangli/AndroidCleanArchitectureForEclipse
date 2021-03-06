/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package data.repository;

import java.util.Collection;

import data.entity.UserEntity;
import data.entity.mapper.UserEntityDataMapper;
import data.exception.RepositoryErrorBundle;
import data.exception.UserNotFoundException;
import data.repository.datasource.UserDataStore;
import data.repository.datasource.UserDataStoreFactory;
import domain.User;
import domain.repository.UserRepository;
import domain.repository.UserRepository.UserDetailsCallback;
import domain.repository.UserRepository.UserListCallback;

/**
 * {@link UserRepository} for retrieving user data.
 */
public class UserDataRepository implements UserRepository {

  private static UserDataRepository INSTANCE;

  public static synchronized UserDataRepository getInstance(UserDataStoreFactory dataStoreFactory,
      UserEntityDataMapper userEntityDataMapper) {
    if (INSTANCE == null) {
      INSTANCE = new UserDataRepository(dataStoreFactory, userEntityDataMapper);
    }
    return INSTANCE;
  }

  private final UserDataStoreFactory userDataStoreFactory;
  private final UserEntityDataMapper userEntityDataMapper;

  /**
   * Constructs a {@link UserRepository}.
   *
   * @param dataStoreFactory A factory to construct different data source implementations.
   * @param userEntityDataMapper {@link UserEntityDataMapper}.
   */
  protected UserDataRepository(UserDataStoreFactory dataStoreFactory,
      UserEntityDataMapper userEntityDataMapper) {
    if (dataStoreFactory == null || userEntityDataMapper == null) {
      throw new IllegalArgumentException("Invalid null parameters in constructor!!!");
    }
    this.userDataStoreFactory = dataStoreFactory;
    this.userEntityDataMapper = userEntityDataMapper;
  }

  /**
   * {@inheritDoc}
   *
   * @param userListCallback A {@link UserListCallback} used for notifying clients.
   */
  @Override public void getUserList(final UserListCallback userListCallback) {
    //we always get all users from the cloud
    final UserDataStore userDataStore = this.userDataStoreFactory.createCloudDataStore();
    userDataStore.getUsersEntityList(new UserDataStore.UserListCallback() {
      @Override public void onUserListLoaded(Collection<UserEntity> usersCollection) {
        Collection<User> users =
            UserDataRepository.this.userEntityDataMapper.transform(usersCollection);
        userListCallback.onUserListLoaded(users);
      }

      @Override public void onError(Exception exception) {
        userListCallback.onError(new RepositoryErrorBundle(exception));
      }
    });
  }

  /**
   * {@inheritDoc}
   *
   * @param userId The user id used to retrieve user data.
   * @param userCallback A {@link com.fernandocejas.android10.sample.domain.repository.UserRepository.UserDetailsCallback}
   * used for notifying clients.
   */
  @Override public void getUserById(final int userId, final UserDetailsCallback userCallback) {
    UserDataStore userDataStore = this.userDataStoreFactory.create(userId);
    userDataStore.getUserEntityDetails(userId, new UserDataStore.UserDetailsCallback() {
      @Override public void onUserEntityLoaded(UserEntity userEntity) {
        User user = UserDataRepository.this.userEntityDataMapper.transform(userEntity);
        if (user != null) {
          userCallback.onUserLoaded(user);
        } else {
          userCallback.onError(new RepositoryErrorBundle(new UserNotFoundException()));
        }
      }

      @Override public void onError(Exception exception) {
        userCallback.onError(new RepositoryErrorBundle(exception));
      }
    });
  }
}
