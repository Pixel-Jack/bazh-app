package fr.clement.rennsurrection.bluesound.SelectBase;

import android.bluetooth.BluetoothDevice;

/**
 * Interface to listen event click on the name of a device
 */
public interface DeviceAdapterListener {
    public void onClickDevice(BluetoothDevice item, int position);
}
