package com.shopsphere.rmi;

import com.shopsphere.dto.InventoryDTO;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Client tester program for RMI lookup and remote method execution.
 */
public class RmiClientMain {
    public static void main(String[] args) {
        String host = "localhost";
        int port = RmiServerMain.RMI_PORT;

        System.out.println("Connecting to RMI Registry at " + host + ":" + port + "...");

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            System.out.println("Looking up service: '" + RmiServerMain.BINDING_NAME + "'...");
            InventoryRemoteService service = (InventoryRemoteService) registry.lookup(RmiServerMain.BINDING_NAME);

            System.out.println(">>> RMI Proxy Stub Acquired Successfully!");

            // 1. Remote call getProduct(1)
            System.out.println("\n[Remote Call 1]: getProduct(1)");
            InventoryDTO prod = service.getProduct(1);
            System.out.println("Remote Response: " + prod);

            // 2. Remote call getStock(1)
            System.out.println("\n[Remote Call 2]: getStock(1)");
            int stock = service.getStock(1);
            System.out.println("Remote Response: Stock = " + stock);

            // 3. Remote call updateStock(1, stock + 5)
            System.out.println("\n[Remote Call 3]: updateStock(1, " + (stock + 5) + ")");
            boolean updated = service.updateStock(1, stock + 5);
            System.out.println("Remote Response: Stock Updated? " + updated);

            // Verify update
            int newStock = service.getStock(1);
            System.out.println("Verified Stock via RMI: " + newStock);

            System.out.println("\nAll RMI Remote Invocations completed successfully!");

        } catch (Exception e) {
            System.err.println("RMI Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
