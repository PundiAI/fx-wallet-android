package com.pundix.core.fxcore;

import com.pundix.core.model.BridgeModel;
import com.pundix.core.model.GenesisModel;
import com.pundix.core.model.UnLockModel;

import java.util.concurrent.Callable;

import io.functionx.Client;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName: NoneService
 * @Description:
 * @Author: YT
 * @CreateDate: 2021/5/28 2:39 PM
 */
public class FunctionXService {

    private static FunctionXService noneService;

    public static FunctionXService getInstance() {
        if (noneService == null) {
            noneService = new FunctionXService();
        }
        return noneService;

    }

    public Disposable getGenesis(String url, Consumer<GenesisModel> success, Consumer<Throwable> error) {
        return Observable.fromCallable(new Callable<GenesisModel>() {
            @Override
            public GenesisModel call() throws Exception {
                return getGenesis(url);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(success, error);
    }

    public Disposable getEthBridgeToken(String chainId, String url, String hrp, String denom, Consumer<BridgeModel> success, Consumer<Throwable> error) {
        return Observable.fromCallable(() -> {
            final Client client = buildClient(chainId, url, hrp);
            final BridgeModel ethBridgeToken = getEthBridgeToken(client, denom.toLowerCase());
            return ethBridgeToken;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(success, error);
    }
}
