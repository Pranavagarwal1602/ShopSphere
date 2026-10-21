package com.shopsphere.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

/**
 * RMI Server Launcher.
 * Programmatically starts or attaches to RMI Registry on port 1099,
 * and binds the InventoryRemoteService stub.
 */
public class RmiServerMain {
    private static final Logger LOGGER = Logger.getLogger(RmiServerMain.class.getName());
    public static final int RMI_PORT = 1099;
    public static final String BINDING_NAME = "ShopSphereInventoryService";

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting Java RMI Registry on port " + RMI_PORT + "...");
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
                LOGGER.info("Created new embedded RMI Registry on port " + RMI_PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(RMI_PORT);
                LOGGER.info("Connected to existing RMI Registry on port " + RMI_PORT);
            }

            InventoryRemoteService service = new InventoryRemoteServiceImpl();
            registry.rebind(BINDING_NAME, service);

            LOGGER.info(">>> ShopSphere InventoryService successfully bound as '" + BINDING_NAME + "' in RMI Registry.");
            LOGGER.info("RMI Server is ready for remote invocations. Press Ctrl+C to terminate.");

            // Keep main thread alive
            Thread.currentThread().join();

        } catch (Exception e) {
            LOGGER.severe("Error starting RMI Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
