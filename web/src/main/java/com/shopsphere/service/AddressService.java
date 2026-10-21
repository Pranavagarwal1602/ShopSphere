package com.shopsphere.service;

import com.shopsphere.dao.AddressDAO;
import com.shopsphere.model.Address;
import com.shopsphere.util.ValidationUtil;
import java.util.List;
import java.util.Optional;

public class AddressService {
    private final AddressDAO addressDAO;

    public AddressService() {
        this.addressDAO = new AddressDAO();
    }

    public AddressService(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public List<Address> getAddresses(int userId) {
        return addressDAO.findByUserId(userId);
    }

    public Optional<Address> getAddress(int addressId, int userId) {
        return addressDAO.findById(addressId, userId);
    }

    public Optional<Address> getDefaultAddress(int userId) {
        return addressDAO.findDefaultByUserId(userId);
    }

    public String saveAddress(Address address) {
        if (address.getAddressLine() == null || address.getAddressLine().trim().length() < 5) {
            return "Address line must be at least 5 characters.";
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            return "City is required.";
        }
        if (address.getState() == null || address.getState().trim().isEmpty()) {
            return "State is required.";
        }
        if (!ValidationUtil.isValidPincode(address.getPincode())) {
            return "Pincode must be a valid 6-digit postal code.";
        }

        boolean success;
        if (address.getAddressId() > 0) {
            success = addressDAO.update(address);
        } else {
            success = addressDAO.create(address);
        }
        return success ? null : "Failed to save address.";
    }

    public boolean deleteAddress(int addressId, int userId) {
        return addressDAO.delete(addressId, userId);
    }
}
