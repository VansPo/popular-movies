package com.vans.movies.main;

import com.vans.movies.Global;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
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

  private CompositeSubscription subscription = new CompositeSubscription();

  public MainPresenter(MainActivity activity) {
    view = activity;
    api = Global.get().api;
  }

  public void init() {
    getMovies();
    subscription.add(Observable.combineLatest(sortSubject.startWith(Global.POPULARITY_DESC),
        refreshSubject.startWith(0), new Func2<String, Object, String>() {
          @Override public String call(String s, Object o) {
            return s;
          }
        })
        .flatMap(new Func1<String, Observable<ListResponse>>() {
          @Override public Observable<ListResponse> call(String s) {
            return api.getMovies(s).subscribeOn(Schedulers.io());
          }
        })
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends ListResponse>>() {
          @Override public Observable<? extends ListResponse> call(Throwable throwable) {
            // throwable
            view.showError();
            return Observable.create(new Observable.OnSubscribe<ListResponse>() {
              @Override public void call(Subscriber<? super ListResponse> subscriber) {
                Observable.just(new ListResponse());
              }
            });
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ListResponse>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(ListResponse data) {
            view.setData(data.results);
          }
        }));
  }

  public void destroy() {
    subscription.clear();
  }

  public void getMovies() { refreshSubject.onNext(0); }

  public void setSort(String sortBy) {
    sortSubject.onNext(sortBy);
  }

}
