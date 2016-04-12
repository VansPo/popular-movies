package com.vans.movies.details;

import com.vans.movies.Global;
import com.vans.movies.data.DataSourceImpl;
import com.vans.movies.entity.ListResponse;
import com.vans.movies.entity.Trailer;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DetailsPresenter {

    private final String id;
    private final DetailsView view;
    private final DataSourceImpl api;
    private CompositeSubscription subscription = new CompositeSubscription();

    public DetailsPresenter(DetailsView view, String movieId) {
        this.id = movieId;
        api = Global.get().api;
        this.view = view;
    }

    public void init() {

        subscription.add(
                api.getTrailers(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ListResponse<Trailer>>() {
                        @Override
                        public void onCompleted() {}
                        @Override
                        public void onError(Throwable e) {}
                        @Override
                        public void onNext(ListResponse<Trailer> trailerListResponse) {
                            view.showTrailers(trailerListResponse.results);
                        }
                    })
        );
    }

    public void destroy() {
        subscription.clear();
    }
}
