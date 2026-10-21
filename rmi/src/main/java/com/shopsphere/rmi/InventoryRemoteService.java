package com.shopsphere.rmi;

import com.shopsphere.dto.InventoryDTO;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface extending java.rmi.Remote.
 * Outlines remote method signatures throwing java.rmi.RemoteException.
 */
public interface InventoryRemoteService extends Remote {

    InventoryDTO getProduct(int productId) throws RemoteException;

    int getStock(int productId) throws RemoteException;

    boolean updateStock(int productId, int newQuantity) throws RemoteException;
}
