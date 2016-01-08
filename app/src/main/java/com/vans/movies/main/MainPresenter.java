package com.vans.movies.main;

import android.util.Log;

import com.vans.movies.Global;
import com.vans.movies.ViewNotification;
import com.vans.movies.ViewNotification.State;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
import com.vans.movies.entity.Movie;

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
    private PublishSubject<Object> paginationSubject = PublishSubject.create();
    private BehaviorSubject<String> sortSubject = BehaviorSubject.create();
    private BehaviorSubject<ViewNotification> notificationSubject = BehaviorSubject.create();

    private CompositeSubscription subscription = new CompositeSubscription();

    private final int DEFAULT_PAGE = 1;
    private int page = DEFAULT_PAGE;
    private int totalPages = DEFAULT_PAGE;

    public MainPresenter(MainActivity activity) {
        view = activity;
        api = Global.get().api;
    }

    public void init() {
        getMovies();

        //that's why lambdas matter
        subscription.add(
                Observable.combineLatest(
                        paginationSubject
                                .debounce(200, TimeUnit.MILLISECONDS)
                                .zipWith(
                                        notificationSubject.filter(new Func1<ViewNotification, Boolean>() {
                                            @Override
                                            public Boolean call(ViewNotification viewNotification) {
                                                return viewNotification.state == State.CONTENT || viewNotification.state == State.ERROR;
                                            }
                                        }),
                                        new Func2<Object, ViewNotification, Object>() {
                                            @Override
                                            public Object call(Object o, ViewNotification viewNotification) {
                                                return null;
                                            }
                                        })
                                .filter(new Func1<Object, Boolean>() {
                                    @Override
                                    public Boolean call(Object o) {
                                        return page <= totalPages;
                                    }
                                })
                                .startWith(0),
                        Observable.combineLatest(
                                sortSubject.distinctUntilChanged(),
                                refreshSubject.startWith(0),
                                new Func2<String, Object, String>() {
                                    @Override
                                    public String call(String s, Object o) {
                                        return s;
                                    }
                                }
                        )
                                .doOnNext(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        notificationSubject.onNext(new ViewNotification(s, State.PROGRESS));
                                    }
                                }),
                        new Func2<Object, String, String>() {
                            @Override
                            public String call(Object o, String s) {
                                return s;
                            }
                        }
                )
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .flatMap(new Func1<String, Observable<ListResponse<Movie>>>() {
                            @Override
                            public Observable<ListResponse<Movie>> call(String s) {
                                return api.getMovies(s, page).onErrorReturn(new Func1<Throwable, ListResponse<Movie>>() {
                                    @Override
                                    public ListResponse<Movie> call(Throwable throwable) {
                                        notificationSubject.onNext(new ViewNotification(throwable, State.ERROR));
                                        return new ListResponse<>();
                                    }
                                }).subscribeOn(Schedulers.io());
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ListResponse<Movie>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                notificationSubject.onNext(new ViewNotification(e, State.ERROR));
                            }

                            @Override
                            public void onNext(ListResponse<Movie> data) {
                                if (data.results != null) {
                                    totalPages = data.totalPages;
                                    Log.i("movies", "setData: " + data.results.size());
                                    if (data.page == DEFAULT_PAGE)
                                        view.clearData();
                                    view.setData(data.results);
                                    notificationSubject.onNext(new ViewNotification(data, State.CONTENT));
                                    page++;
                                }
                            }
                        })
        );

        subscription.add(notificationSubject.subscribeOn(Schedulers.io())
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ViewNotification>() {
                    @Override
                    public void call(ViewNotification viewNotification) {
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
        resetPages();
        refreshSubject.onNext(0);
    }

    public void nextPage() {
//        page++;
        paginationSubject.onNext(0);
    }

    public void setSort(String sortBy) {
        resetPages();
        sortSubject.onNext(sortBy);
    }

    private void resetPages() {
        page = DEFAULT_PAGE;
    }
}
