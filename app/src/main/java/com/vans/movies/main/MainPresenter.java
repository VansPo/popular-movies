package com.vans.movies.main;

import com.vans.movies.Global;
import com.vans.movies.ViewNotification;
import com.vans.movies.ViewNotification.State;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter {

  private DataSourceImpl api;
  private MainActivity view;

  private PublishSubject<Object> refreshSubject = PublishSubject.create();
  private BehaviorSubject<String> sortSubject = BehaviorSubject.create();
  private BehaviorSubject<ViewNotification> notificationSubject = BehaviorSubject.create();

  private CompositeSubscription subscription = new CompositeSubscription();

  public MainPresenter(MainActivity activity) {
    view = activity;
    api = Global.get().api;
  }

  public void init() {
    getMovies();

    subscription.add(Observable.combineLatest(
        sortSubject.distinctUntilChanged(),
        refreshSubject.startWith(0),
        new Func2<String, Object, String>() {
          @Override public String call(String s, Object o) {
            return s;
          }
        })
        .doOnNext(new Action1<String>() {
          @Override public void call(String s) {
            notificationSubject.onNext(new ViewNotification(s, State.PROGRESS));
          }
        })
        .debounce(200, TimeUnit.MILLISECONDS)
        .flatMap(new Func1<String, Observable<ListResponse>>() {
          @Override public Observable<ListResponse> call(String s) {
            return api.getMovies(s).onErrorReturn(new Func1<Throwable, ListResponse>() {
              @Override public ListResponse call(Throwable throwable) {
                notificationSubject.onNext(new ViewNotification(throwable, State.ERROR));
                return new ListResponse();
              }
            }).subscribeOn(Schedulers.io());
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ListResponse>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            notificationSubject.onNext(new ViewNotification(e, State.ERROR));
          }

          @Override public void onNext(ListResponse data) {
            if (data.results != null) {
              view.setData(data.results);
              notificationSubject.onNext(new ViewNotification(data, State.CONTENT));
            }
          }
        }));

    subscription.add(notificationSubject.subscribeOn(Schedulers.io())
            .debounce(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<ViewNotification>() {
              @Override public void call(ViewNotification viewNotification) {
                switch (viewNotification.state) {
                  case ERROR:
                    view.showError();
                    break;
                  case CONTENT:
                    view.showProgress(false);
                    break;
                  case PROGRESS:
                    view.showProgress(true);
                }
              }
            }));
  }

  public void destroy() {
    subscription.clear();
  }

  public void getMovies() {
    refreshSubject.onNext(0);
  }

  public void setSort(String sortBy) {
    sortSubject.onNext(sortBy);
  }
}
