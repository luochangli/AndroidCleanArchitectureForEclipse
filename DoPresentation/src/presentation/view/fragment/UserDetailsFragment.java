/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package presentation.view.fragment;

import presentation.UIThread;
import presentation.mapper.UserModelDataMapper;
import presentation.model.UserModel;
import presentation.presenter.UserDetailsPresenter;
import presentation.view.UserDetailsView;
import presentation.view.component.AutoLoadImageView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dong.dopresentation.R;

import data.cache.FileManager;
import data.cache.UserCache;
import data.cache.UserCacheImpl;
import data.cache.serializer.JsonSerializer;
import data.entity.mapper.UserEntityDataMapper;
import data.executor.JobExecutor;
import data.repository.UserDataRepository;
import data.repository.datasource.UserDataStoreFactory;
import domain.executor.PostExecutionThread;
import domain.executor.ThreadExecutor;
import domain.interactor.GetUserDetailsUseCase;
import domain.interactor.GetUserDetailsUseCaseImpl;
import domain.repository.UserRepository;

/**
 * Fragment that shows details of a certain user.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class UserDetailsFragment extends BaseFragment implements UserDetailsView {

  private static final String ARGUMENT_KEY_USER_ID = "org.android10.ARGUMENT_USER_ID";

  private int userId;
  private UserDetailsPresenter userDetailsPresenter;

  private AutoLoadImageView iv_cover;
  private TextView tv_fullname;
  private TextView tv_email;
  private TextView tv_followers;
  private TextView tv_description;
  private RelativeLayout rl_progress;
  private RelativeLayout rl_retry;
  private Button bt_retry;

  public UserDetailsFragment() { super(); }

  @SuppressLint("NewApi") public static UserDetailsFragment newInstance(int userId) {
    UserDetailsFragment userDetailsFragment = new UserDetailsFragment();

    Bundle argumentsBundle = new Bundle();
    argumentsBundle.putInt(ARGUMENT_KEY_USER_ID, userId);
    userDetailsFragment.setArguments(argumentsBundle);

    return userDetailsFragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.initialize();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_user_details, container, false);

    this.iv_cover = (AutoLoadImageView) fragmentView.findViewById(R.id.iv_cover);
    this.tv_fullname = (TextView) fragmentView.findViewById(R.id.tv_fullname);
    this.tv_email = (TextView) fragmentView.findViewById(R.id.tv_email);
    this.tv_followers = (TextView) fragmentView.findViewById(R.id.tv_followers);
    this.tv_description = (TextView) fragmentView.findViewById(R.id.tv_description);
    this.rl_progress = (RelativeLayout) fragmentView.findViewById(R.id.rl_progress);
    this.rl_retry = (RelativeLayout) fragmentView.findViewById(R.id.rl_retry);
    this.bt_retry = (Button) fragmentView.findViewById(R.id.bt_retry);
    this.bt_retry.setOnClickListener(this.retryOnClickListener);

    return fragmentView;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    this.userDetailsPresenter.initialize(this.userId);
  }

  @Override public void onResume() {
    super.onResume();
    this.userDetailsPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    this.userDetailsPresenter.pause();
  }

  @Override void initializePresenter() {
    // All these dependency initialization could have been avoided using a
    // dependency injection framework. But in this case are used this way for
    // LEARNING EXAMPLE PURPOSE.
    ThreadExecutor threadExecutor = JobExecutor.getInstance();
    PostExecutionThread postExecutionThread = UIThread.getInstance();

    JsonSerializer userCacheSerializer = new JsonSerializer();
    UserCache userCache = UserCacheImpl.getInstance(getActivity(), userCacheSerializer,
        FileManager.getInstance(), threadExecutor);
    UserDataStoreFactory userDataStoreFactory =
        new UserDataStoreFactory(this.getContext(), userCache);
    UserEntityDataMapper userEntityDataMapper = new UserEntityDataMapper();
    UserRepository userRepository = UserDataRepository.getInstance(userDataStoreFactory,
        userEntityDataMapper);

    GetUserDetailsUseCase getUserDetailsUseCase = new GetUserDetailsUseCaseImpl(userRepository,
        threadExecutor, postExecutionThread);
    UserModelDataMapper userModelDataMapper = new UserModelDataMapper();

    this.userDetailsPresenter =
        new UserDetailsPresenter(this, getUserDetailsUseCase, userModelDataMapper);
  }

  @Override public void renderUser(UserModel user) {
    if (user != null) {
      this.iv_cover.setImageUrl(user.getCoverUrl());
      this.tv_fullname.setText(user.getFullName());
      this.tv_email.setText(user.getEmail());
      this.tv_followers.setText(String.valueOf(user.getFollowers()));
      this.tv_description.setText(user.getDescription());
    }
  }

  @Override public void showLoading() {
    this.rl_progress.setVisibility(View.VISIBLE);
    this.getActivity().setProgressBarIndeterminateVisibility(true);
  }

  @Override public void hideLoading() {
    this.rl_progress.setVisibility(View.GONE);
    this.getActivity().setProgressBarIndeterminateVisibility(false);
  }

  @Override public void showRetry() {
    this.rl_retry.setVisibility(View.VISIBLE);
  }

  @Override public void hideRetry() {
    this.rl_retry.setVisibility(View.GONE);
  }

  @Override public void showError(String message) {
    this.showToastMessage(message);
  }

  @Override public Context getContext() {
    return getActivity().getApplicationContext();
  }

  /**
   * Initializes fragment's private members.
   */
  private void initialize() {
    this.userId = getArguments().getInt(ARGUMENT_KEY_USER_ID);
  }

  /**
   * Loads all users.
   */
  private void loadUserDetails() {
    if (this.userDetailsPresenter != null) {
      this.userDetailsPresenter.initialize(this.userId);
    }
  }

  private final View.OnClickListener retryOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View view) {
      UserDetailsFragment.this.loadUserDetails();
    }
  };
}
