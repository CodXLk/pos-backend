package com.codX.pos.service;

import com.codX.pos.dto.request.CreateVehicleRequest;
import com.codX.pos.dto.response.VehicleResponse;
import com.codX.pos.entity.VehicleEntity;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleEntity createVehicle(CreateVehicleRequest request);
    List<VehicleResponse> getVehiclesByCustomer(UUID customerId);
    List<VehicleResponse> getVehiclesByCompany(UUID companyId);
    List<VehicleResponse> getVehiclesByBranch(UUID branchId); // NEW METHOD
    List<VehicleResponse> getAllVehicles(); // NEW METHOD
    VehicleResponse getVehicleById(UUID id);
    VehicleResponse getVehicleByNumber(String vehicleNumber);
    List<VehicleResponse> searchVehiclesByNumber(String vehicleNumber);
    VehicleEntity updateVehicle(UUID id, CreateVehicleRequest request);
    void deactivateVehicle(UUID id);
}
