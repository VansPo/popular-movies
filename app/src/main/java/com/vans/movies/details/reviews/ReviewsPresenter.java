package com.vans.movies.details.reviews;

import android.util.Log;

import com.vans.movies.Global;
import com.vans.movies.ViewNotification;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
import com.vans.movies.entity.Review;

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

public class ReviewsPresenter {

    private final DataSourceImpl api;
    private final ReviewsActivity view;
    private final String id;

    private PublishSubject<Object> refreshSubject = PublishSubject.create();
    private PublishSubject<Object> paginationSubject = PublishSubject.create();
    private BehaviorSubject<ViewNotification> notificationSubject = BehaviorSubject.create();

    private CompositeSubscription subscription = new CompositeSubscription();

    private final int DEFAULT_PAGE = 1;
    private int page = DEFAULT_PAGE;
    private int totalPages = DEFAULT_PAGE;


    public ReviewsPresenter(ReviewsActivity reviewsActivity, String id) {
        api = Global.get().api;
        view = reviewsActivity;
        this.id = id;
    }

    public void init() {
        getReviews();

        subscription.add(
                paginationSubject
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .zipWith(
                                notificationSubject.filter(new Func1<ViewNotification, Boolean>() {
                                    @Override
                                    public Boolean call(ViewNotification viewNotification) {
                                        return viewNotification.state == ViewNotification.State.CONTENT || viewNotification.state == ViewNotification.State.ERROR;
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
                        .startWith(0)
                        .mergeWith(
                                refreshSubject.startWith(0)
                                        .doOnNext(new Action1<Object>() {
                                            @Override
                                            public void call(Object o) {
                                                notificationSubject.onNext(new ViewNotification(o, ViewNotification.State.PROGRESS));
                                            }
                                        })
                        )
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .flatMap(new Func1<Object, Observable<ListResponse<Review>>>() {
                            @Override
                            public Observable<ListResponse<Review>> call(Object o) {
                                return api.getReviews(id, page).onErrorReturn(new Func1<Throwable, ListResponse<Review>>() {
                                    @Override
                                    public ListResponse<Review> call(Throwable throwable) {
                                        notificationSubject.onNext(new ViewNotification(throwable, ViewNotification.State.ERROR));
                                        return new ListResponse<>();
                                    }
                                }).subscribeOn(Schedulers.io());
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ListResponse<Review>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                notificationSubject.onNext(new ViewNotification(e, ViewNotification.State.ERROR));
                            }

                            @Override
                            public void onNext(ListResponse<Review> data) {
                                if (data.results != null) {
                                    totalPages = data.totalPages;
                                    Log.i("movies", "setData: " + data.results.size());
                                    if (data.page == DEFAULT_PAGE)
                                        view.clearData();
                                    view.setData(data.results);
                                    notificationSubject.onNext(new ViewNotification(data, ViewNotification.State.CONTENT));
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

    public void getReviews() {
        resetPages();
        refreshSubject.onNext(0);
    }

    public void nextPage() {
        paginationSubject.onNext(0);
    }

    private void resetPages() {
        page = DEFAULT_PAGE;
    }
}
