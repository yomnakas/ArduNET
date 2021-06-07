package com.clj.fastble.callback;


import com.clj.fastble.exception.BleException;
import com.clj.fastble.exception.OtherException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);


}
