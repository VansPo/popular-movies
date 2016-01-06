package com.vans.movies.main;

import com.vans.movies.Global;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
  private BehaviorSubject<Throwable> errorSubject = BehaviorSubject.create();

  private CompositeSubscription subscription = new CompositeSubscription();

  public MainPresenter(MainActivity activity) {
    view = activity;
    api = Global.get().api;
  }

  public void init() {
    getMovies();

    subscription.add(Observable.combineLatest(
        sortSubject.startWith(Global.POPULARITY_DESC)
            .distinctUntilChanged()
            .debounce(200, TimeUnit.MILLISECONDS),
        refreshSubject
            .startWith(0)
            .debounce(200, TimeUnit.MILLISECONDS),
        new Func2<String, Object, String>() {
          @Override public String call(String s, Object o) {
            return s;
          }
        })
        .onBackpressureDrop()
        .flatMap(new Func1<String, Observable<ListResponse>>() {
          @Override public Observable<ListResponse> call(String s) {
            return api.getMovies(s).onErrorReturn(new Func1<Throwable, ListResponse>() {
              @Override public ListResponse call(Throwable throwable) {
                errorSubject.onNext(throwable);
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
            errorSubject.onNext(e);
          }

          @Override public void onNext(ListResponse data) {
            if (data.results != null) view.setData(data.results);
          }
        }));

    subscription.add(
        errorSubject.subscribeOn(Schedulers.io())
            .debounce(200, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Throwable>() {
          @Override public void onCompleted() {}
          @Override public void onError(Throwable e) {}
          @Override public void onNext(Throwable throwable) {
            throwable.printStackTrace();
            view.showError();
          }
        })
    );
  }

  public void destroy() {
    subscription.clear();
  }

  public void getMovies() {
    view.showProgress(true);
    refreshSubject.onNext(0);
  }

  public void setSort(String sortBy) {
    view.showProgress(true);
    sortSubject.onNext(sortBy);
  }
}
