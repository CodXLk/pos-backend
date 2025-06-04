package com.codX.pos.service.impl;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import com.codX.pos.dto.request.CreateVehicleRequest;
import com.codX.pos.dto.response.VehicleResponse;
import com.codX.pos.entity.Role;
import com.codX.pos.entity.VehicleEntity;
import com.codX.pos.exception.UnauthorizedException;
import com.codX.pos.repository.VehicleRepository;
import com.codX.pos.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleEntity createVehicle(CreateVehicleRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.POS_USER && currentUser.role() != Role.BRANCH_ADMIN &&
                currentUser.role() != Role.COMPANY_ADMIN && currentUser.role() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Insufficient permissions to create vehicle");
        }

        // Check if vehicle number already exists in the company
        if (vehicleRepository.existsByVehicleNumberAndCompanyId(request.vehicleNumber(), currentUser.companyId())) {
            throw new RuntimeException("Vehicle with number " + request.vehicleNumber() + " already exists");
        }

        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleNumber(request.vehicleNumber())
                .make(request.make())
                .model(request.model())
                .year(request.year())
                .color(request.color())
                .engineNumber(request.engineNumber())
                .chassisNumber(request.chassisNumber())
                .customerId(request.customerId())
                .companyId(currentUser.companyId())
                .branchId(currentUser.branchId())
                .isActive(true)
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<VehicleResponse> getVehiclesByCustomer(UUID customerId) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<VehicleEntity> vehicles = vehicleRepository.findByCustomerIdAndCompanyIdAndIsActiveTrue(
                customerId, currentUser.companyId());

        return vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getVehiclesByCompany(UUID companyId) {
        UserContextDto currentUser = UserContext.getUserContext();

        if (currentUser.role() != Role.SUPER_ADMIN && !currentUser.companyId().equals(companyId)) {
            throw new UnauthorizedException("Access denied to company vehicles");
        }

        List<VehicleEntity> vehicles = vehicleRepository.findByCompanyIdAndIsActiveTrue(companyId);

        return vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        VehicleEntity vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        return mapToResponse(vehicle);
    }

    @Override
    public VehicleResponse getVehicleByNumber(String vehicleNumber) {
        UserContextDto currentUser = UserContext.getUserContext();

        VehicleEntity vehicle = vehicleRepository.findByVehicleNumberAndCompanyIdAndIsActiveTrue(
                        vehicleNumber, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> searchVehiclesByNumber(String vehicleNumber) {
        UserContextDto currentUser = UserContext.getUserContext();

        List<VehicleEntity> vehicles = vehicleRepository.searchByVehicleNumber(vehicleNumber, currentUser.companyId());

        return vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleEntity updateVehicle(UUID id, CreateVehicleRequest request) {
        UserContextDto currentUser = UserContext.getUserContext();

        VehicleEntity vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setColor(request.color());
        vehicle.setEngineNumber(request.engineNumber());
        vehicle.setChassisNumber(request.chassisNumber());

        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deactivateVehicle(UUID id) {
        UserContextDto currentUser = UserContext.getUserContext();

        VehicleEntity vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, currentUser.companyId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    private VehicleResponse mapToResponse(VehicleEntity vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .engineNumber(vehicle.getEngineNumber())
                .chassisNumber(vehicle.getChassisNumber())
                .customerId(vehicle.getCustomerId())
                .createdDate(vehicle.getCreatedDate())
                .build();
    }
}
