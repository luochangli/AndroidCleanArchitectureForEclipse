/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package data.entity.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import data.entity.UserEntity;
import domain.User;

/**
 * Mapper class used to transform {@link UserEntity} (in the data layer) to {@link User} in the
 * domain layer.
 */
public class UserEntityDataMapper {

  public UserEntityDataMapper() {
    //empty
  }

  /**
   * Transform a {@link UserEntity} into an {@link User}.
   *
   * @param userEntity Object to be transformed.
   * @return {@link User} if valid {@link UserEntity} otherwise null.
   */
  public User transform(UserEntity userEntity) {
    User user = null;
    if (userEntity != null) {
      user = new User(userEntity.getUserId());
      user.setCoverUrl(userEntity.getCoverUrl());
      user.setFullName(userEntity.getFullname());
      user.setDescription(userEntity.getDescription());
      user.setFollowers(userEntity.getFollowers());
      user.setEmail(userEntity.getEmail());
    }

    return user;
  }

  /**
   * Transform a Collection of {@link UserEntity} into a Collection of {@link User}.
   *
   * @param userEntityCollection Object Collection to be transformed.
   * @return {@link User} if valid {@link UserEntity} otherwise null.
   */
  public Collection<User> transform(Collection<UserEntity> userEntityCollection) {
    List<User> userList = new ArrayList<User>(20);
    User user;
    for (UserEntity userEntity : userEntityCollection) {
      user = transform(userEntity);
      if (user != null) {
        userList.add(user);
      }
    }

    return userList;
  }
}
