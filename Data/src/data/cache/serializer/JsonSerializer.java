/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package data.cache.serializer;

import com.google.gson.Gson;

import data.entity.UserEntity;

/**
 * Class user as Serializer/Deserializer for user entities.
 */
public class JsonSerializer {

  private final Gson gson = new Gson();

  public JsonSerializer() {
    //empty
  }

  /**
   * Serialize an object to Json.
   *
   * @param userEntity {@link UserEntity} to serialize.
   */
  public String serialize(UserEntity userEntity) {
    String jsonString = gson.toJson(userEntity, UserEntity.class);
    return jsonString;
  }

  /**
   * Deserialize a json representation of an object.
   *
   * @param jsonString A json string to deserialize.
   * @return {@link UserEntity}
   */
  public UserEntity deserialize(String jsonString) {
    UserEntity userEntity = gson.fromJson(jsonString, UserEntity.class);
    return userEntity;
  }
}
