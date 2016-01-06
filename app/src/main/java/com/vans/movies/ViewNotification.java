package com.vans.movies;

public class ViewNotification<T> {
  public T data;
  public State state;
  public enum State {
    PROGRESS, CONTENT, ERROR
  }

  public ViewNotification(T data, State state) {
    this.data = data;
    this.state = state;
  }
}
